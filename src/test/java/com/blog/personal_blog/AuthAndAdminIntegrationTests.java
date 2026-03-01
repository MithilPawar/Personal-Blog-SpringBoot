package com.blog.personal_blog;

import com.blog.personal_blog.Enum.Role;
import com.blog.personal_blog.model.User;
import com.blog.personal_blog.repository.UserProfileRepository;
import com.blog.personal_blog.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthAndAdminIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

        private String shortUsername(String prefix) {
                return (prefix + UUID.randomUUID().toString().replace("-", "")).substring(0, 18);
        }

    @Test
    void registerShouldReturnTokenAndUserRole() throws Exception {
                String username = shortUsername("u");

        String requestBody = """
                {
                  "username": "%s",
                  "password": "password123"
                }
                """.formatted(username);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void loginShouldSucceedWithValidCredentials() throws Exception {
                String username = shortUsername("u");

        String registerBody = """
                {
                  "username": "%s",
                  "password": "password123"
                }
                """.formatted(username);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerBody)).andExpect(status().isOk());

        String loginBody = """
                {
                  "username": "%s",
                  "password": "password123"
                }
                """.formatted(username);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void loginShouldFailWithInvalidCredentials() throws Exception {
                String username = shortUsername("u");

        String registerBody = """
                {
                  "username": "%s",
                  "password": "password123"
                }
                """.formatted(username);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerBody)).andExpect(status().isOk());

        String invalidLoginBody = """
                {
                  "username": "%s",
                  "password": "wrong-password"
                }
                """.formatted(username);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidLoginBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void adminEndpointShouldRejectUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/admin/blogs"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpointShouldRejectUserRoleToken() throws Exception {
                String username = shortUsername("u");
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode("password123"))
                .role(Role.USER)
                .enabled(true)
                .build();
        userProfileRepository.save(user);

        String userToken = jwtUtil.generateToken(username, Role.USER.name());

        mockMvc.perform(get("/api/admin/blogs")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpointShouldAllowAdminRoleToken() throws Exception {
                String username = shortUsername("a");
        User admin = User.builder()
                .username(username)
                .password(passwordEncoder.encode("password123"))
                .role(Role.ADMIN)
                .enabled(true)
                .build();
        userProfileRepository.save(admin);

        String adminToken = jwtUtil.generateToken(username, Role.ADMIN.name());

        mockMvc.perform(get("/api/admin/blogs")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
