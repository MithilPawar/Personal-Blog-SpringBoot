package com.blog.personal_blog.dto;

import lombok.Data;

@Data
public class ContactRequestDTO {
    private String username;
    private String email;
    private String message;
}
