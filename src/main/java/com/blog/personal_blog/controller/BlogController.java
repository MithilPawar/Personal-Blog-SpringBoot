package com.blog.personal_blog.controller;

import com.blog.personal_blog.dto.BlogDTO;
import com.blog.personal_blog.model.Comment;
import com.blog.personal_blog.service.BlogServiceImpl;
import com.blog.personal_blog.service.CommentServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<String> likeBlog(@PathVariable("id") Long blogId){
        blogService.likeBlog(blogId);
        return ResponseEntity.ok("like");
    }

    //sharing blog (simple)
    @GetMapping("/{id}/share")
    public ResponseEntity<String> getSharedLink(@PathVariable("id") Long blogId){
        String sharePath = "/blog/" + blogId;
        return ResponseEntity.ok(sharePath);
    }
}
