package com.blog.personal_blog.repository;

import com.blog.personal_blog.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByAuthor(String author);
    List<Blog> findByTagsContaining(String tag);
}
