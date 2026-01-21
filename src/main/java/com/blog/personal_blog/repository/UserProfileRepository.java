package com.blog.personal_blog.repository;

import com.blog.personal_blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
