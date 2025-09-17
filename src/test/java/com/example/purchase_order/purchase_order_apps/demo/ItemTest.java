package com.example.purchase_order.purchase_order_apps.demo;
import com.example.purchase_order.purchase_order_apps.entity.Item;
import com.example.purchase_order.purchase_order_apps.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemTest {


        @LocalServerPort
        private int port;

        @Autowired
        private TestRestTemplate restTemplate;

        @Autowired
        private ItemRepository itemRepository;

        private String getBaseUrl() {
            return "http://localhost:" + port + "/api/items";
        }

        @BeforeEach
        void cleanDatabase() {
            itemRepository.deleteAll(); // pastikan database kosong sebelum tiap test
        }

        @Test
        void testCreateItem() {
            Item item = Item.builder()
                    .name("Mouse")
                    .description("Wireless Mouse")
                    .price(200000)
                    .cost(120000)
                    .createdBy("admin")
                    .build();

            ResponseEntity<String> createResponse = restTemplate.postForEntity(
                    getBaseUrl(), item, String.class);

            assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            // cek langsung di repository
            List<Item> all = itemRepository.findAll();
            assertThat(all).hasSize(1);
            assertThat(all.get(0).getName()).isEqualTo("Mouse");
        }

        @Test
        void testGetItems() {
            // Siapkan data dulu
            Item item = Item.builder()
                    .name("Mouse")
                    .description("Wireless Mouse")
                    .price(200000)
                    .cost(120000)
                    .createdBy("admin")
                    .build();
            itemRepository.save(item);

            // GET semua item
            ResponseEntity<String> getResponse = restTemplate.getForEntity(getBaseUrl(), String.class);

            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(getResponse.getBody()).contains("Mouse");
        }
    }
