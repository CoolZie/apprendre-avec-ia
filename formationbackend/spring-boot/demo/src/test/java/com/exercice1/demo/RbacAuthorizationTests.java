package com.exercice1.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.exercice1.DemoApplication;
import com.exercice1.demo.dto.ProductResponse;
import com.exercice1.demo.model.Product;
import com.exercice1.demo.model.enums.Category;
import com.exercice1.demo.service.ProductService;
import com.exercice1.security.dto.UserResponse;
import com.exercice1.security.service.UserService;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
class RbacAuthorizationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void userCannotCreateProduct() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Laptop Test",
                          "description": "desc",
                          "price": 899.99,
                          "stock": 10,
                          "category": "ELECTRONICS"
                        }
                        """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "moderator", roles = { "MODERATOR" })
    void moderatorCanCreateProduct() throws Exception {
                Product product = new Product();
                product.setId(1L);
                product.setName("Laptop Test");
                product.setDescription("desc");
                product.setPrice(899.99);
                product.setStock(10);
                product.setCategory(Category.ELECTRONICS);
                product.setCreatedAt(LocalDateTime.now());
                product.setUpdatedAt(LocalDateTime.now());
                ProductResponse response = new ProductResponse(product);

        when(productService.createProduct(any())).thenReturn(response);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Laptop Test",
                          "description": "desc",
                          "price": 899.99,
                          "stock": 10,
                          "category": "ELECTRONICS"
                        }
                        """))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "moderator", roles = { "MODERATOR" })
    void moderatorCannotDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void adminCanDeleteProduct() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" })
    void userCannotListUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void adminCanListUsers() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of(new UserResponse(1L, "admin", "admin@example.com", LocalDateTime.now())));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }
}
