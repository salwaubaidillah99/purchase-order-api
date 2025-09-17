package com.example.purchase_order.purchase_order_apps.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "po_d")
public class PurchaseOrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poh_id", nullable = false)
    private PurchaseOrderHeader poHeader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @JsonBackReference
    private Item item;

    private int itemQty;
    private int itemCost;
    private int itemPrice;

    private String createdBy;
    private String updatedBy;

    private LocalDateTime createdDatetime = LocalDateTime.now();
    private LocalDateTime updatedDatetime = LocalDateTime.now();
}

