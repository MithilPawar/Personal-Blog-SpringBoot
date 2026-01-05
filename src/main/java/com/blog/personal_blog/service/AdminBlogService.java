package com.blog.personal_blog.service;

import com.blog.personal_blog.dto.AdminBlogRequestDTO;
import com.blog.personal_blog.dto.BlogResponseDTO;
import com.blog.personal_blog.model.Blog;
import com.blog.personal_blog.model.User;
import com.blog.personal_blog.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminBlogService {
    private final BlogRepository blogRepository;

    public AdminBlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    private BlogResponseDTO toResponse(Blog blog) {
        return BlogResponseDTO.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .author(blog.getAuthor())
                .tags(blog.getTags())
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

    public Object updateBlog(Long id, AdminBlogRequestDTO adminBlogRequestDTO) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        blog.setTitle(adminBlogRequestDTO.getTitle());
        blog.setContent(adminBlogRequestDTO.getContent());
        blog.setTags(adminBlogRequestDTO.getTags());
        blog.setAuthor(adminBlogRequestDTO.getAuthor());

        return toResponse(blogRepository.save(blog));
    }

    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

    public BlogResponseDTO togglePublish(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        blog.setPublished(!blog.isPublished());
        return toResponse(blogRepository.save(blog));
    }

    public List<BlogResponseDTO> getAllBlogsForAdmin() {
        return blogRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
