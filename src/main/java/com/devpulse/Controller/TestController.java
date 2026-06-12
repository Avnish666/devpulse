package com.devpulse.Controller;
import com.devpulse.Entity.User;
import com.devpulse.Repository.UserRepository;
import com.devpulse.Service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

        User user = userRepository.findById(1L).orElseThrow();

        return gitHubService.fetchUserRepos(
                user.getGithubAccessToken()
        );
    }
    @GetMapping("/sync-repos")
    public String syncRepos() {

        gitHubService.saveReposForUser(1L);

        return "Repositories Synced!";
    }
    @GetMapping("/commits")
    public Object commits() {

        User user =
                userRepository.findById(1L)
                        .orElseThrow();

        return gitHubService.fetchRepoCommits(
                user.getGithubAccessToken(),
                "Avnish666",
                "ReplyAPP"
        );
    }
    @GetMapping("/sync-commits")
    public String syncCommits() {

        gitHubService.saveCommitsForRepo(
                1L,
                "Avnish666",
                "ReplyAPP"
        );

        return "Commits Synced!";
    }
    @GetMapping("/sync-all-commits")
    public String syncAllCommits() {

        gitHubService.syncAllCommitsForUser(1L);

        return "All Commits Synced!";
    }
    @GetMapping("/prs")
    public Object prs() {

        User user =
                userRepository.findById(1L)
                        .orElseThrow();

        return gitHubService.fetchPullRequests(
                user.getGithubAccessToken(),
                "Avnish666",
                "dsa-code"
        );
    }
    @GetMapping("/sync-prs")
    public String syncPrs() {

        gitHubService.savePullRequestsForRepo(
                1L,
                "Avnish666",
                "dsa-code"
        );

        return "PRs Synced!";
    }
    @GetMapping("/sync-all-prs")
    public String syncAllPrs() {

        gitHubService.syncAllPullRequestsForUser(1L);

        return "All PRs Synced!";
    }
}
