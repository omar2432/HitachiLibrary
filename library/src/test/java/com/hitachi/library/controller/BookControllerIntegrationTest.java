package com.hitachi.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.library.payload.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String adminToken;
    private String userToken;

    @BeforeEach
    public void setUp() throws Exception {
        // Admin Authentication
        adminToken = obtainToken("admin", "admin123");

        // User Authentication
        userToken = obtainToken("Omar4", "omarPass4");
    }

    private String obtainToken(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        String response = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract token from response
        return response.split(":")[1].replaceAll("\"", "").replace("}", "").trim();
    }

    @Test
    public void testGetBooksAsUser() throws Exception {
        mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer " + userToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetBookByIdAsUser() throws Exception {
        mockMvc.perform(get("/api/books/1")
                        .header("Authorization", "Bearer " + userToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateBookAsAdmin() throws Exception {
        // make sure to put in the correct ID of a valid Author
        String bookJson = "{\"title\":\"New Book\",\"isbn\":\"1234567890123\",\"publishedDate\":\"2024-08-08\",\"author\":{\"id\":1}}";

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateBookAsAdmin() throws Exception {
        // make sure to put in the correct ID of a valid Author
        String bookJson = "{\"title\":\"Updated Book\",\"isbn\":\"1234567890123\",\"publishedDate\":\"2024-08-08\",\"author\":{\"id\":1}}";

        mockMvc.perform(put("/api/books/1")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteBookAsAdmin() throws Exception {
        // make sure to put in the correct ID of a valid book
        mockMvc.perform(delete("/api/books/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}
