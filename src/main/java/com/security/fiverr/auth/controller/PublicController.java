package com.security.fiverr.auth.controller;

import com.security.fiverr.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/public")
public class PublicController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/user/exists")
    public boolean checkUserExists(@RequestParam String email) {
        return userService.existsByEmail(email);
    }
}