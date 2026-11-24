package com.fooddonation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class HomeController {

    // Serves the full visual Dashboard UI at root /
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    // Serves the API status & endpoints overview at /api
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiStatus() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("application", "Food Donation System API");
        info.put("status", "ONLINE");
        info.put("timestamp", LocalDateTime.now());
        info.put("message", "Welcome to the Food Donation System Backend API");

        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("dashboard_ui", "GET /");
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
