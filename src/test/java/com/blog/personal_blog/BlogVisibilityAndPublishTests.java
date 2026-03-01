package com.blog.personal_blog;

import com.blog.personal_blog.Enum.Role;
import com.blog.personal_blog.model.Blog;
import com.blog.personal_blog.model.User;
import com.blog.personal_blog.repository.UserBlogRepository;
import com.blog.personal_blog.repository.UserProfileRepository;
import com.blog.personal_blog.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BlogVisibilityAndPublishTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserBlogRepository userBlogRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void publishedBlogShouldBeVisibleToPublicUsers() throws Exception {
        Blog blog = userBlogRepository.save(Blog.builder()
                .title("Published Blog")
                .content("Visible content")
                .author("author")
                .tags("java")
                .published(true)
                .build());

        mockMvc.perform(get("/api/blogs/" + blog.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(blog.getId()))
                .andExpect(jsonPath("$.title").value("Published Blog"));
    }

    @Test
    void draftBlogShouldNotBeVisibleToPublicUsers() throws Exception {
        Blog draft = userBlogRepository.save(Blog.builder()
                .title("Draft Blog")
                .content("Hidden content")
                .author("author")
                .tags("spring")
                .published(false)
                .build());

        mockMvc.perform(get("/api/blogs/" + draft.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void adminShouldBeAbleToToggleBlogPublishStatus() throws Exception {
        String username = ("admin" + UUID.randomUUID().toString().replace("-", "")).substring(0, 18);

        User admin = userProfileRepository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode("password123"))
                .role(Role.ADMIN)
                .enabled(true)
                .build());

        Blog draft = userBlogRepository.save(Blog.builder()
                .title("Toggle Blog")
                .content("Toggle content")
                .author(admin.getUsername())
                .tags("react")
                .published(false)
                .build());

        String adminToken = jwtUtil.generateToken(admin.getUsername(), Role.ADMIN.name());

        mockMvc.perform(patch("/api/admin/blogs/" + draft.getId() + "/publish")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(draft.getId()))
                .andExpect(jsonPath("$.published").value(true));

        Blog updated = userBlogRepository.findById(draft.getId()).orElseThrow();
        assertTrue(updated.isPublished());
    }
}
