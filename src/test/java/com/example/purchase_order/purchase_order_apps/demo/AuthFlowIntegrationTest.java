package com.example.purchase_order.purchase_order_apps.demo;
import com.example.purchase_order.purchase_order_apps.entity.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // gunakan application-test.yml jika ada
class AuthFlowIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test
    void register_login_then_access_protected() throws Exception {
        // register
        var reg = """
      {"firstName":"A","lastName":"B","email":"a@b.com","phone":"1","password":"secret"}
    """;
        mvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reg))
                .andExpect(status().isCreated());

        // login
        LoginRequest login = new LoginRequest();
        login.setEmail("a@b.com");
        login.setPassword("secret");

        String loginResp = mvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // extract token sederhana
        String token = om.readTree(loginResp).path("data").path("token").asText();
        // akses protected
        mvc.perform(get("/api/users/1")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk()); // atau 404 jika user id 1 tidak ada, sesuaikan
    }
}
