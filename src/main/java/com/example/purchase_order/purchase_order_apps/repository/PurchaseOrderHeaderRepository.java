package com.example.purchase_order.purchase_order_apps.repository;


import com.example.purchase_order.purchase_order_apps.entity.PurchaseOrderHeader;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseOrderHeaderRepository extends JpaRepository<PurchaseOrderHeader, Long> {
    @EntityGraph(attributePaths = "details")
    Optional<PurchaseOrderHeader> findWithDetailsById(Long id);

}

