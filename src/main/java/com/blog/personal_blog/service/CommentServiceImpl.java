package com.blog.personal_blog.service;

import com.blog.personal_blog.dto.CommentResponseDTO;
import com.blog.personal_blog.exception.BlogNotFoundException;
import com.blog.personal_blog.model.Blog;
import com.blog.personal_blog.model.Comment;
import com.blog.personal_blog.model.User;
import com.blog.personal_blog.repository.BlogRepository;
import com.blog.personal_blog.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;

    public CommentServiceImpl(CommentRepository commentRepository, BlogRepository blogRepository) {
        this.commentRepository = commentRepository;
        this.blogRepository = blogRepository;
    }

    @Override
    public CommentResponseDTO addComment(Long blogId, String text, User user) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment text cannot be empty");
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with Id: " + blogId));

        Comment comment = Comment.builder()
                .text(text)
                .user(user)
                .blog(blog)
                .build();

        Comment saved = commentRepository.save(comment);

        return CommentResponseDTO.builder()
                .id(saved.getId())
                .text(saved.getText())
                .authorName(saved.getUser().getUsername())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public List<CommentResponseDTO> getCommentByBlogId(Long blogId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with Id: " + blogId));

        return commentRepository.findByBlogOrderByCreatedAtDesc(blog)
                .stream()
                .map(comment -> CommentResponseDTO.builder()
                        .id(comment.getId())
                        .authorName(comment.getUser().getUsername())
                        .text(comment.getText())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .toList();
    }
}
