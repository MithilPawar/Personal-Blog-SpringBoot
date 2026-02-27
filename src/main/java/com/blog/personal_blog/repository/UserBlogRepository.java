package com.blog.personal_blog.repository;

import com.blog.personal_blog.model.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByPublishedTrueOrderByCreatedAtDesc();

    Page<Blog> findByPublishedTrue(Pageable pageable);

    List<Blog> findByPublished(boolean published, Sort sort);

        Page<Blog> findByPublished(boolean published, Pageable pageable);

    List<Blog> findByTitleContainingIgnoreCase(String title, Sort sort);

        Page<Blog> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    List<Blog> findByPublishedAndTitleContainingIgnoreCase(
            boolean published,
            String title,
            Sort sort);

        Page<Blog> findByPublishedAndTitleContainingIgnoreCase(
            boolean published,
            String title,
            Pageable pageable);

    List<Blog> findByPublishedTrueAndAuthor(String author);

    List<Blog> findByPublishedTrueAndTagsContaining(String tag);

    List<Blog> findByTagsContaining(String tag);
}
