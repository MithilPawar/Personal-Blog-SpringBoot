package com.blog.personal_blog.model;

import com.blog.personal_blog.Enum.ReactionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "blog_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"blog_id", "user_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reactionType;
}
