package com.blog.personal_blog.repository;

import com.blog.personal_blog.model.Blog;
import com.blog.personal_blog.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBlogOrderByCreatedAtDesc(Blog blog);
}
