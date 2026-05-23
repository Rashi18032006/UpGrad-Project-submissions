package com.upgrad.quora.api.controller;

import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "User registration, authentication, and profile APIs")
public class UserController {

    private final UserBusinessService userBusinessService;

    public UserController(UserBusinessService userBusinessService) {
        this.userBusinessService = userBusinessService;
    }

    @PostMapping("/signup")
    @Operation(summary = "Register a new user")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        UserEntity user = new UserEntity();
        user.setFirstName(req.firstName());
        user.setLastName(req.lastName());
        user.setUsername(req.username());
        user.setEmail(req.email());
        user.setPassword(req.password());
        UserEntity saved = userBusinessService.signup(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", saved.getUuid(), "status", "USER SUCCESSFULLY REGISTERED"));
    }

    @PostMapping("/signin")
    @Operation(summary = "Sign in and receive a JWT token")
    public ResponseEntity<?> signin(@RequestBody SigninRequest req) {
        String token = userBusinessService.signin(req.username(), req.password());
        return ResponseEntity.ok(Map.of("token", token, "message", "SIGNED IN SUCCESSFULLY"));
    }

    @PostMapping("/signout")
    @Operation(summary = "Sign out (invalidate session)")
    public ResponseEntity<?> signout(@RequestHeader("Authorization") String authHeader) {
        userBusinessService.signout(extractToken(authHeader));
        return ResponseEntity.ok(Map.of("message", "SIGNED OUT SUCCESSFULLY"));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user profile by UUID")
    public ResponseEntity<?> getProfile(@PathVariable String userId,
                                        @RequestHeader("Authorization") String authHeader) {
        UserEntity user = userBusinessService.getUserProfile(userId, extractToken(authHeader));
        return ResponseEntity.ok(Map.of(
                "uuid", user.getUuid(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "username", user.getUsername(),
                "email", user.getEmail()
        ));
    }

    @DeleteMapping("/admin/userDelete/{userId}")
    @Operation(summary = "Admin: delete a user by UUID")
    public ResponseEntity<?> deleteUser(@PathVariable String userId,
                                        @RequestHeader("Authorization") String authHeader) {
        userBusinessService.deleteUser(userId, extractToken(authHeader));
        return ResponseEntity.ok(Map.of("id", userId, "status", "USER SUCCESSFULLY DELETED"));
    }

    private String extractToken(String header) {
        if (header != null && header.startsWith("Bearer ")) return header.substring(7);
        return header;
    }

    record SignupRequest(String firstName, String lastName, String username,
                         String email, String password) {}
    record SigninRequest(String username, String password) {}
}
