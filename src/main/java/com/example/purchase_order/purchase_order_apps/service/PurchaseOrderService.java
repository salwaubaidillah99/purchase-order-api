package com.example.purchase_order.purchase_order_apps.service;

import com.example.purchase_order.purchase_order_apps.entity.Item;
import com.example.purchase_order.purchase_order_apps.entity.PurchaseOrderDetail;
import com.example.purchase_order.purchase_order_apps.entity.PurchaseOrderHeader;
import com.example.purchase_order.purchase_order_apps.entity.User;
import com.example.purchase_order.purchase_order_apps.entity.dto.PoDetailRequest;
import com.example.purchase_order.purchase_order_apps.entity.dto.PoDetailResponse;
import com.example.purchase_order.purchase_order_apps.entity.dto.PoHeaderRequest;
import com.example.purchase_order.purchase_order_apps.entity.dto.PoHeaderResponse;
import com.example.purchase_order.purchase_order_apps.repository.ItemRepository;
import com.example.purchase_order.purchase_order_apps.repository.PurchaseOrderDetailRepository;
import com.example.purchase_order.purchase_order_apps.repository.PurchaseOrderHeaderRepository;
import com.example.purchase_order.purchase_order_apps.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderHeaderRepository pohRepo;
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;

    public PurchaseOrderService(PurchaseOrderHeaderRepository pohRepo,
                                PurchaseOrderDetailRepository podRepo,
                                ItemRepository itemRepo,
                                UserRepository userRepo) {
        this.pohRepo = pohRepo;
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
    }

    public List<PurchaseOrderHeader> getAll() { return pohRepo.findAll(); }

    public Optional<PurchaseOrderHeader> getById(Long id) { return pohRepo.findById(id); }

    @Transactional
    public PoHeaderResponse create(PoHeaderRequest req, String user, Long authUserId) {
        PurchaseOrderHeader h = new PurchaseOrderHeader();
        h.setDateTime(req.getDateTime() != null ? req.getDateTime() : LocalDateTime.now());
        h.setDescription(req.getDescription());
        h.setCreatedBy(user);
        h.setCreatedDateTime(LocalDateTime.now()); // ensure created set

        if (req.getDetails() == null || req.getDetails().isEmpty())
            throw new RuntimeException("PO details required");

        if (authUserId != null) {
            User u = userRepo.findById(authUserId) // now Long
                    .orElseThrow(() -> new RuntimeException("User not found: " + authUserId));
            h.setUser(u);
        }

        int totalPrice = 0, totalCost = 0;
        List<PurchaseOrderDetail> details = new ArrayList<>();
        for (PoDetailRequest r : req.getDetails()) {
            Item item = itemRepo.findById(r.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found: " + r.getItemId()));
            PurchaseOrderDetail d = new PurchaseOrderDetail();
            d.setPoHeader(h);
            d.setItem(item);
            d.setItemQty(r.getQty());
            d.setItemCost(r.getCost());
            d.setItemPrice(r.getPrice());
            d.setCreatedBy(user);
            d.setCreatedDatetime(LocalDateTime.now());
            // DO NOT set updated* on create
            details.add(d);

            totalPrice += r.getQty() * r.getPrice();
            totalCost  += r.getQty() * r.getCost();
        }
        h.setDetails(details);
        h.setTotalPrice(totalPrice);
        h.setTotalCost(totalCost);

        PurchaseOrderHeader saved = pohRepo.saveAndFlush(h);
        return toResponse(saved);
    }


    @Transactional
    public PoHeaderResponse update(Long id, PoHeaderRequest req, String user) {
        PurchaseOrderHeader h = pohRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("PO not found"));

        h.setDateTime(req.getDateTime() != null ? req.getDateTime() : h.getDateTime());
        h.setDescription(req.getDescription());
        h.setUpdatedBy(user);
        h.setUpdatedDateTime(LocalDateTime.now());

        Map<Long, PurchaseOrderDetail> existingById = h.getDetails().stream()
                .filter(d -> d.getId() != null)
                .collect(Collectors.toMap(PurchaseOrderDetail::getId, d -> d));

        List<PurchaseOrderDetail> newState = new ArrayList<>();
        int totalPrice = 0, totalCost = 0;

        for (PoDetailRequest r : req.getDetails()) {
            Item item = itemRepo.findById(r.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found: " + r.getItemId()));

            PurchaseOrderDetail d;
            if (r.getId() != null) {
                d = existingById.remove(r.getId());
                if (d == null) throw new RuntimeException("Detail id not found: " + r.getId());
                d.setItem(item);
                d.setItemQty(r.getQty());
                d.setItemCost(r.getCost());
                d.setItemPrice(r.getPrice());
                d.setUpdatedBy(user);
                d.setUpdatedDatetime(LocalDateTime.now());
            } else {
                d = new PurchaseOrderDetail();
                d.setPoHeader(h);
                d.setItem(item);
                d.setItemQty(r.getQty());
                d.setItemCost(r.getCost());
                d.setItemPrice(r.getPrice());
                d.setCreatedBy(h.getCreatedBy());
                d.setCreatedDatetime(LocalDateTime.now());
                d.setUpdatedBy(user);
                d.setUpdatedDatetime(LocalDateTime.now());
            }
            newState.add(d);

            totalPrice += r.getQty() * r.getPrice();
            totalCost  += r.getQty() * r.getCost();
        }

        Set<Long> toRemove = existingById.keySet();
        h.getDetails().removeIf(d -> d.getId() != null && toRemove.contains(d.getId()));

        // Replace state
        h.getDetails().clear();
        for (PurchaseOrderDetail d : newState) {
            d.setPoHeader(h);
            h.getDetails().add(d);
        }

        h.setTotalPrice(totalPrice);
        h.setTotalCost(totalCost);

        PurchaseOrderHeader saved = pohRepo.saveAndFlush(h);
        return toResponse(saved);
    }


    private PoHeaderResponse toResponse(PurchaseOrderHeader h) {
        List<PoDetailResponse> det = h.getDetails().stream()
                .map(d -> new PoDetailResponse(
                        d.getId(),
                        d.getItem().getId(),
                        d.getItem().getName(),
                        d.getItemQty(),
                        d.getItemCost(),
                        d.getItemPrice()))
                .toList();
        return new PoHeaderResponse(
                h.getId(), h.getDateTime(), h.getDescription(),
                h.getTotalPrice(), h.getTotalCost(), det);
    }
    public void delete(Long id) { pohRepo.deleteById(id);
    }

}
