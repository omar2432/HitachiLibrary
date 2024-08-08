package com.hitachi.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.library.entity.Role;
import com.hitachi.library.entity.User;
import com.hitachi.library.payload.LoginRequest;
import com.hitachi.library.payload.SignupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String adminToken;
    private String userToken;

    @BeforeEach
    public void setUp() throws Exception {
        // Admin Authentication
        adminToken = obtainToken("admin", "admin123");

        // User Authentication
        userToken = obtainToken("Omar2", "newpassword");
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
    public void testGetUsersAsAdmin() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUserByIdAsAdmin() throws Exception {
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCurrentUserAsUser() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + userToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateCurrentUserAsUser() throws Exception {
        String userJson = "{\"email\":\"Omar2@gmail.com\",\"username\":\"Omar2\",\"password\":\"newpassword\",\"roles\":[{\"id\": 1,\"name\": \"ROLE_USER\"}]}";

        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());
    }

    @Test
    @Disabled
    public void testDeleteCurrentUserAsUser() throws Exception {
        mockMvc.perform(delete("/api/users/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testCreateUserAsAdmin() throws Exception {
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("newuser@gmail.com");
        user.setPassword("newpassword");
        user.setRoles(List.of(new Role(1L,"ROLE_USER")));

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateAdminAsAdmin() throws Exception {
        User user = new User();
        user.setUsername("newAdmin");
        user.setEmail("newAdmin@gmail.com");
        user.setPassword("newAdminpassword");
        user.setRoles(List.of(new Role(2L,"ROLE_ADMIN")));

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateUserAsAdmin() throws Exception {
        String userJson = "{\"email\":\"Omar222@gmail.com\",\"username\":\"Omar2\",\"password\":\"newpassword\",\"roles\":[{\"id\": 1,\"name\": \"ROLE_USER\"}]}";

        mockMvc.perform(put("/api/users/6")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());
    }

    @Test
    @Disabled
    public void testDeleteUserAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}
