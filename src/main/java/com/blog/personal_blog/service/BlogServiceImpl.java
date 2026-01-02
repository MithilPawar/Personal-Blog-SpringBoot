package com.blog.personal_blog.service;

import com.blog.personal_blog.Enum.ReactionType;
import com.blog.personal_blog.dto.BlogDTO;
import com.blog.personal_blog.exception.BlogNotFoundException;
import com.blog.personal_blog.model.Blog;
import com.blog.personal_blog.model.BlogReaction;
import com.blog.personal_blog.model.User;
import com.blog.personal_blog.repository.BlogReactionRepository;
import com.blog.personal_blog.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService{
    private final BlogRepository blogRepository;
    private final BlogReactionRepository blogReactionRepository;

    public BlogServiceImpl(BlogRepository blogRepository, BlogReactionRepository blogReactionRepository){
        this.blogRepository  = blogRepository;
        this.blogReactionRepository = blogReactionRepository;
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
                .createdAt(blog.getCreatedAt())
                .updatedAt(blog.getUpdatedAt())
                .build();
    }

    private Blog mapToEntity(BlogDTO dto) {
        return Blog.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(dto.getAuthor())
                .tags(dto.getTags())
                .build();
    }

    @Override
    public BlogDTO createBlog(BlogDTO blogDTO) {
        Blog blog = mapToEntity(blogDTO);
        return mapToDTO(blogRepository.save(blog));
    }

    @Override
    public List<BlogDTO> getAllBlogs() {
        return blogRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BlogDTO getBlogById(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with Id: " + id));
        return mapToDTO(blog);
    }

    @Override
    public BlogDTO updateBlog(Long id, BlogDTO blogDTO) {
        Blog existingBlog = blogRepository.findById(id)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with Id: " + id));

        existingBlog.setTitle(blogDTO.getTitle());
        existingBlog.setContent(blogDTO.getContent());
        existingBlog.setTags(blogDTO.getTags());
        existingBlog.setAuthor(blogDTO.getAuthor());
        return mapToDTO(blogRepository.save(existingBlog));
    }

    @Override
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

    @Override
    public void react(Long blogId, ReactionType reactionType, User user){
        Blog blog = blogRepository.findById(blogId)
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
    public ReactionType getUserReaction(Long blogId, User user) {
        return blogReactionRepository
                .findByBlogIdAndUserId(blogId, user.getId())
                .map(BlogReaction :: getReactionType)
                .orElse(null);
    }

}
