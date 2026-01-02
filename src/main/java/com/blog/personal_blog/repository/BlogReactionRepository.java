package com.blog.personal_blog.repository;

import com.blog.personal_blog.Enum.ReactionType;
import com.blog.personal_blog.model.BlogReaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogReactionRepository extends JpaRepository<BlogReaction, Long> {
    Optional<BlogReaction> findByBlogIdAndUserId(Long blogId, Long userId);

    long countByBlogIdAndReactionType(Long blogId, ReactionType reactionType);
}
