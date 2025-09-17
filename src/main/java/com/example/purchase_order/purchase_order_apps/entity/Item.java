package com.example.purchase_order.purchase_order_apps.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    private int price;
    private int cost;

    private String createdBy;
    private String updatedBy;

    private LocalDateTime createdDatetime = LocalDateTime.now();
    private LocalDateTime updatedDatetime = LocalDateTime.now();
}
