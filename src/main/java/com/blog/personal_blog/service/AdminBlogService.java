package com.blog.personal_blog.service;

import com.blog.personal_blog.Enum.ReactionType;
import com.blog.personal_blog.dto.AdminBlogRequestDTO;
import com.blog.personal_blog.dto.BlogResponseDTO;
import com.blog.personal_blog.dto.CommentResponseDTO;
import com.blog.personal_blog.exception.BlogNotFoundException;
import com.blog.personal_blog.model.Blog;
import com.blog.personal_blog.model.User;
import com.blog.personal_blog.repository.BlogReactionRepository;
import com.blog.personal_blog.repository.BlogRepository;
import com.blog.personal_blog.repository.CommentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AdminBlogService {
    private final BlogRepository blogRepository;
    private final BlogReactionRepository blogReactionRepository;
    private final CommentRepository commentRepository;

    public AdminBlogService(BlogRepository blogRepository, BlogReactionRepository blogReactionRepository, CommentRepository commentRepository) {
        this.blogRepository = blogRepository;
        this.blogReactionRepository = blogReactionRepository;
        this.commentRepository = commentRepository;
    }

    private BlogResponseDTO toResponse(Blog blog) {
        return BlogResponseDTO.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .author(blog.getAuthor())
                .tags(blog.getTags())
                .likes(blogReactionRepository.countByBlogIdAndReactionType(blog.getId(), ReactionType.LIKE))
                .dislikes(blogReactionRepository.countByBlogIdAndReactionType(blog.getId(), ReactionType.DISLIKE))
                .commentCount(commentRepository.countByBlogId(blog.getId()))
                .published(blog.isPublished())
                .createdAt(blog.getCreatedAt())
                .build();
    }

    public BlogResponseDTO createBlog(AdminBlogRequestDTO adminBlogRequestDTO, User admin) {
        Blog blog = Blog.builder()
                .title(adminBlogRequestDTO.getTitle())
                .content(adminBlogRequestDTO.getContent())
                .tags(adminBlogRequestDTO.getTags())
                .author(admin.getUsername())
                .published(false)
                .build();

        Blog saved = blogRepository.save(blog);
        return toResponse(saved);
    }

    public BlogResponseDTO updateBlog(Long id, AdminBlogRequestDTO adminBlogRequestDTO) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        blog.setTitle(adminBlogRequestDTO.getTitle());
        blog.setContent(adminBlogRequestDTO.getContent());
        blog.setTags(adminBlogRequestDTO.getTags());
        blog.setAuthor(adminBlogRequestDTO.getAuthor());

        return toResponse(blogRepository.save(blog));
    }

    public void deleteBlog(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found"));

        if (blog.isPublished()) {
            throw new IllegalStateException("Cannot delete a published blog");
        }

        blogRepository.delete(blog);
    }

    public BlogResponseDTO togglePublish(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found"));

        blog.setPublished(!blog.isPublished());
        return toResponse(blogRepository.save(blog));
    }

    public List<BlogResponseDTO> getAllBlogsForAdmin(
            String status,
            String search,
            String sortBy,
            String order) {

        Set<String> allowedSortFields = Set.of("createdAt", "updatedAt");

        if(!allowedSortFields.contains(sortBy)){
            sortBy = "createdAt";
        }

        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, sortBy);

        boolean hasStatus = status != null && !status.equalsIgnoreCase("ALL");
        boolean hasSearch = search != null && !search.isBlank();

        List<Blog> blogs;

        if(hasStatus && hasSearch){
            boolean published = status.equalsIgnoreCase("PUBLISHED");
            blogs = blogRepository.findByPublishedAndTitleContainingIgnoreCase(
                    published, search, sort);
        }else if(hasStatus){
            boolean published = status.equalsIgnoreCase("PUBLISHED");
            blogs = blogRepository.findByPublished(published, sort);
        } else if (hasSearch) {
            blogs = blogRepository.findByTitleContainingIgnoreCase(search, sort);
        }else{
            blogs = blogRepository.findAll(sort);
        }

        return blogs.stream()
                .map(this::toResponse)
                .toList();
    }

    public BlogResponseDTO getBlogById(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id: " + id));;

        return toResponse(blog);
    }

    public List<CommentResponseDTO> getCommentByBlogId(Long blogId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with Id: " + blogId));

        return commentRepository.findByBlogOrderByCreatedAtDesc(blog)
                .stream()
                .map(comment -> CommentResponseDTO.builder()
                        .id(comment.getId())
                        .authorName(comment.getUser().getUsername())
                        .text(comment.getText())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .toList();
    }
}
