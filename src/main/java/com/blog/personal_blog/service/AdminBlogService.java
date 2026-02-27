package com.blog.personal_blog.service;

import com.blog.personal_blog.Enum.ReactionType;
import com.blog.personal_blog.dto.AdminBlogRequestDTO;
import com.blog.personal_blog.dto.BlogResponseDTO;
import com.blog.personal_blog.exception.BlogNotFoundException;
import com.blog.personal_blog.model.Blog;
import com.blog.personal_blog.model.User;
import com.blog.personal_blog.repository.AdminCommentRepository;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminBlogService {
    private final UserBlogRepository userBlogRepository;
    private final BlogReactionRepository blogReactionRepository;
    private final AdminCommentRepository adminCommentRepository;

    public AdminBlogService(UserBlogRepository userBlogRepository, BlogReactionRepository blogReactionRepository, UserCommentRepository userCommentRepository, AdminCommentRepository adminCommentRepository) {
        this.userBlogRepository = userBlogRepository;
        this.blogReactionRepository = blogReactionRepository;
        this.adminCommentRepository = adminCommentRepository;
    }

    private BlogResponseDTO toResponse(Blog blog, long likes, long dislikes, long commentCount) {
        return BlogResponseDTO.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .author(blog.getAuthor())
                .tags(blog.getTags())
                .likes(likes)
                .dislikes(dislikes)
                .commentCount(commentCount)
                .published(blog.isPublished())
                .createdAt(blog.getCreatedAt())
                .build();
    }

    private List<BlogResponseDTO> mapBlogsWithAggregates(List<Blog> blogs) {
        if (blogs.isEmpty()) {
            return List.of();
        }

        // REVIEW NOTE: Aggregate counts once for the full list to avoid N+1 query overhead.
        List<Long> blogIds = blogs.stream().map(Blog::getId).toList();
        Map<Long, Long> likeCounts = new HashMap<>();
        Map<Long, Long> dislikeCounts = new HashMap<>();
        Map<Long, Long> commentCounts = new HashMap<>();

        for (Object[] row : blogReactionRepository.countReactionsByBlogIds(blogIds)) {
            Long blogId = (Long) row[0];
            ReactionType reactionType = (ReactionType) row[1];
            Long count = (Long) row[2];

            if (reactionType == ReactionType.LIKE) {
                likeCounts.put(blogId, count);
            } else if (reactionType == ReactionType.DISLIKE) {
                dislikeCounts.put(blogId, count);
            }
        }

        for (Object[] row : adminCommentRepository.countCommentsByBlogIds(blogIds)) {
            commentCounts.put((Long) row[0], (Long) row[1]);
        }

        return blogs.stream()
                .map(blog -> toResponse(
                        blog,
                        likeCounts.getOrDefault(blog.getId(), 0L),
                        dislikeCounts.getOrDefault(blog.getId(), 0L),
                        commentCounts.getOrDefault(blog.getId(), 0L)
                ))
                .toList();
    }

    public BlogResponseDTO createBlog(AdminBlogRequestDTO adminBlogRequestDTO, User admin) {
        Blog blog = Blog.builder()
                .title(adminBlogRequestDTO.getTitle())
                .content(adminBlogRequestDTO.getContent())
                .tags(adminBlogRequestDTO.getTags())
                .author(admin.getUsername())
                .published(false)
                .build();

        Blog saved = userBlogRepository.save(blog);
        return toResponse(saved, 0L, 0L, 0L);
    }

    public BlogResponseDTO updateBlog(Long id, AdminBlogRequestDTO adminBlogRequestDTO) {
        Blog blog = userBlogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        blog.setTitle(adminBlogRequestDTO.getTitle());
        blog.setContent(adminBlogRequestDTO.getContent());
        blog.setTags(adminBlogRequestDTO.getTags());
        blog.setAuthor(adminBlogRequestDTO.getAuthor());

        Blog updated = userBlogRepository.save(blog);
        long likes = blogReactionRepository.countByBlogIdAndReactionType(updated.getId(), ReactionType.LIKE);
        long dislikes = blogReactionRepository.countByBlogIdAndReactionType(updated.getId(), ReactionType.DISLIKE);
        long commentCount = adminCommentRepository.countByBlogId(updated.getId());
        return toResponse(updated, likes, dislikes, commentCount);
    }

    public void deleteBlog(Long id) {
        Blog blog = userBlogRepository.findById(id)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found"));

        if (blog.isPublished()) {
            throw new IllegalStateException("Cannot delete a published blog");
        }

        userBlogRepository.delete(blog);
    }

    public BlogResponseDTO togglePublish(Long id) {
        Blog blog = userBlogRepository.findById(id)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found"));

        blog.setPublished(!blog.isPublished());
        Blog updated = userBlogRepository.save(blog);
        long likes = blogReactionRepository.countByBlogIdAndReactionType(updated.getId(), ReactionType.LIKE);
        long dislikes = blogReactionRepository.countByBlogIdAndReactionType(updated.getId(), ReactionType.DISLIKE);
        long commentCount = adminCommentRepository.countByBlogId(updated.getId());
        return toResponse(updated, likes, dislikes, commentCount);
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
            blogs = userBlogRepository.findByPublishedAndTitleContainingIgnoreCase(
                    published, search, sort);
        }else if(hasStatus){
            boolean published = status.equalsIgnoreCase("PUBLISHED");
            blogs = userBlogRepository.findByPublished(published, sort);
        } else if (hasSearch) {
            blogs = userBlogRepository.findByTitleContainingIgnoreCase(search, sort);
        }else{
            blogs = userBlogRepository.findAll(sort);
        }

        return mapBlogsWithAggregates(blogs);
    }

    public Page<BlogResponseDTO> getAllBlogsForAdminPaged(
            String status,
            String search,
            String sortBy,
            String order,
            int page,
            int size
    ) {
        Set<String> allowedSortFields = Set.of("createdAt", "updatedAt");

        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "createdAt";
        }

        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 10 : Math.min(size, 100);

        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(direction, sortBy));

        boolean hasStatus = status != null && !status.equalsIgnoreCase("ALL");
        boolean hasSearch = search != null && !search.isBlank();

        Page<Blog> blogPage;

        if (hasStatus && hasSearch) {
            boolean published = status.equalsIgnoreCase("PUBLISHED");
            blogPage = userBlogRepository.findByPublishedAndTitleContainingIgnoreCase(
                    published, search, pageable);
        } else if (hasStatus) {
            boolean published = status.equalsIgnoreCase("PUBLISHED");
            blogPage = userBlogRepository.findByPublished(published, pageable);
        } else if (hasSearch) {
            blogPage = userBlogRepository.findByTitleContainingIgnoreCase(search, pageable);
        } else {
            blogPage = userBlogRepository.findAll(pageable);
        }

        // REVIEW NOTE: Keep aggregated count optimization on paginated data as well.
        List<BlogResponseDTO> responseDTOs = mapBlogsWithAggregates(blogPage.getContent());
        Map<Long, BlogResponseDTO> dtoById = responseDTOs.stream()
                .collect(Collectors.toMap(BlogResponseDTO::getId, dto -> dto));

        List<BlogResponseDTO> orderedDtos = blogPage.getContent().stream()
                .map(blog -> dtoById.get(blog.getId()))
                .toList();

        return new PageImpl<>(orderedDtos, pageable, blogPage.getTotalElements());
    }

    public BlogResponseDTO getBlogById(Long id) {
        Blog blog = userBlogRepository.findById(id)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id: " + id));;

        long likes = blogReactionRepository.countByBlogIdAndReactionType(blog.getId(), ReactionType.LIKE);
        long dislikes = blogReactionRepository.countByBlogIdAndReactionType(blog.getId(), ReactionType.DISLIKE);
        long commentCount = adminCommentRepository.countByBlogId(blog.getId());
        return toResponse(blog, likes, dislikes, commentCount);
    }
}
