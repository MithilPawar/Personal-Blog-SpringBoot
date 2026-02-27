package com.blog.personal_blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequestDTO {
    @NotBlank(message = "Comment text is required")
    @Size(max = 1000, message = "Comment must be at most 1000 characters")
    private String text;
}
