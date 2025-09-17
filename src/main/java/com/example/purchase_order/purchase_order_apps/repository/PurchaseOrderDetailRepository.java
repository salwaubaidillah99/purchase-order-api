package com.example.purchase_order.purchase_order_apps.repository;

import com.example.purchase_order.purchase_order_apps.entity.PurchaseOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, Long> {
    List<PurchaseOrderDetail> findByPoHeaderId(Long pohId);
}
