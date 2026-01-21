package com.blog.personal_blog.service;

import com.blog.personal_blog.Enum.ReactionType;
import com.blog.personal_blog.dto.BlogDTO;
import com.blog.personal_blog.dto.ReactionStatusResponseDTO;
import com.blog.personal_blog.model.User;

import java.util.List;

public interface UserBlogService {
    List<BlogDTO> getAllBlogs();
    BlogDTO getBlogById(Long id);
    void react(Long blogId, ReactionType reactionType, User user);
    ReactionStatusResponseDTO getUserReaction(Long blogId, User user);
}
