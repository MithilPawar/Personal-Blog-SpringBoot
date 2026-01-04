package com.blog.personal_blog.controller;

import com.blog.personal_blog.dto.ContactRequestDTO;
import com.blog.personal_blog.service.ContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
public class ContactController {
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<Void> submitMessage(@RequestBody ContactRequestDTO requestDTO){
        contactService.saveMessage(requestDTO);
        return ResponseEntity.ok().build();
    }
}
