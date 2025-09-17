package com.example.purchase_order.purchase_order_apps.controller;

import com.example.purchase_order.purchase_order_apps.entity.Item;
import com.example.purchase_order.purchase_order_apps.response.ApiResponse;
import com.example.purchase_order.purchase_order_apps.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService is;

    public ItemController(ItemService is) {
        this.is = is;
    }

    @Operation(summary = "Get items", description = "Ambil semua item atau berdasarkan ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Success")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Item not found")

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
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Item created successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Failed to create item")
    @PostMapping
    public ResponseEntity<ApiResponse> createItem(@RequestBody Item item) {
        try {
            Item saved = is.saveItem(item);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(201, "Item created successfully", saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(400, "Failed to create item", null));
        }
    }

    @Operation(summary = "Update item", description = "Update item berdasarkan ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item updated successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Item not found")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Failed to update item")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateItem(@PathVariable Long id, @RequestBody Item item) {
        try {
            Item updated = is.updateItem(id, item);
            return ResponseEntity.ok(new ApiResponse(200, "Item updated successfully", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(404, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(400, "Failed to update item", null));
        }
    }

    @Operation(summary = "Delete item", description = "Hapus item berdasarkan ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item deleted successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Item not found")
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
