package com.blog.personal_blog.controller;

import com.blog.personal_blog.Enum.ReactionType;
import com.blog.personal_blog.config.UserPrincipal;
import com.blog.personal_blog.dto.BlogDTO;
import com.blog.personal_blog.dto.ReactionRequestDTO;
import com.blog.personal_blog.service.BlogServiceImpl;
import com.blog.personal_blog.service.CommentServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {
    private final BlogServiceImpl blogService;

    public BlogController(BlogServiceImpl blogService, CommentServiceImpl commentService) {
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
