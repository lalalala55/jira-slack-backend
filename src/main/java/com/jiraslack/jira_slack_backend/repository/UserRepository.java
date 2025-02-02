package com.jiraslack.jira_slack_backend.repository;

import com.jiraslack.jira_slack_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String user);
    User findByEmail(String email);
}
