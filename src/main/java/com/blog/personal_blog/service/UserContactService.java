package com.blog.personal_blog.service;

import com.blog.personal_blog.dto.ContactRequestDTO;
import com.blog.personal_blog.model.ContactMessage;
import com.blog.personal_blog.repository.ContactRepository;
import org.springframework.stereotype.Service;

@Service
public class UserContactService {
    private final ContactRepository contactRepository;

    public UserContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public void saveMessage(ContactRequestDTO dto){
        ContactMessage contactMessage = ContactMessage.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .message(dto.getMessage())
                .build();

        contactRepository.save(contactMessage);
    }

}
