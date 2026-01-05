package com.blog.personal_blog.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BlogResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String author;
    private String tags;
    private boolean published;
    private LocalDateTime createdAt;
}
