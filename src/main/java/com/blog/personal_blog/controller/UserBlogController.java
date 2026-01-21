package com.blog.personal_blog.controller;

import com.blog.personal_blog.config.UserPrincipal;
import com.blog.personal_blog.dto.BlogDTO;
import com.blog.personal_blog.dto.ReactionRequestDTO;
import com.blog.personal_blog.service.UserBlogServiceImpl;
import com.blog.personal_blog.service.UserCommentServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
public class UserBlogController {
    private final UserBlogServiceImpl blogService;

    public UserBlogController(UserBlogServiceImpl blogService, UserCommentServiceImpl commentService) {
        this.blogService = blogService;
    }

    @GetMapping
    public List<BlogDTO> getAllBlogs(){
        return blogService.getAllBlogs();
    }

    @GetMapping("/{id}")
    public BlogDTO getBlogById(@PathVariable Long id)
    {
        return blogService.getBlogById(id);
    }

    //like a blog
    @PostMapping("/{id}/reaction")
    public ResponseEntity<?> toggleLike(
            @PathVariable Long id,
            @RequestBody ReactionRequestDTO reactionRequestDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (userPrincipal == null) {
            return ResponseEntity.status(401).build();
        }

        blogService.react(
                id,
                reactionRequestDTO.getReactionType(),
                userPrincipal.getUser());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/reaction/status")
    public ResponseEntity<?> getReactionStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(
                blogService.getUserReaction(id, userPrincipal.getUser())
        );
    }

    //sharing blog (simple)
    @GetMapping("/{id}/share")
    public ResponseEntity<String> getSharedLink(@PathVariable("id") Long blogId){
        String sharePath = "/blog/" + blogId;
        return ResponseEntity.ok(sharePath);
    }
}
