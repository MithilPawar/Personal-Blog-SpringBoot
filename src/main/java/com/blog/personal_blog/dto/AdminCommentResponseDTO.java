package com.blog.personal_blog.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminCommentResponseDTO {
    private Long id;
    private String authorName;
    private String text;
    private LocalDateTime createdAt;
}
