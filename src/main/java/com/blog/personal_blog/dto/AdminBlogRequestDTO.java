package com.blog.personal_blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminBlogRequestDTO {
    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must be at most 150 characters")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @Size(max = 255, message = "Tags must be at most 255 characters")
    private String tags;

    @Size(max = 50, message = "Author must be at most 50 characters")
    private String author;
}
