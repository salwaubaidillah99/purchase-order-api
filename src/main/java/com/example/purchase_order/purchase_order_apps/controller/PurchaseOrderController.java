package com.example.purchase_order.purchase_order_apps.controller;

import com.example.purchase_order.purchase_order_apps.entity.PurchaseOrderHeader;
import com.example.purchase_order.purchase_order_apps.entity.dto.PoHeaderRequest;
import com.example.purchase_order.purchase_order_apps.entity.dto.PoHeaderResponse;
import com.example.purchase_order.purchase_order_apps.response.ApiResponse;
import com.example.purchase_order.purchase_order_apps.service.PurchaseOrderService;
import jakarta.validation.Valid;
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
    public ResponseEntity<ApiResponse> create(@RequestBody @Valid PoHeaderRequest req,
                                              @RequestHeader(value="X-User", required=false) String user) {
        PoHeaderResponse resp = ps.create(req, user == null ? "system" : user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(201, "PO created", resp));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id,
                                              @RequestBody @Valid PoHeaderRequest req,
                                              @RequestHeader(value="X-User", required=false) String user) {
        PoHeaderResponse resp = ps.update(id, req, user == null ? "system" : user);
        return ResponseEntity.ok(new ApiResponse(200, "PO updated", resp));
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
