package com.blog.personal_blog.service;

import com.blog.personal_blog.Enum.ReactionType;
import com.blog.personal_blog.dto.BlogDTO;
import com.blog.personal_blog.model.User;

import java.util.List;

public interface BlogService {
    BlogDTO createBlog(BlogDTO blogDTO);
    List<BlogDTO> getAllBlogs();
    BlogDTO getBlogById(Long id);
    BlogDTO updateBlog(Long id, BlogDTO blogDTO);
    void deleteBlog(Long id);
    void react(Long blogId, ReactionType reactionType, User user);
    ReactionType getUserReaction(Long blogId, User user);
}
