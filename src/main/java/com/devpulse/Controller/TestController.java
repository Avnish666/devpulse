package com.devpulse.Controller;
import com.devpulse.Entity.User;
import com.devpulse.Repository.UserRepository;
import com.devpulse.Service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.devpulse.Security.SecurityUtils;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "DevPulse Backend Running";
    }
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GitHubService gitHubService;

    @GetMapping("/repos")
    public Object repos() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow();

        return gitHubService.fetchUserRepos(
                user.getGithubAccessToken()
        );
    }
    @GetMapping("/sync-repos")
    public String syncRepos() {
        Long userId = SecurityUtils.getCurrentUserId();
        gitHubService.saveReposForUser(userId);

        return "Repositories Synced!";
    }
    @GetMapping("/commits")
    public Object commits() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user =
                userRepository.findById(userId)
                        .orElseThrow();

        return gitHubService.fetchRepoCommits(
                user.getGithubAccessToken(),
                "Avnish666",
                "ReplyAPP"
        );
    }
    @GetMapping("/sync-commits")
    public String syncCommits() {
        Long userId = SecurityUtils.getCurrentUserId();
        gitHubService.saveCommitsForRepo(
                userId,
                "Avnish666",
                "ReplyAPP"
        );

        return "Commits Synced!";
    }
    @GetMapping("/sync-all-commits")
    public String syncAllCommits() {
        Long userId = SecurityUtils.getCurrentUserId();
        gitHubService.syncAllCommitsForUser(userId);

        return "All Commits Synced!";
    }
    @GetMapping("/prs")
    public Object prs() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user =
                userRepository.findById(userId)
                        .orElseThrow();

        return gitHubService.fetchPullRequests(
                user.getGithubAccessToken(),
                "Avnish666",
                "dsa-code"
        );
    }
    @GetMapping("/sync-prs")
    public String syncPrs() {
        Long userId = SecurityUtils.getCurrentUserId();
        gitHubService.savePullRequestsForRepo(
                userId,
                "Avnish666",
                "dsa-code"
        );

        return "PRs Synced!";
    }
    @GetMapping("/sync-all-prs")
    public String syncAllPrs() {
        Long userId = SecurityUtils.getCurrentUserId();
        gitHubService.syncAllPullRequestsForUser(userId);

        return "All PRs Synced!";
    }
}
