package com.example.purchase_order.purchase_order_apps.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "po_h")
public class PurchaseOrderHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime;

    private String description;

    private Integer totalPrice;

    private Integer totalCost;

    private String createdBy;

    private String updatedBy;

    private LocalDateTime createdDateTime = LocalDateTime.now();

    private LocalDateTime updatedDateTime = LocalDateTime.now();

    @OneToMany(mappedBy = "poHeader", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<PurchaseOrderDetail> details = new ArrayList<>();

}
