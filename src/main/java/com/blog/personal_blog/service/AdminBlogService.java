package com.blog.personal_blog.service;

import com.blog.personal_blog.dto.AdminBlogRequestDTO;
import com.blog.personal_blog.dto.BlogResponseDTO;
import com.blog.personal_blog.exception.BlogNotFoundException;
import com.blog.personal_blog.model.Blog;
import com.blog.personal_blog.model.User;
import com.blog.personal_blog.repository.BlogRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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
}
