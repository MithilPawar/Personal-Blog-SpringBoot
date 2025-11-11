package com.blog.personal_blog.controller;

import com.blog.personal_blog.model.User;
import com.blog.personal_blog.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserRepository userRepository;
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public User getCurrentUserProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username  = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/another")
    public Map<String, Object> getAnother(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Map<String, Object> userData = new HashMap<>();
        userData.put("Name", user.getUsername());
        userData.put("Role", user.getRole());
        userData.put("Status", user.isEnabled());

        return userData;
    }
}
