package com.blog.personal_blog.service;

import com.blog.personal_blog.Enum.Role;
import com.blog.personal_blog.config.UserPrincipal;
import com.blog.personal_blog.dto.LoginRequestDTO;
import com.blog.personal_blog.dto.LoginResponseDTO;
import com.blog.personal_blog.dto.RegisterRequestDTO;
import com.blog.personal_blog.model.User;
import com.blog.personal_blog.repository.UserRepository;
import com.blog.personal_blog.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public String register(RegisterRequestDTO registerRequestDTO){
        if(userRepository.findByUsername(registerRequestDTO.getUsername()).isPresent()){
            return "Username already exists!";
        }

        User user = User.builder()
                .username(registerRequestDTO.getUsername())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        return "User registered successfully!";
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtUtil.generateToken(
                userPrincipal.getUsername(),
                userPrincipal.getUser().getRole().name());

        System.out.println(userPrincipal.getUser().getRole().name());
        return new LoginResponseDTO(
                token,
                userPrincipal.getUsername(),
                userPrincipal.getUser().getRole().name()
        );
    }
}
