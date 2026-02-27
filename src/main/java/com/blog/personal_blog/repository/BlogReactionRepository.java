package com.blog.personal_blog.repository;

import com.blog.personal_blog.Enum.ReactionType;
import com.blog.personal_blog.model.BlogReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BlogReactionRepository extends JpaRepository<BlogReaction, Long> {
    Optional<BlogReaction> findByBlogIdAndUserId(Long blogId, Long userId);

    long countByBlogIdAndReactionType(Long blogId, ReactionType reactionType);

    // REVIEW NOTE: Batch aggregate for list screens to avoid one query per blog.
    @Query("""
            select br.blog.id, br.reactionType, count(br)
            from BlogReaction br
            where br.blog.id in :blogIds
            group by br.blog.id, br.reactionType
            """)
    List<Object[]> countReactionsByBlogIds(@Param("blogIds") List<Long> blogIds);
}
