package com.example.purchase_order.purchase_order_apps.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PoDetailRequest {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @NotNull
    private Long itemId;
    @Min(1)
    private int qty;
    @Min(0)
    private int cost;
    @Min(0)
    private int price;
}
