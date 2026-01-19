package com.blog.personal_blog.controller;

import com.blog.personal_blog.dto.AdminCommentResponseDTO;
import com.blog.personal_blog.service.AdminCommentService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/blogs/comment")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCommentController {
    private final AdminCommentService adminCommentService;

    public AdminCommentController(AdminCommentService adminCommentService) {
        this.adminCommentService = adminCommentService;
    }

    @GetMapping("/{blogId}")
    public Page<AdminCommentResponseDTO> getCommentsByBLog(
            @PathVariable Long blogId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int size
    ){
        return adminCommentService.getCommentsByBlog(blogId, page, size);
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
