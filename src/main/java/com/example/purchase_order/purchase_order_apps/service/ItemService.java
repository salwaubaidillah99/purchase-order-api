package com.example.purchase_order.purchase_order_apps.service;

import com.example.purchase_order.purchase_order_apps.entity.Item;
import com.example.purchase_order.purchase_order_apps.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public Item updateItem(Long id, Item itemDetails) {
        return itemRepository.findById(id).map(item -> {
            item.setName(itemDetails.getName());
            item.setDescription(itemDetails.getDescription());
            item.setPrice(itemDetails.getPrice());
            item.setCost(itemDetails.getCost());
            item.setUpdatedBy(itemDetails.getUpdatedBy());
            item.setUpdatedDatetime(LocalDateTime.now());
            return itemRepository.save(item);
        }).orElseThrow(() -> new RuntimeException("Item not found"));
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}




