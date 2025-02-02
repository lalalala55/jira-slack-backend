package com.jiraslack.jira_slack_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "Backend is running!";
    }

    @GetMapping("/testAuth")
    public String authCheck() {
        return "Success";
    }
}
