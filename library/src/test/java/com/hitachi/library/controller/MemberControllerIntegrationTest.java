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
public class MemberControllerIntegrationTest {

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
    public void testGetMembersAsUser() throws Exception {
        mockMvc.perform(get("/api/members")
                        .header("Authorization", "Bearer " + userToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetMemberByIdAsUser() throws Exception {
        // make sure you put the correct member ID
        mockMvc.perform(get("/api/members/1")
                        .header("Authorization", "Bearer " + userToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateMemberAsAdmin() throws Exception {
        String memberJson = "{\"name\":\"New Member\",\"email\":\"new.member@example.com\",\"membershipDate\":\"2024-08-08\"}";

        mockMvc.perform(post("/api/members")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(memberJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateMemberAsAdmin() throws Exception {
        String memberJson = "{\"name\":\"Updated Member\",\"email\":\"updated.member@example.com\",\"membershipDate\":\"2024-08-08\"}";
        // make sure you put the correct member ID
        mockMvc.perform(put("/api/members/1")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(memberJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteMemberAsAdmin() throws Exception {
        // make sure you put the correct member ID
        mockMvc.perform(delete("/api/members/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}
