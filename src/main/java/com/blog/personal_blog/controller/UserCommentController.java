package com.blog.personal_blog.controller;

import com.blog.personal_blog.config.UserPrincipal;
import com.blog.personal_blog.dto.CommentRequestDTO;
import com.blog.personal_blog.dto.UserCommentResponseDTO;
import com.blog.personal_blog.service.UserCommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/blogs")
public class UserCommentController {
    private final UserCommentService userCommentService;

    public UserCommentController(UserCommentService userCommentService){
        this.userCommentService = userCommentService;
    }

    //Adding comment
    @PostMapping("/{id}/comments")
    public ResponseEntity<UserCommentResponseDTO> addComment(
            @PathVariable("id") Long blogId,
            @Valid @RequestBody CommentRequestDTO requestDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal){

        return ResponseEntity.ok(
                userCommentService.addComment(
                        blogId,
                        requestDTO.getText(),
                        userPrincipal.getUser()
                )
        );
    }

    //Getting comments
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<UserCommentResponseDTO>> getComments(
            @PathVariable("id") Long blogId) {

        return ResponseEntity.ok(userCommentService.getCommentByBlogId(blogId));
    }
}
