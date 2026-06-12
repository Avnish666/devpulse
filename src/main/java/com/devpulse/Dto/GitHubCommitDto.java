package com.devpulse.Dto;

import lombok.Data;

@Data
public class GitHubCommitDto {

    private String sha;
    private Commit commit;

    @Data
    public static class Commit {
        private String message;
        private Author author;
    }

    @Data
    public static class Author {
        private String name;
        private String date;
    }
}
