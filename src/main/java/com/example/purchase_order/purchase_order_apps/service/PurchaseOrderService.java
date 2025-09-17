package com.example.purchase_order.purchase_order_apps.service;

import com.example.purchase_order.purchase_order_apps.entity.Item;
import com.example.purchase_order.purchase_order_apps.entity.PurchaseOrderDetail;
import com.example.purchase_order.purchase_order_apps.entity.PurchaseOrderHeader;
import com.example.purchase_order.purchase_order_apps.repository.ItemRepository;
import com.example.purchase_order.purchase_order_apps.repository.PurchaseOrderDetailRepository;
import com.example.purchase_order.purchase_order_apps.repository.PurchaseOrderHeaderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseOrderService {
    private final PurchaseOrderHeaderRepository pohRepo;
    private final PurchaseOrderDetailRepository podRepo;
    private final ItemRepository itemRepo;

    public PurchaseOrderService(PurchaseOrderHeaderRepository pohRepo, PurchaseOrderDetailRepository podRepo, ItemRepository itemRepo) {
        this.pohRepo = pohRepo; this.podRepo = podRepo; this.itemRepo = itemRepo;
    }

    public List<PurchaseOrderHeader> getAll() { return pohRepo.findAll(); }

    public Optional<PurchaseOrderHeader> getById(Long id) { return pohRepo.findById(id); }

    @Transactional
    public PurchaseOrderHeader save(PurchaseOrderHeader header) {
        if (header.getDetails()==null || header.getDetails().isEmpty())
            throw new RuntimeException("PO details required");
        int totalPrice = 0, totalCost = 0;
        for (PurchaseOrderDetail d : header.getDetails()) {
            Item item = itemRepo.findById(d.getItem().getId())
                    .orElseThrow(() -> new RuntimeException("Item not found: "+d.getItem().getId()));
            d.setItem(item);
            d.setPoHeader(header);
            if (d.getItemQty()<=0) throw new RuntimeException("itemQty must be > 0");
            if (d.getItemPrice()<0 || d.getItemCost()<0) throw new RuntimeException("price/cost invalid");
            totalPrice += d.getItemQty()*d.getItemPrice();
            totalCost  += d.getItemQty()*d.getItemCost();
        }
        header.setTotalPrice(totalPrice);
        header.setTotalCost(totalCost);
        header.setUpdatedDateTime(LocalDateTime.now());
        if (header.getCreatedDateTime()==null) header.setCreatedDateTime(LocalDateTime.now());
        return pohRepo.save(header);
    }

    @Transactional
    public PurchaseOrderHeader update(Long id, PurchaseOrderHeader incoming) {
        PurchaseOrderHeader header = pohRepo.findById(id).orElseThrow(() -> new RuntimeException("PO not found"));
        header.setDateTime(incoming.getDateTime());
        header.setDescription(incoming.getDescription());
        header.getDetails().clear();
        header.getDetails().addAll(incoming.getDetails());
        for (PurchaseOrderDetail d : header.getDetails()) d.setPoHeader(header);
        return save(header); 
    }

    public void delete(Long id) { pohRepo.deleteById(id); }
}

