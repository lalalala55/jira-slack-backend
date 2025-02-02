package com.jiraslack.jira_slack_backend.service;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RefreshTokenService {
    private Set<String> refreshTokens = new HashSet<>();

    public void addToBlackList(String token) {
        refreshTokens.add(token);
    }

    public boolean isTokenBlackListed(String token) {
        return refreshTokens.contains(token);
    }
}
