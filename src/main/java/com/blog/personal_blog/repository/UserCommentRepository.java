package com.blog.personal_blog.repository;

import com.blog.personal_blog.model.Blog;
import com.blog.personal_blog.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserCommentRepository extends JpaRepository<Comment, Long> {
//   For User comments
    List<Comment> findByBlogAndHiddenFalseOrderByCreatedAtDesc(Blog blog);

//    For User comments
    long countByBlogIdAndHiddenFalse(long blogId);
}
