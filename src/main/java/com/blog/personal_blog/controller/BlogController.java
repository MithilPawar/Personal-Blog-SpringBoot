package com.blog.personal_blog.controller;

import com.blog.personal_blog.Enum.ReactionType;
import com.blog.personal_blog.config.UserPrincipal;
import com.blog.personal_blog.dto.BlogDTO;
import com.blog.personal_blog.dto.ReactionRequestDTO;
import com.blog.personal_blog.service.BlogServiceImpl;
import com.blog.personal_blog.service.CommentServiceImpl;
import jakarta.validation.Valid;
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

    @PostMapping
    public BlogDTO createBlog(@Valid @RequestBody BlogDTO blogDTO){
        return blogService.createBlog(blogDTO);
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

    @PutMapping("/{id}")
    public BlogDTO updateBlog(@PathVariable Long id, @RequestBody BlogDTO blogDTO){
        return blogService.updateBlog(id, blogDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteBlog(@PathVariable Long id){
        blogService.deleteBlog(id);
        return "Blog Deleted Successfully";
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
    public ResponseEntity<?> getLikeStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).build();
        }

        ReactionType reactionType = blogService.getUserReaction(
                id,
                userPrincipal.getUser()
        );

        return ResponseEntity.ok(
                Map.of("reaction", reactionType));
    }

    //sharing blog (simple)
    @GetMapping("/{id}/share")
    public ResponseEntity<String> getSharedLink(@PathVariable("id") Long blogId){
        String sharePath = "/blog/" + blogId;
        return ResponseEntity.ok(sharePath);
    }
}
