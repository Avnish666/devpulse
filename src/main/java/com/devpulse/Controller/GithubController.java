package com.devpulse.Controller;

import com.devpulse.Security.SecurityUtils;
import com.devpulse.Service.GitHubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/github")
@RequiredArgsConstructor
public class GithubController {

    private final GitHubService gitHubService;

    @PostMapping("/sync")
    public ResponseEntity<String> syncGithub() {

        Long userId = SecurityUtils.getCurrentUserId();

        gitHubService.sync(userId);

        return ResponseEntity.ok("GitHub synced successfully.");
    }
}