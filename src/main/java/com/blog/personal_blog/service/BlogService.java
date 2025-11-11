package com.blog.personal_blog.service;

import com.blog.personal_blog.dto.BlogDTO;

import java.util.List;

public interface BlogService {
    BlogDTO createBlog(BlogDTO blogDTO);
    List<BlogDTO> getAllBlogs();
    BlogDTO getBlogById(Long id);
    BlogDTO updateBlog(Long id, BlogDTO blogDTO);
    void deleteBlog(Long id);
    void likeBlog(Long blogId);
}
