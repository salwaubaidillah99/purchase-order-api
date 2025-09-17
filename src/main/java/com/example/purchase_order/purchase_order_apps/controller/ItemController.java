package com.example.purchase_order.purchase_order_apps.controller;

import com.example.purchase_order.purchase_order_apps.entity.Item;
import com.example.purchase_order.purchase_order_apps.repository.ItemRepository;
import com.example.purchase_order.purchase_order_apps.response.ApiResponse;
import com.example.purchase_order.purchase_order_apps.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@SecurityRequirement(name = "bearerAuth")
public class ItemController {

    private final ItemService is;
    private final ItemRepository itemRepo;

    public ItemController(ItemService is, ItemRepository itemRepo) {
        this.is = is;
        this.itemRepo = itemRepo;
    }

    @Operation(summary = "Get items", description = "Ambil semua item atau berdasarkan ID")
    @GetMapping
    public ResponseEntity<ApiResponse> getItems(@RequestParam(required = false) Long id) {
        if (id != null) {
            return is.getItemById(id)
                    .map(item -> ResponseEntity.ok(new ApiResponse(200, "Success", item)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse(404, "Item not found", null)));
        }
        List<Item> items = is.getAllItems();
        return ResponseEntity.ok(new ApiResponse(200, "Success", items));
    }

    @Operation(summary = "Create item", description = "Buat item baru")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody @Valid Item body, HttpServletRequest http) {
        String audit = (String) http.getAttribute("authAuditName");
        if (audit == null) audit = "system";
        body.setCreatedBy(audit);
        body.setUpdatedBy(audit);
        body.setCreatedDatetime(LocalDateTime.now());
        body.setUpdatedDatetime(LocalDateTime.now());
        var saved = itemRepo.save(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(201, "Item created", saved));
    }

    @Operation(summary = "Update item", description = "Update item berdasarkan ID")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id,
                                              @RequestBody @Valid Item body,
                                              HttpServletRequest http) {
        String audit = (String) http.getAttribute("authAuditName");
        if (audit == null) audit = "system";
        var it = itemRepo.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        it.setName(body.getName());
        it.setDescription(body.getDescription());
        it.setPrice(body.getPrice());
        it.setCost(body.getCost());
        it.setUpdatedBy(audit);
        it.setUpdatedDatetime(LocalDateTime.now());
        var saved = itemRepo.save(it);
        return ResponseEntity.ok(new ApiResponse(200, "Item updated", saved));
    }

    @Operation(summary = "Delete item", description = "Hapus item berdasarkan ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteItem(@PathVariable Long id) {
        try {
            is.deleteItem(id);
            return ResponseEntity.ok(new ApiResponse(200, "Item deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(404, "Item not found", null));
        }
    }
}