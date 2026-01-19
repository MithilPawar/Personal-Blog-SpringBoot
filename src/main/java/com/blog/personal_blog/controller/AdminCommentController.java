package com.blog.personal_blog.controller;

import com.blog.personal_blog.dto.AdminCommentResponseDTO;
import com.blog.personal_blog.service.AdminCommentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/blogs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCommentController {
    private final AdminCommentService adminCommentService;

    public AdminCommentController(AdminCommentService adminCommentService) {
        this.adminCommentService = adminCommentService;
    }

    @GetMapping("/{id}/comments/recent")
    public List<AdminCommentResponseDTO> getRecentComments(@PathVariable Long id){
        return adminCommentService.getRecentComments(id);
    }

    @GetMapping("/{id}/comments/count")
    public long getCommentsCount(@PathVariable Long id){
        return adminCommentService.getCommentCount(id);
    }
}
