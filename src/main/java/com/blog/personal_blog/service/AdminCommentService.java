package com.blog.personal_blog.service;

import com.blog.personal_blog.dto.AdminCommentResponseDTO;
import com.blog.personal_blog.exception.BlogNotFoundException;
import com.blog.personal_blog.model.Blog;
import com.blog.personal_blog.model.Comment;
import com.blog.personal_blog.repository.AdminCommentRepository;
import com.blog.personal_blog.repository.UserBlogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminCommentService {
    private final AdminCommentRepository adminCommentRepository;
    private final UserBlogRepository userBlogRepository;

    public AdminCommentService(AdminCommentRepository adminCommentRepository,
                               UserBlogRepository userBlogRepository) {
        this.adminCommentRepository = adminCommentRepository;
        this.userBlogRepository = userBlogRepository;
    }

    // ✅ Recent comments (Preview page)
    public List<AdminCommentResponseDTO> getRecentComments(Long blogId) {

        Blog blog = userBlogRepository.findById(blogId)
                .orElseThrow(() ->
                        new BlogNotFoundException("Blog not found with id: " + blogId)
                );

        return adminCommentRepository
                .findTop5ByBlogOrderByCreatedAtDesc(blog)
                .stream()
                .limit(3)
                .map(comment -> AdminCommentResponseDTO.builder()
                        .id(comment.getId())
                        .authorName(comment.getUser().getUsername())
                        .text(comment.getText())
                        .hidden(comment.isHidden())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .toList();
    }

    // ✅ Comment count (Insight cards)
    public long getCommentCount(Long blogId) {
        if (!userBlogRepository.existsById(blogId)) {
            throw new BlogNotFoundException("Blog not found with id: " + blogId);
        }
        return adminCommentRepository.countByBlogId(blogId);
    }

    // ✅ Paginated comments (Admin full view)
    public Page<AdminCommentResponseDTO> getCommentsByBlog(
            Long blogId,
            int page,
            int size
    ) {

        Blog blog = userBlogRepository.findById(blogId)
                .orElseThrow(() ->
                        new BlogNotFoundException("Blog not found with id: " + blogId)
                );

        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 10 : Math.min(size, 100);

        Pageable pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return adminCommentRepository
                .findByBlog(blog, pageable)
                .map(comment -> AdminCommentResponseDTO.builder()
                        .id(comment.getId())
                        .authorName(comment.getUser().getUsername())
                        .text(comment.getText())
                        .hidden(comment.isHidden())
                        .createdAt(comment.getCreatedAt())
                        .build());
    }

    public void toggleHide(Long commentId){
        Comment comment = adminCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setHidden(!comment.isHidden());
        adminCommentRepository.save(comment);
    }
}
