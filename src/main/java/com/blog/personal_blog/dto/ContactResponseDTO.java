package com.blog.personal_blog.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactResponseDTO {
    private String message;
}
