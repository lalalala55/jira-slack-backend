package com.jiraslack.jira_slack_backend.controller;

import com.jiraslack.jira_slack_backend.dto.*;
import com.jiraslack.jira_slack_backend.entity.User;
import com.jiraslack.jira_slack_backend.security.JwtUtils;
import com.jiraslack.jira_slack_backend.service.RefreshTokenService;
import com.jiraslack.jira_slack_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        String username = loginRequestDTO.getUsername();
        String password = loginRequestDTO.getPassword();
        return userService.loginUser(username, password);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDTO> logout(@RequestBody LogoutRequestDTO logoutRequestDTO) {
        return userService.logoutUser(logoutRequestDTO.getRefreshToken());
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshAccessToken(@RequestBody RefreshRequestDTO refreshRequestDTO) {
        String refreshToken = refreshRequestDTO.getRefreshToken();
        boolean isBlackListed = refreshTokenService.isTokenBlackListed(refreshToken);
        if(isBlackListed) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh token is invalid");
        }
        String username = jwtUtils.extractUsername(refreshToken);
        String newAccessToken = jwtUtils.generateToken(username);
        return ResponseEntity.ok(newAccessToken);
    }
}
