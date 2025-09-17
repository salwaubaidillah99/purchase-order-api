package com.example.purchase_order.purchase_order_apps.controller;

import com.example.purchase_order.purchase_order_apps.entity.dto.PoHeaderRequest;
import com.example.purchase_order.purchase_order_apps.entity.dto.PoHeaderResponse;
import com.example.purchase_order.purchase_order_apps.response.ApiResponse;
import com.example.purchase_order.purchase_order_apps.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/po")
@SecurityRequirement(name = "bearerAuth")
public class PurchaseOrderController {

    private final PurchaseOrderService ps;

    public PurchaseOrderController(PurchaseOrderService ps) { this.ps = ps; }

    @Operation(summary = "Get PO(s)", description = "Ambil semua PO atau berdasarkan ID")
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

    @Operation(summary = "Create PO")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody @Valid PoHeaderRequest req,
                                              HttpServletRequest http) {
        String audit = (String) http.getAttribute("authAuditName");
        if (audit == null) audit = "system";
        PoHeaderResponse resp = ps.create(req, audit);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(201, "PO created", resp));
    }

    @Operation(summary = "Update PO")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id,
                                              @RequestBody @Valid PoHeaderRequest req,
                                              HttpServletRequest http) {
        String audit = (String) http.getAttribute("authAuditName");
        if (audit == null) audit = "system";
        PoHeaderResponse resp = ps.update(id, req, audit);
        return ResponseEntity.ok(new ApiResponse(200, "PO updated", resp));
    }

    @Operation(summary = "Delete PO")
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
