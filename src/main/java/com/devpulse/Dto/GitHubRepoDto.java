package com.devpulse.Dto;

import lombok.Data;

@Data
public class GitHubRepoDto {

    private Long id;
    private String name;
    private String full_name;
    private String language;
    private Integer stargazers_count;
    private Integer forks_count;
}
