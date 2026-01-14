package com.blog.personal_blog.repository;

import com.blog.personal_blog.model.Blog;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByPublishedTrueOrderByCreatedAtDesc();

    List<Blog> findByPublished(boolean published, Sort sort);

    List<Blog> findByTitleContainingIgnoreCase(String title, Sort sort);

    List<Blog> findByPublishedAndTitleContainingIgnoreCase(
            boolean published,
            String title,
            Sort sort);

    List<Blog> findByPublishedTrueAndAuthor(String author);

    List<Blog> findByPublishedTrueAndTagsContaining(String tag);

    List<Blog> findByTagsContaining(String tag);
}
