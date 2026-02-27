package com.blog.personal_blog.repository;

import com.blog.personal_blog.model.Blog;
import com.blog.personal_blog.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdminCommentRepository extends JpaRepository<Comment, Long> {
    //    For Admin comments which includes hidden
    List<Comment> findTop5ByBlogOrderByCreatedAtDesc(Blog blog);

    //    Admin Only which includes hidden
    Page<Comment> findByBlog(Blog blog, Pageable pageable);

    long countByBlogId(long blogId);

    // REVIEW NOTE: Batch aggregate for admin list screen to avoid N+1 comment counts.
    @Query("""
            select c.blog.id, count(c)
            from Comment c
            where c.blog.id in :blogIds
            group by c.blog.id
            """)
    List<Object[]> countCommentsByBlogIds(@Param("blogIds") List<Long> blogIds);
}
