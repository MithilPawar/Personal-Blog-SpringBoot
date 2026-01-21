package com.blog.personal_blog.service;

import com.blog.personal_blog.dto.UserCommentResponseDTO;
import com.blog.personal_blog.model.User;

import java.util.List;

public interface UserCommentService {

    UserCommentResponseDTO addComment(Long blogId, String text, User user);

    List<UserCommentResponseDTO> getCommentByBlogId(Long blogId);
}
