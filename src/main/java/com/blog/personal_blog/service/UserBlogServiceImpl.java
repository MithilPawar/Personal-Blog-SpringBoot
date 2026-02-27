package com.blog.personal_blog.service;

import com.blog.personal_blog.Enum.ReactionType;
import com.blog.personal_blog.dto.BlogDTO;
import com.blog.personal_blog.dto.ReactionStatusResponseDTO;
import com.blog.personal_blog.exception.BlogNotFoundException;
import com.blog.personal_blog.model.Blog;
import com.blog.personal_blog.model.BlogReaction;
import com.blog.personal_blog.model.User;
import com.blog.personal_blog.repository.BlogReactionRepository;
import com.blog.personal_blog.repository.UserBlogRepository;
import com.blog.personal_blog.repository.UserCommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserBlogServiceImpl implements UserBlogService {
    private final UserBlogRepository userBlogRepository;
    private final BlogReactionRepository blogReactionRepository;
    private final UserCommentRepository userCommentRepository;

    public UserBlogServiceImpl(UserBlogRepository userBlogRepository, BlogReactionRepository blogReactionRepository, UserCommentRepository userCommentRepository){
        this.userBlogRepository = userBlogRepository;
        this.blogReactionRepository = blogReactionRepository;
        this.userCommentRepository = userCommentRepository;
    }

    private BlogDTO mapToDTO(Blog blog, long likes, long dislikes, long commentCount) {
        return BlogDTO.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .author(blog.getAuthor())
                .tags(blog.getTags())
                .likes(likes)
                .dislikes(dislikes)
                .commentCount(commentCount)
                .createdAt(blog.getCreatedAt())
                .updatedAt(blog.getUpdatedAt())
                .build();
    }

    private List<BlogDTO> mapBlogsWithAggregates(List<Blog> blogs) {
        if (blogs.isEmpty()) {
            return List.of();
        }

        // REVIEW NOTE: Build blog ID list once, then aggregate likes/dislikes/comments in 3 grouped queries.
        List<Long> blogIds = blogs.stream().map(Blog::getId).toList();
        Map<Long, Long> likeCounts = new HashMap<>();
        Map<Long, Long> dislikeCounts = new HashMap<>();
        Map<Long, Long> commentCounts = new HashMap<>();

        List<Object[]> reactionRows = blogReactionRepository.countReactionsByBlogIds(blogIds);
        for (Object[] row : reactionRows) {
            Long blogId = (Long) row[0];
            ReactionType reactionType = (ReactionType) row[1];
            Long count = (Long) row[2];

            if (reactionType == ReactionType.LIKE) {
                likeCounts.put(blogId, count);
            } else if (reactionType == ReactionType.DISLIKE) {
                dislikeCounts.put(blogId, count);
            }
        }

        List<Object[]> commentRows = userCommentRepository.countVisibleCommentsByBlogIds(blogIds);
        for (Object[] row : commentRows) {
            commentCounts.put((Long) row[0], (Long) row[1]);
        }

        return blogs.stream()
                .map(blog -> mapToDTO(
                        blog,
                        likeCounts.getOrDefault(blog.getId(), 0L),
                        dislikeCounts.getOrDefault(blog.getId(), 0L),
                        commentCounts.getOrDefault(blog.getId(), 0L)
                ))
                .toList();
    }

    @Override
    public List<BlogDTO> getAllBlogs() {
        List<Blog> blogs = userBlogRepository.findByPublishedTrueOrderByCreatedAtDesc();
        return mapBlogsWithAggregates(blogs);
    }

    @Override
    public Page<BlogDTO> getAllBlogs(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 10 : Math.min(size, 100);

        Pageable pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Blog> blogPage = userBlogRepository.findByPublishedTrue(pageable);
        List<BlogDTO> blogDTOs = mapBlogsWithAggregates(blogPage.getContent());
        Map<Long, BlogDTO> dtoById = blogDTOs.stream()
            .collect(Collectors.toMap(BlogDTO::getId, dto -> dto));

        List<BlogDTO> orderedDtos = blogPage.getContent().stream()
            .map(blog -> dtoById.get(blog.getId()))
            .toList();

        return new PageImpl<>(orderedDtos, pageable, blogPage.getTotalElements());
    }

    @Override
    public BlogDTO getBlogById(Long id) {
        Blog blog = userBlogRepository.findById(id)
                .filter(Blog :: isPublished)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with Id: " + id));

        long likes = blogReactionRepository.countByBlogIdAndReactionType(blog.getId(), ReactionType.LIKE);
        long dislikes = blogReactionRepository.countByBlogIdAndReactionType(blog.getId(), ReactionType.DISLIKE);
        long commentCount = userCommentRepository.countByBlogIdAndHiddenFalse(blog.getId());

        return mapToDTO(blog, likes, dislikes, commentCount);
    }

    @Override
    public void react(Long blogId, ReactionType reactionType, User user){
        Blog blog = userBlogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with Id: " + blogId));

        Optional<BlogReaction> existingReaction = blogReactionRepository.findByBlogIdAndUserId(blogId, user.getId());

        if(existingReaction.isPresent()){
            BlogReaction blogReaction = existingReaction.get();

            if(blogReaction.getReactionType() == reactionType){
                blogReactionRepository.delete(blogReaction);
            }else{
                blogReaction.setReactionType(reactionType);
                blogReactionRepository.save(blogReaction);
            }
        }else{
            BlogReaction newReaction = BlogReaction.builder()
                    .blog(blog)
                    .user(user)
                    .reactionType(reactionType)
                    .build();

            blogReactionRepository.save(newReaction);
        }
    }

    @Override
    public ReactionStatusResponseDTO getUserReaction(Long blogId, User user) {
        return blogReactionRepository
                .findByBlogIdAndUserId(blogId, user.getId())
                .map(blogReaction ->
                        new ReactionStatusResponseDTO(blogReaction.getReactionType().name()))
                .orElse(new ReactionStatusResponseDTO("NONE"));
    }

}
