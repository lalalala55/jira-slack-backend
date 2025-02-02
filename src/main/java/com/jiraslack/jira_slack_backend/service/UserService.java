package com.jiraslack.jira_slack_backend.service;

import com.jiraslack.jira_slack_backend.entity.Role;
import com.jiraslack.jira_slack_backend.entity.User;
import com.jiraslack.jira_slack_backend.repository.UserRepository;
import com.jiraslack.jira_slack_backend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode((user.getPassword())));
        user.setRoles(List.of(Role.USER));
        return userRepository.save(user);
    }

    public String loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if(user != null && passwordEncoder.matches(password, user.getPassword())) {
            return jwtUtils.generateToken(user.getUsername());
        }
        return null;
    }

}
