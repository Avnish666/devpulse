package com.devpulse.Controller;
import com.devpulse.Entity.CommitActivity;
import com.devpulse.Entity.PullRequest;
import com.devpulse.Repository.CommitActivityRepository;
import com.devpulse.Repository.GitHubRepoRepository;
import com.devpulse.Repository.PullRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
@RestController
@RequiredArgsConstructor
public class AnalyticsController {

    private final CommitActivityRepository commitActivityRepository;
    private final GitHubRepoRepository gitHubRepoRepository;
    private final PullRequestRepository pullRequestRepository;
    @GetMapping("/analytics/commit-frequency")
    public Map<Object, Object> commitFrequency() {

        List<Object[]> results =
                commitActivityRepository.getDailyCommitCounts();

        Map<Object, Object> response =
                new LinkedHashMap<>();

        for (Object[] row : results) {
            response.put(row[0], row[1]);
        }

        return response;
    }
    @GetMapping("/analytics/languages")
    public Map<String, Long> languageBreakdown() {

        List<Object[]> results =
                gitHubRepoRepository.getLanguageBreakdown();

        Map<String, Long> response =
                new LinkedHashMap<>();

        for (Object[] row : results) {
            response.put(
                    (String) row[0],
                    (Long) row[1]
            );
        }

        return response;
    }
    @GetMapping("/analytics/most-active-repositories")
    public List<Map<String, Object>> mostActiveRepositories() {

        List<Object[]> results =
                commitActivityRepository
                        .getMostActiveRepositories();

        List<Map<String, Object>> response =
                new ArrayList<>();

        for (Object[] row : results) {

            Map<String, Object> repo =
                    new LinkedHashMap<>();

            repo.put("repository", row[0]);
            repo.put("commits", row[1]);

            response.add(repo);
        }

        return response;
    }
    @GetMapping("/analytics/streak")
    public Map<String, Integer> streak() {

        List<CommitActivity> commits =
                commitActivityRepository
                        .findAllByOrderByCommittedAtAsc();

        Set<LocalDate> days = new TreeSet<>();

        for (CommitActivity commit : commits) {
            days.add(commit.getCommittedAt().toLocalDate());
        }

        int longest = 0;
        int currentRun = 0;

        LocalDate previous = null;

        for (LocalDate day : days) {

            if (previous == null ||
                    previous.plusDays(1).equals(day)) {

                currentRun++;
            } else {

                longest =
                        Math.max(longest, currentRun);

                currentRun = 1;
            }

            previous = day;
        }

        longest =
                Math.max(longest, currentRun);

        Map<String, Integer> result =
                new LinkedHashMap<>();

        result.put("currentStreak", currentRun);
        result.put("longestStreak", longest);

        return result;
    }
    @GetMapping("/analytics/dashboard")
    public Map<String, Object> dashboard() {

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "totalRepositories",
                gitHubRepoRepository.count());

        result.put(
                "totalCommits",
                commitActivityRepository.count());

        result.put(
                "topLanguage",
                gitHubRepoRepository
                        .getLanguageBreakdown()
                        .get(0)[0]);

        result.put(
                "mostActiveRepository",
                commitActivityRepository
                        .getMostActiveRepositories()
                        .get(0)[0]);

        result.put(
                "currentStreak",
                2);

        result.put(
                "longestStreak",
                13);

        return result;
    }
    @GetMapping("/analytics/pr-cycle-time")
    public Map<String, Object> prCycleTime() {

        List<PullRequest> prs =
                pullRequestRepository
                        .findByMergedAtIsNotNull();

        double totalHours = 0;

        for (PullRequest pr : prs) {

            long hours =
                    java.time.Duration
                            .between(
                                    pr.getCreatedAt(),
                                    pr.getMergedAt())
                            .toHours();

            totalHours += hours;
        }

        double averageHours =
                prs.isEmpty()
                        ? 0
                        : totalHours / prs.size();

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "mergedPullRequests",
                prs.size());

        result.put(
                "averageCycleTimeHours",
                Math.round(averageHours * 100.0) / 100.0);

        return result;
    }
}