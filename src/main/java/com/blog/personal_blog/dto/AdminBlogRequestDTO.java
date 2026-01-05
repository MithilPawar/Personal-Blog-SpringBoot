package com.blog.personal_blog.dto;

import lombok.Data;

@Data
public class AdminBlogRequestDTO {
    private String title;
    private String content;
    private String tags;
    private String author;
}
