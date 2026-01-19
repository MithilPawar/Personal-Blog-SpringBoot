package com.blog.personal_blog.service;

import com.blog.personal_blog.dto.AdminCommentResponseDTO;
import com.blog.personal_blog.exception.BlogNotFoundException;
import com.blog.personal_blog.model.Blog;
import com.blog.personal_blog.repository.BlogRepository;
import com.blog.personal_blog.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminCommentService {
    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;

    public AdminCommentService(CommentRepository commentRepository, BlogRepository blogRepository) {
        this.commentRepository = commentRepository;
        this.blogRepository = blogRepository;
    }

    public List<AdminCommentResponseDTO> getRecentComments(Long blogId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id " + blogId));

        return commentRepository.findTop5ByBlogOrderByCreatedAtDesc(blog)
                .stream()
                .map(comment -> AdminCommentResponseDTO.builder()
                        .id(comment.getId())
                        .authorName(comment.getUser().getUsername())
                        .text(comment.getText())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .toList();
    }

    public long getCommentCount(Long blogId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found"));

        return commentRepository.countByBlogId(blogId);
    }
}
