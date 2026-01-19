package com.blog.personal_blog.service;

import com.blog.personal_blog.dto.AdminCommentResponseDTO;
import com.blog.personal_blog.exception.BlogNotFoundException;
import com.blog.personal_blog.model.Blog;
import com.blog.personal_blog.repository.BlogRepository;
import com.blog.personal_blog.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminCommentService {

    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;

    public AdminCommentService(CommentRepository commentRepository,
                               BlogRepository blogRepository) {
        this.commentRepository = commentRepository;
        this.blogRepository = blogRepository;
    }

    // ✅ Recent comments (Preview page)
    public List<AdminCommentResponseDTO> getRecentComments(Long blogId) {

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() ->
                        new BlogNotFoundException("Blog not found with id: " + blogId)
                );

        return commentRepository
                .findTop5ByBlogOrderByCreatedAtDesc(blog)
                .stream()
                .limit(3)
                .map(comment -> AdminCommentResponseDTO.builder()
                        .id(comment.getId())
                        .authorName(comment.getUser().getUsername())
                        .text(comment.getText())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .toList();
    }

    // ✅ Comment count (Insight cards)
    public long getCommentCount(Long blogId) {
        if (!blogRepository.existsById(blogId)) {
            throw new BlogNotFoundException("Blog not found with id: " + blogId);
        }
        return commentRepository.countByBlogId(blogId);
    }

    // ✅ Paginated comments (Admin full view)
    public Page<AdminCommentResponseDTO> getCommentsByBlog(
            Long blogId,
            int page,
            int size
    ) {

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() ->
                        new BlogNotFoundException("Blog not found with id: " + blogId)
                );

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return commentRepository
                .findByBlog(blog, pageable)
                .map(comment -> AdminCommentResponseDTO.builder()
                        .id(comment.getId())
                        .authorName(comment.getUser().getUsername())
                        .text(comment.getText())
                        .createdAt(comment.getCreatedAt())
                        .build());
    }
}
