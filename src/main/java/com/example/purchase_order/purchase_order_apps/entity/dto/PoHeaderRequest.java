package com.example.purchase_order.purchase_order_apps.entity.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PoHeaderRequest {

    @NotNull
    private LocalDateTime dateTime;
    private String description;
    @NotEmpty
    @Valid
    private List<PoDetailRequest> details;
}
