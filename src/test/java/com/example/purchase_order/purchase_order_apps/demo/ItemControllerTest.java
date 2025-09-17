package com.example.purchase_order.purchase_order_apps.demo;

import com.example.purchase_order.purchase_order_apps.controller.ItemController;
import com.example.purchase_order.purchase_order_apps.entity.Item;
import com.example.purchase_order.purchase_order_apps.response.ApiResponse;
import com.example.purchase_order.purchase_order_apps.service.ItemService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private final ItemService itemService = Mockito.mock(ItemService.class);
    private final ItemController itemController = new ItemController(itemService);

    @Test
    void testGetAllItems() {
        when(itemService.getAllItems()).thenReturn(Collections.emptyList());

        ResponseEntity<ApiResponse> response = itemController.getItems(null);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        ApiResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(200);
        assertThat(body.getMessage()).isEqualTo("Success");
        assertThat(body.getData()).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGetItemByIdFound() {
        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Desc")
                .price(100)
                .cost(50)
                .createdBy("admin")
                .updatedBy("editor")
                .createdDatetime(LocalDateTime.now())
                .updatedDatetime(LocalDateTime.now())
                .build();

        when(itemService.getItemById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<ApiResponse> response = itemController.getItems(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        ApiResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(200);
        assertThat(body.getData()).isEqualTo(item);
    }

    @Test
    void testGetItemByIdNotFound() {
        when(itemService.getItemById(99L)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = itemController.getItems(99L);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        ApiResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(404);
        assertThat(body.getMessage()).isEqualTo("Item not found");
    }

    @Test
    void testCreateItem() {
        Item item = Item.builder()
                .id(1L)
                .name("Keyboard")
                .description("Mechanical Keyboard")
                .price(500000)
                .cost(300000)
                .createdBy("admin")
                .createdDatetime(LocalDateTime.now())
                .updatedDatetime(LocalDateTime.now())
                .build();

        when(itemService.saveItem(item)).thenReturn(item);

        ResponseEntity<ApiResponse> response = itemController.createItem(item);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        ApiResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(201);
        assertThat(body.getData()).isEqualTo(item);
    }

    @Test
    void testDeleteItem() {
        ResponseEntity<ApiResponse> response = itemController.deleteItem(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        ApiResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(200);
        assertThat(body.getMessage()).isEqualTo("Item deleted successfully");
    }
}
