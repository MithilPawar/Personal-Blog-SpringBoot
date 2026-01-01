package com.blog.personal_blog.service;

import com.blog.personal_blog.dto.BlogDTO;
import com.blog.personal_blog.exception.BlogNotFoundException;
import com.blog.personal_blog.model.Blog;
import com.blog.personal_blog.model.BlogLike;
import com.blog.personal_blog.model.User;
import com.blog.personal_blog.repository.BlogLikeRepository;
import com.blog.personal_blog.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService{
    private final BlogRepository blogRepository;
    private final BlogLikeRepository blogLikeRepository;

    public BlogServiceImpl(BlogRepository blogRepository, BlogLikeRepository blogLikeRepository){
        this.blogRepository  = blogRepository;
        this.blogLikeRepository = blogLikeRepository;
    }

    private BlogDTO mapToDTO(Blog blog) {
        return BlogDTO.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .author(blog.getAuthor())
                .tags(blog.getTags())
                .likes(blogLikeRepository.countByBlogId(blog.getId()))
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
    public Boolean toggleLike(Long blogId, User user){
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with Id: " + blogId));

        Optional<BlogLike> existingLike = blogLikeRepository.findByBlogIdAndUserId(blogId, user.getId());

        if(existingLike.isPresent()){
            blogLikeRepository.delete(existingLike.get());
            return false;
        }else{
            BlogLike blogLike = BlogLike.builder()
                    .blog(blog)
                    .user(user)
                    .build();

            blogLikeRepository.save(blogLike);
            return true;
        }
    }

    @Override
    public long getLikeCount(Long blogId) {
        return blogLikeRepository.countByBlogId(blogId);
    }

    @Override
    public boolean isLikedByUser(Long blogId, User user) {
        return blogLikeRepository
                .findByBlogIdAndUserId(blogId, user.getId())
                .isPresent();
    }

}
