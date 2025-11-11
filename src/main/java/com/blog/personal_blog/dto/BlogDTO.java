package com.blog.personal_blog.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogDTO {
    private Long id;
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 10, message = "Content must have at least 10 characters")
    private String content;

    @NotBlank(message = "Author name is required")
    @Size(min = 3, max = 50, message = "Author name must be between 3 and 50 characters")
    private String author;

    @Size(max = 100, message = "Tags must be less than 100 characters")
    private String tags;

    @Min(value = 0, message = "Likes count cannot be negative")
    private int likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
