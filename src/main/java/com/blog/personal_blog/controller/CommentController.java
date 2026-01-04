package com.blog.personal_blog.controller;

import com.blog.personal_blog.config.UserPrincipal;
import com.blog.personal_blog.dto.CommentRequestDTO;
import com.blog.personal_blog.dto.CommentResponseDTO;
import com.blog.personal_blog.service.CommentService;
import com.blog.personal_blog.service.CommentServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/blogs")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }

    //Adding comment
    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponseDTO> addComment(
            @PathVariable("id") Long blogId,
            @RequestBody CommentRequestDTO requestDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal){

        return ResponseEntity.ok(
                commentService.addComment(
                        blogId,
                        requestDTO.getText(),
                        userPrincipal.getUser()
                )
        );
    }

    //Getting comments
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getComments(
            @PathVariable("id") Long blogId) {

        return ResponseEntity.ok(commentService.getCommentByBlogId(blogId));
    }
}
