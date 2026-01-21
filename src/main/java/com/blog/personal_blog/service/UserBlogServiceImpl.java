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
import org.springframework.stereotype.Service;

import java.util.List;
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

    private BlogDTO mapToDTO(Blog blog) {
        return BlogDTO.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .author(blog.getAuthor())
                .tags(blog.getTags())
                .likes(blogReactionRepository.countByBlogIdAndReactionType(blog.getId(), ReactionType.LIKE))
                .dislikes(blogReactionRepository.countByBlogIdAndReactionType(blog.getId(), ReactionType.DISLIKE))
                .commentCount(userCommentRepository.countByBlogIdAndHiddenFalse(blog.getId()))
                .createdAt(blog.getCreatedAt())
                .updatedAt(blog.getUpdatedAt())
                .build();
    }

    @Override
    public List<BlogDTO> getAllBlogs() {
        return userBlogRepository
                .findByPublishedTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BlogDTO getBlogById(Long id) {
        Blog blog = userBlogRepository.findById(id)
                .filter(Blog :: isPublished)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with Id: " + id));

        return mapToDTO(blog);
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
