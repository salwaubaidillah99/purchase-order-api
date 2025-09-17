package com.example.purchase_order.purchase_order_apps.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PoHeaderResponse {
        private Long id;
        private LocalDateTime datetime;
        private String description;
        private int totalPrice;
        private int totalCost;
        private List<PoDetailResponse> details;
}

