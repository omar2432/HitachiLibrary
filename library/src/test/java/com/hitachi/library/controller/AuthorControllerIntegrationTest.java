package com.hitachi.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.library.payload.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorControllerIntegrationTest {

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
    public void testGetAuthorsAsUser() throws Exception {
        mockMvc.perform(get("/api/authors")
                        .header("Authorization", "Bearer " + userToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAuthorByIdAsUser() throws Exception {
        // make sure you put the right ID in the URL if the sequence changed
        mockMvc.perform(get("/api/authors/1")
                        .header("Authorization", "Bearer " + userToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateAuthorAsAdmin() throws Exception {
        String authorJson = "{\"name\":\"New Author\",\"biography\":\"Bio\",\"dob\":\"2000-01-01\"}";

        mockMvc.perform(post("/api/authors")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateAuthorAsAdmin() throws Exception {
        String authorJson = "{\"name\":\"Updated Author\",\"biography\":\"Updated Bio\",\"dob\":\"2000-01-01\"}";
        // make sure you put the right ID in the URL if the sequence changed
        mockMvc.perform(put("/api/authors/1")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteAuthorAsAdmin() throws Exception {
        // make sure you put the right ID in the URL if the sequence changed
        mockMvc.perform(delete("/api/authors/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}
