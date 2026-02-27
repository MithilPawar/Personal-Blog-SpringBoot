package com.blog.personal_blog.service;

import com.blog.personal_blog.Enum.ReactionType;
import com.blog.personal_blog.dto.BlogDTO;
import com.blog.personal_blog.dto.ReactionStatusResponseDTO;
import com.blog.personal_blog.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserBlogService {
    List<BlogDTO> getAllBlogs();
    Page<BlogDTO> getAllBlogs(int page, int size);
    BlogDTO getBlogById(Long id);
    void react(Long blogId, ReactionType reactionType, User user);
    ReactionStatusResponseDTO getUserReaction(Long blogId, User user);
}
