package com.blog.personal_blog.repository;

import com.blog.personal_blog.model.Blog;
import com.blog.personal_blog.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserCommentRepository extends JpaRepository<Comment, Long> {
//   For User comments
    List<Comment> findByBlogAndHiddenFalseOrderByCreatedAtDesc(Blog blog);

//    For User comments
    long countByBlogIdAndHiddenFalse(long blogId);

//    REVIEW NOTE: Batch aggregate for published-blog list response.
    @Query("""
            select c.blog.id, count(c)
            from Comment c
            where c.hidden = false and c.blog.id in :blogIds
            group by c.blog.id
            """)
    List<Object[]> countVisibleCommentsByBlogIds(@Param("blogIds") List<Long> blogIds);
}
