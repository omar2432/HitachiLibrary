package com.hitachi.library.service;

import com.hitachi.library.entity.Role;
import com.hitachi.library.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        role = new Role();
        role.setName("ROLE_USER");
    }

    @Test
    void testFindByName() {
        when(roleRepository.findByName("ROLE_USER")).thenReturn(role);

        Role foundRole = roleService.findByName("ROLE_USER");

        assertNotNull(foundRole);
        assertEquals("ROLE_USER", foundRole.getName());
    }

    @Test
    void testSaveRole() {
        when(roleRepository.save(role)).thenReturn(role);

        Role savedRole = roleService.save(role);

        assertNotNull(savedRole);
        assertEquals("ROLE_USER", savedRole.getName());
    }
}
