package com.blog.personal_blog.service;

import com.blog.personal_blog.dto.UserCommentResponseDTO;
import com.blog.personal_blog.exception.BlogNotFoundException;
import com.blog.personal_blog.model.Blog;
import com.blog.personal_blog.model.Comment;
import com.blog.personal_blog.model.User;
import com.blog.personal_blog.repository.UserBlogRepository;
import com.blog.personal_blog.repository.UserCommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCommentServiceImpl implements UserCommentService {
    private final UserCommentRepository userCommentRepository;
    private final UserBlogRepository userBlogRepository;

    public UserCommentServiceImpl(UserCommentRepository userCommentRepository, UserBlogRepository userBlogRepository) {
        this.userCommentRepository = userCommentRepository;
        this.userBlogRepository = userBlogRepository;
    }

    @Override
    public UserCommentResponseDTO addComment(Long blogId, String text, User user) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment text cannot be empty");
        }

        Blog blog = userBlogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with Id: " + blogId));

        Comment comment = Comment.builder()
                .text(text)
                .user(user)
                .blog(blog)
                .build();

        Comment saved = userCommentRepository.save(comment);

        return UserCommentResponseDTO.builder()
                .id(saved.getId())
                .text(saved.getText())
                .authorName(saved.getUser().getUsername())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public List<UserCommentResponseDTO> getCommentByBlogId(Long blogId) {
        Blog blog = userBlogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with Id: " + blogId));

        return userCommentRepository.findByBlogAndHiddenFalseOrderByCreatedAtDesc(blog)
                .stream()
                .map(comment -> UserCommentResponseDTO.builder()
                        .id(comment.getId())
                        .authorName(comment.getUser().getUsername())
                        .text(comment.getText())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .toList();
    }
}
