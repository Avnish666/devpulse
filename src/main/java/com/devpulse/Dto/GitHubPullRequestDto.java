package com.devpulse.Dto;

import lombok.Data;

@Data
public class GitHubPullRequestDto {

    private Long id;

    private String title;

    private String state;

    private String created_at;

    private String closed_at;

    private String merged_at;

    private User user;

    @Data
    public static class User {
        private String login;
    }
}