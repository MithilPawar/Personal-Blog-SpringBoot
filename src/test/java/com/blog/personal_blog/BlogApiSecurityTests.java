package com.blog.personal_blog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BlogApiSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicBlogsPagedEndpointShouldBeAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/blogs/paged"))
                .andExpect(status().isOk());
    }

    @Test
    void reactionStatusShouldReturnUnauthorizedWithoutUserPrincipal() throws Exception {
        mockMvc.perform(get("/api/blogs/1/reaction/status"))
                .andExpect(status().isUnauthorized());
    }
}
