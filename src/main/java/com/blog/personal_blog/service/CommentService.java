package com.blog.personal_blog.service;

import com.blog.personal_blog.dto.CommentResponseDTO;
import com.blog.personal_blog.model.User;

import java.util.List;

public interface CommentService {

    CommentResponseDTO addComment(Long blogId, String text, User user);

    List<CommentResponseDTO> getCommentByBlogId(Long blogId);
}
