package com.devpulse.Service;

import com.devpulse.Dto.GitHubCommitDto;
import com.devpulse.Dto.GitHubPullRequestDto;
import com.devpulse.Dto.GitHubRepoDto;
import com.devpulse.Entity.CommitActivity;
import com.devpulse.Entity.GitHubRepo;
import com.devpulse.Entity.PullRequest;
import com.devpulse.Entity.User;
import com.devpulse.Repository.CommitActivityRepository;
import com.devpulse.Repository.GitHubRepoRepository;
import com.devpulse.Repository.PullRequestRepository;
import com.devpulse.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GitHubService {
    private final CommitActivityRepository commitActivityRepository;
    private final WebClient.Builder webClientBuilder;
    private final GitHubRepoRepository gitHubRepoRepository;
    private final PullRequestRepository pullRequestRepository;
    private final UserRepository userRepository;
    private WebClient getClient(String token) {
        return webClientBuilder
                .baseUrl("https://api.github.com")
                .defaultHeader("Authorization",
                        "Bearer " + token)
                .defaultHeader("Accept",
                        "application/vnd.github+json")
                .build();
    }
    @Cacheable("repositories")
    public List<GitHubRepoDto> fetchUserRepos(String token) {
        System.out.println("Fetching repositories from GitHub API...");
        return getClient(token)
                .get()
                .uri("/user/repos?per_page=100")
                .retrieve()
                .bodyToFlux(GitHubRepoDto.class)
                .collectList()
                .block();
    }
    @Cacheable(
            value = "commits",
            key = "#owner + '-' + #repo"
    )
    public List<GitHubCommitDto> fetchRepoCommits(
            String token,
            String owner,
            String repo) {
        System.out.println("Fetching commits from GitHub API...");
        return getClient(token)
                .get()
                .uri("/repos/{owner}/{repo}/commits?per_page=100",
                        owner,
                        repo)
                .retrieve()
                .bodyToFlux(GitHubCommitDto.class)
                .collectList()
                .block();
    }
    public void saveReposForUser(Long userId) {

        User user = userRepository
                .findById(userId)
                .orElseThrow();

        List<GitHubRepoDto> repos =
                fetchUserRepos(user.getGithubAccessToken());
        if (repos == null) {
            return;
        }
        for (GitHubRepoDto dto : repos) {

            GitHubRepo repo = gitHubRepoRepository
                    .findByGithubRepoId(
                            String.valueOf(dto.getId()))
                    .orElse(new GitHubRepo());

            repo.setGithubRepoId(
                    String.valueOf(dto.getId()));

            repo.setName(dto.getName());

            repo.setFullName(dto.getFull_name());

            repo.setLanguage(dto.getLanguage());

            repo.setStars(dto.getStargazers_count());

            repo.setForks(dto.getForks_count());

            repo.setOwner(user);

            gitHubRepoRepository.save(repo);
        }

    }
    public void saveCommitsForRepo(
            Long userId,
            String owner,
            String repoName) {

        User user = userRepository
                .findById(userId)
                .orElseThrow();

        List<GitHubCommitDto> commits =
                fetchRepoCommits(
                        user.getGithubAccessToken(),
                        owner,
                        repoName
                );

        if (commits == null) {
            return;
        }

        for (GitHubCommitDto dto : commits) {

            if (commitActivityRepository
                    .existsBySha(dto.getSha())) {
                continue;
            }

            CommitActivity commit =
                    CommitActivity.builder()
                            .repoFullName(owner + "/" + repoName)
                            .sha(dto.getSha())
                            .message(dto.getCommit().getMessage())
                            .authorName(
                                    dto.getCommit()
                                            .getAuthor()
                                            .getName())
                            .committedAt(
                                    java.time.OffsetDateTime
                                            .parse(
                                                    dto.getCommit()
                                                            .getAuthor()
                                                            .getDate())
                                            .toLocalDateTime())
                            .user(user)
                            .build();

            commitActivityRepository.save(commit);
        }
    }
    public void syncAllCommitsForUser(Long userId) {

        User user = userRepository
                .findById(userId)
                .orElseThrow();

        List<GitHubRepo> repos =
                gitHubRepoRepository.findByOwner(user);

        for (GitHubRepo repo : repos) {

            String[] parts =
                    repo.getFullName().split("/");

            saveCommitsForRepo(
                    userId,
                    parts[0],
                    parts[1]
            );
        }
    }
    @Cacheable(
            value = "pullRequests",
            key = "#owner + '-' + #repo"
    )
    public List<GitHubPullRequestDto> fetchPullRequests(
            String token,
            String owner,
            String repo) {
        System.out.println("Fetching PRs from GitHub API...");
        return getClient(token)
                .get()
                .uri("/repos/{owner}/{repo}/pulls?state=all&per_page=100",
                        owner,
                        repo)
                .retrieve()
                .bodyToFlux(GitHubPullRequestDto.class)
                .collectList()
                .block();
    }
    public void savePullRequestsForRepo(
            Long userId,
            String owner,
            String repoName) {

        User user = userRepository
                .findById(userId)
                .orElseThrow();

        List<GitHubPullRequestDto> prs =
                fetchPullRequests(
                        user.getGithubAccessToken(),
                        owner,
                        repoName);

        for (GitHubPullRequestDto dto : prs) {

            PullRequest pr =
                    pullRequestRepository
                            .findByGithubPrId(dto.getId())
                            .orElse(new PullRequest());

            pr.setGithubPrId(dto.getId());

            pr.setRepoFullName(
                    owner + "/" + repoName);

            pr.setTitle(dto.getTitle());

            pr.setState(dto.getState());

            pr.setAuthor(
                    dto.getUser().getLogin());

            pr.setCreatedAt(
                    java.time.OffsetDateTime
                            .parse(dto.getCreated_at())
                            .toLocalDateTime());

            if (dto.getClosed_at() != null) {
                pr.setClosedAt(
                        java.time.OffsetDateTime
                                .parse(dto.getClosed_at())
                                .toLocalDateTime());
            }

            if (dto.getMerged_at() != null) {
                pr.setMergedAt(
                        java.time.OffsetDateTime
                                .parse(dto.getMerged_at())
                                .toLocalDateTime());
            }

            pr.setUser(user);

            pullRequestRepository.save(pr);
        }
    }
    public void syncAllPullRequestsForUser(Long userId) {

        User user = userRepository
                .findById(userId)
                .orElseThrow();

        List<GitHubRepo> repos =
                gitHubRepoRepository.findByOwner(user);

        for (GitHubRepo repo : repos) {

            String[] parts =
                    repo.getFullName().split("/");

            savePullRequestsForRepo(
                    userId,
                    parts[0],
                    parts[1]
            );
        }
    }
}