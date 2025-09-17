package com.example.purchase_order.purchase_order_apps.controller;

import com.example.purchase_order.purchase_order_apps.entity.PurchaseOrderHeader;
import com.example.purchase_order.purchase_order_apps.response.ApiResponse;
import com.example.purchase_order.purchase_order_apps.service.PurchaseOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/po")
public class PurchaseOrderController {
    private final PurchaseOrderService ps;

    public PurchaseOrderController(PurchaseOrderService ps) { this.ps = ps; }

    @GetMapping
    public ResponseEntity<ApiResponse> getPO(@RequestParam(required=false) Long id) {
        if (id != null) {
            return ps.getById(id)
                    .map(po -> ResponseEntity.ok(new ApiResponse(200, "Success", po)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse(404, "PO not found", null)));
        }
        return ResponseEntity.ok(new ApiResponse(200, "Success", ps.getAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody PurchaseOrderHeader po) {
        try {
            PurchaseOrderHeader saved = ps.save(po);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(201, "PO created successfully", saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(400, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @RequestBody PurchaseOrderHeader po) {
        try {
            PurchaseOrderHeader updated = ps.update(id, po);
            return ResponseEntity.ok(new ApiResponse(200, "PO updated successfully", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(404, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(400, "Failed to update PO", null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        try {
            ps.delete(id);
            return ResponseEntity.ok(new ApiResponse(200, "PO deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(404, "PO not found", null));
        }
    }
}
