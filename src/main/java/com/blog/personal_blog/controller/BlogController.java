package com.blog.personal_blog.controller;

import com.blog.personal_blog.config.UserPrincipal;
import com.blog.personal_blog.dto.BlogDTO;
import com.blog.personal_blog.model.Comment;
import com.blog.personal_blog.model.User;
import com.blog.personal_blog.service.BlogServiceImpl;
import com.blog.personal_blog.service.CommentServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {
    private final BlogServiceImpl blogService;
    private final CommentServiceImpl commentService;

    public BlogController(BlogServiceImpl blogService, CommentServiceImpl commentService) {
        this.blogService = blogService;
        this.commentService = commentService;
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
    public BlogDTO getBlogById(@PathVariable Long id){
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

    //Adding comment
    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable("id") Long blogId, @RequestBody Comment comment){
        Comment saved = commentService.addComment(blogId, comment);
        return ResponseEntity.ok(saved);
    }

    //Getting comments
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable("id") Long blogId){
        List<Comment> comments = commentService.getCommentByBlogId(blogId);
        return ResponseEntity.ok(comments);
    }

    //like a blog
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (userPrincipal == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userPrincipal.getUser();

        boolean liked = blogService.toggleLike(id, user);
        long totalLike = blogService.getLikeCount(id);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("totalLike", totalLike);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/like/status")
    public ResponseEntity<?> getLikeStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userPrincipal.getUser();
        boolean liked = blogService.isLikedByUser(id, user);

        return ResponseEntity.ok(
                Map.of("liked", liked)
        );
    }

    //sharing blog (simple)
    @GetMapping("/{id}/share")
    public ResponseEntity<String> getSharedLink(@PathVariable("id") Long blogId){
        String sharePath = "/blog/" + blogId;
        return ResponseEntity.ok(sharePath);
    }
}
