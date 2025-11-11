package com.blog.personal_blog.service;

import com.blog.personal_blog.exception.BlogNotFoundException;
import com.blog.personal_blog.model.Blog;
import com.blog.personal_blog.model.Comment;
import com.blog.personal_blog.repository.BlogRepository;
import com.blog.personal_blog.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;

    public CommentServiceImpl(CommentRepository commentRepository, BlogRepository blogRepository) {
        this.commentRepository = commentRepository;
        this.blogRepository = blogRepository;
    }

    @Override
    public Comment addComment(Long blogId, Comment comment) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with Id: " + blogId));
        comment.setBlog(blog);
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentByBlogId(Long blogId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with Id: " + blogId));
        return commentRepository.findByBlogOrderByCreatedAtDesc(blog);
    }
}
