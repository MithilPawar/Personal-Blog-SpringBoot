package com.blog.personal_blog.controller;

import com.blog.personal_blog.dto.UserProfileDTO;
import com.blog.personal_blog.repository.UserProfileRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserProfileController {
    private final UserProfileRepository userProfileRepository;
    public UserProfileController(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @GetMapping("/profile")
    public UserProfileDTO getCurrentUserProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username  = authentication.getName();

        var user = userProfileRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserProfileDTO(
                user.getUsername(),
                user.getRole().name(),
                user.isEnabled()
        );
    }
}
