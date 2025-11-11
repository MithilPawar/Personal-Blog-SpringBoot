package com.blog.personal_blog.service;

import com.blog.personal_blog.model.Comment;

import java.util.List;

public interface CommentService {
    Comment addComment(Long blogId, Comment comment);
    List<Comment> getCommentByBlogId(Long blogId);
}
