package com.example.purchase_order.purchase_order_apps.repository;

import com.example.purchase_order.purchase_order_apps.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
