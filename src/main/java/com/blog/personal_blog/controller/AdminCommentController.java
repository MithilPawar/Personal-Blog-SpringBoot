package com.blog.personal_blog.controller;

import com.blog.personal_blog.dto.AdminCommentResponseDTO;
import com.blog.personal_blog.service.AdminCommentService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCommentController {
    private final AdminCommentService adminCommentService;

    public AdminCommentController(AdminCommentService adminCommentService) {
        this.adminCommentService = adminCommentService;
    }

    @GetMapping("/blogs/{blogId}/comments")
    public Page<AdminCommentResponseDTO> getCommentsByBlog(
            @PathVariable Long blogId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return adminCommentService.getCommentsByBlog(blogId, page, size);
    }

    @GetMapping("/blogs/{blogId}/comments/recent")
    public List<AdminCommentResponseDTO> getRecentComments(@PathVariable Long blogId){
        return adminCommentService.getRecentComments(blogId);
    }

    @GetMapping("/blogs/{blogId}/comments/count")
    public long getCommentsCount(@PathVariable Long blogId){
        return adminCommentService.getCommentCount(blogId);
    }

    @PatchMapping("/comments/{commentId}/toggle-hide")
    public ResponseEntity<Void> toggleHide(@PathVariable Long commentId){
        adminCommentService.toggleHide(commentId);
        return ResponseEntity.ok().build();
    }
}
