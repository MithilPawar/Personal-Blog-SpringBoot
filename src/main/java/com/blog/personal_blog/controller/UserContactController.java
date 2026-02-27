package com.blog.personal_blog.controller;

import com.blog.personal_blog.dto.ContactRequestDTO;
import com.blog.personal_blog.service.UserContactService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
public class UserContactController {
    private final UserContactService userContactService;

    public UserContactController(UserContactService userContactService) {
        this.userContactService = userContactService;
    }

    @PostMapping
    public ResponseEntity<Void> submitMessage(@Valid @RequestBody ContactRequestDTO requestDTO){
        userContactService.saveMessage(requestDTO);
        return ResponseEntity.ok().build();
    }
}
