package com.fooddonation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
public class HomeController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("application", "Food Donation System API");
        info.put("status", "ONLINE");
        info.put("timestamp", LocalDateTime.now());
        info.put("message", "Food Donation System Backend API is running");

        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("get_all_users", "GET /api/users");
        endpoints.put("register_user", "POST /api/users/register");
        endpoints.put("get_available_donations", "GET /api/donations/available");
        endpoints.put("get_all_donations", "GET /api/donations");
        endpoints.put("create_donation", "POST /api/donations/{donorId}");
        endpoints.put("create_request", "POST /api/requests");

        info.put("available_endpoints", endpoints);
        return ResponseEntity.ok(info);
    }
}
