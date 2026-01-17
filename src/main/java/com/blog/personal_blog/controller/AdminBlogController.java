package com.blog.personal_blog.controller;

import com.blog.personal_blog.config.UserPrincipal;
import com.blog.personal_blog.dto.AdminBlogRequestDTO;
import com.blog.personal_blog.service.AdminBlogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/blogs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBlogController {
    private final AdminBlogService adminBlogService;

    public AdminBlogController(AdminBlogService adminBlogService) {
        this.adminBlogService = adminBlogService;
    }

    @PostMapping
    public ResponseEntity<?> createBlog(
            @RequestBody AdminBlogRequestDTO adminBlogRequestDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal){
        return ResponseEntity.ok(
                adminBlogService.createBlog(adminBlogRequestDTO, userPrincipal.getUser()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBlog(
            @PathVariable("id") Long id,
            @RequestBody AdminBlogRequestDTO adminBlogRequestDTO){
        return ResponseEntity.ok(adminBlogService.updateBlog(id, adminBlogRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable("id") Long id){
        adminBlogService.deleteBlog(id);
        return ResponseEntity.ok("Blog deleted");
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<?> togglePublish(@PathVariable Long id) {
        return ResponseEntity.ok(adminBlogService.togglePublish(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllBlogsForAdmin(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order ){
        return ResponseEntity.ok(adminBlogService.getAllBlogsForAdmin(status, search, sortBy, order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBlogById(@PathVariable Long id){
        return ResponseEntity.ok(adminBlogService.getBlogById(id));
    }
}
