package com.hitachi.library.controller;

import com.hitachi.library.entity.Role;
import com.hitachi.library.entity.User;
import com.hitachi.library.payload.JwtResponse;
import com.hitachi.library.payload.LoginRequest;
import com.hitachi.library.payload.SignupRequest;
import com.hitachi.library.payload.UserDTO;
import com.hitachi.library.service.AuthService;
import com.hitachi.library.service.RoleService;
import com.hitachi.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Autowired
    public UserController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder, AuthService authService) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody SignupRequest signupRequest) {
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setEmail(signupRequest.getEmail());
        // Set default role to ROLE_USER
        user.setRoles(List.of(new Role(1L,"ROLE_USER")));
/*
        Role userRole = roleService.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            roleService.save(userRole);
        }

        user.setRoles(List.of(userRole));
*/
        userService.save(user);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            String jwt = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(user -> new UserDTO(user.getUsername(), user.getEmail(), user.getRoles()))
                .collect(Collectors.toList());
    }


    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserDTO userDTO=new UserDTO(user.getUsername(),user.getEmail(), user.getRoles());
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {

        User currentUser = userService.getUserById(id);
        currentUser.setUsername(userDetails.getUsername());
        currentUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        currentUser.setEmail(userDetails.getEmail());
        User updatedUser = userService.updateUser(currentUser.getId(), currentUser);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyProfile(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userService.getUserByUsername(username);
        UserDTO userDTO=new UserDTO(user.getUsername(),user.getEmail(), user.getRoles());
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateMyProfile(Authentication authentication, @RequestBody User userDetails) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User currentUser = userService.getUserByUsername(username);
        currentUser.setUsername(userDetails.getUsername());
        currentUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        currentUser.setEmail(userDetails.getEmail());
        User updatedUser = userService.updateUser(currentUser.getId(), currentUser);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User currentUser = userService.getUserByUsername(username);
        userService.deleteUser(currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
