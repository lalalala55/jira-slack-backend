package com.jiraslack.jira_slack_backend.service;

import com.jiraslack.jira_slack_backend.dto.LoginResponseDTO;
import com.jiraslack.jira_slack_backend.dto.LogoutResponseDTO;
import com.jiraslack.jira_slack_backend.dto.RegisterResponseDTO;
import com.jiraslack.jira_slack_backend.entity.Role;
import com.jiraslack.jira_slack_backend.entity.User;
import com.jiraslack.jira_slack_backend.repository.UserRepository;
import com.jiraslack.jira_slack_backend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public ResponseEntity<RegisterResponseDTO> registerUser(User user) {
        user.setPassword(passwordEncoder.encode((user.getPassword())));
        user.setRoles(List.of(Role.USER));

        try{
            User savedUser = userRepository.save(user);

            String accessToken = jwtUtils.generateToken(user.getUsername());
            String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(RegisterResponseDTO
                            .builder()
                            .token(accessToken)
                            .refreshToken(refreshToken)
                            .build());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RegisterResponseDTO
                            .builder()
                            .message("Invalid Credentials")
                            .build());
        }
    }

    public ResponseEntity<LoginResponseDTO> loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if(user != null && passwordEncoder.matches(password, user.getPassword())) {
            String accessToken = jwtUtils.generateToken(user.getUsername());
            String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(LoginResponseDTO
                            .builder()
                            .token(accessToken)
                            .refreshToken(refreshToken)
                            .build());
        }


        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(LoginResponseDTO
                        .builder()
                        .message("Invalid Credentials")
                        .build());
    }

    public ResponseEntity<LogoutResponseDTO> logoutUser(String refreshToken) {
        if(refreshToken != null && jwtUtils.validTokenFormat(refreshToken))  {
            refreshTokenService.addToBlackList(refreshToken);
            return ResponseEntity.status(HttpStatus.OK).body(LogoutResponseDTO.builder().message("Logout Success").build());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(LogoutResponseDTO.builder().message("Invalid Token").build());
    }
}
