package com.example.purchase_order.purchase_order_apps.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class PoDetailResponse {
    private Long id;
    private Long itemId;
    private String itemName;
    private int qty;
    private int cost;
    private int price;
}
