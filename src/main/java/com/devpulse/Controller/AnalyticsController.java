package com.devpulse.Controller;
import com.devpulse.Dto.ContributionDay;
import com.devpulse.Entity.CommitActivity;
import com.devpulse.Entity.PullRequest;
import com.devpulse.Repository.CommitActivityRepository;
import com.devpulse.Repository.GitHubRepoRepository;
import com.devpulse.Repository.PullRequestRepository;
import com.devpulse.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.devpulse.Security.SecurityUtils;
import com.devpulse.Entity.User;

import java.time.LocalDate;
import java.util.*;
@RestController
@RequiredArgsConstructor
public class AnalyticsController {

    private final UserRepository userRepository;
    private final CommitActivityRepository commitActivityRepository;
    private final GitHubRepoRepository gitHubRepoRepository;
    private final PullRequestRepository pullRequestRepository;
    private User getCurrentUser() {

        Long userId = SecurityUtils.getCurrentUserId();

        return userRepository
                .findById(userId)
                .orElseThrow();
    }
    @GetMapping("/analytics/commit-frequency")
    @Cacheable(value = "commitFrequency", key = "T(com.devpulse.Security.SecurityUtils).getCurrentUserId()")
    public Map<Object, Object> commitFrequency() {

        System.out.println("Commit Frequency API executed");

        User user = getCurrentUser();

        List<Object[]> results =
                commitActivityRepository
                        .getDailyCommitCounts(user);

        Map<Object, Object> response =
                new LinkedHashMap<>();

        for (Object[] row : results) {
            response.put(row[0], row[1]);
        }

        return response;
    }


    @GetMapping("/analytics/languages")
    @Cacheable(value = "languages", key = "T(com.devpulse.Security.SecurityUtils).getCurrentUserId()")
    public Map<String, Long> languageBreakdown() {

        System.out.println("Languages API executed");

        User user = getCurrentUser();

        List<Object[]> results =
                gitHubRepoRepository
                        .getLanguageBreakdown(user);

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
    @Cacheable(value = "repositories",key = "T(com.devpulse.Security.SecurityUtils).getCurrentUserId()")
    public List<Map<String, Object>> mostActiveRepositories() {
        System.out.println("Most Active Repository API executed");
        User user = getCurrentUser();
        List<Object[]> results =
                commitActivityRepository
                        .getMostActiveRepositories(user);

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
    @Cacheable(value = "streak",key = "T(com.devpulse.Security.SecurityUtils).getCurrentUserId()")
    public Map<String, Integer> streak() {
        User user = getCurrentUser();
        List<CommitActivity> commits =
                commitActivityRepository
                        .findByUserOrderByCommittedAtAsc(user);

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
    @Cacheable(
            value = "dashboard",
            key = "T(com.devpulse.Security.SecurityUtils).getCurrentUserId()"
    )
    public Map<String, Object> dashboard() {

        System.out.println("Dashboard API executed from Database");
        User user = getCurrentUser();
        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "totalRepositories",
                gitHubRepoRepository.countByOwner(user));

        result.put(
                "totalCommits",
                commitActivityRepository.countByUser(user));
        List<Object[]> languages = gitHubRepoRepository.getLanguageBreakdown(user);
        result.put(
                "topLanguage",
                languages.isEmpty() ? "N/A" : languages.get(0)[0]);
        List<Object[]> active = commitActivityRepository.getMostActiveRepositories(user);
        result.put(
                "mostActiveRepository",
                active.isEmpty()? "N/A" : active.get(0)[0]);

        Map<String, Integer> streak = streak();

        result.put(
                "currentStreak",
                streak.get("currentStreak"));

        result.put(
                "longestStreak",
                streak.get("longestStreak"));

        return result;
    }
    @GetMapping("/analytics/pr-cycle-time")
    @Cacheable(value = "prCycle",key = "T(com.devpulse.Security.SecurityUtils).getCurrentUserId()")
    public Map<String, Object> prCycleTime() {
        User user = getCurrentUser();
        List<PullRequest> prs =
                pullRequestRepository
                        .findByUserAndMergedAtIsNotNull(user);

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
    @GetMapping("/analytics/contribution-heatmap")
    @Cacheable(value = "contributionHeatmap",key = "T(com.devpulse.Security.SecurityUtils).getCurrentUserId()")
    public List<ContributionDay> contributionHeatmap() {
        User user = getCurrentUser();
        List<Object[]> results =
                commitActivityRepository.getDailyCommitCounts(user);

        Map<LocalDate, Long> commitMap =
                new HashMap<>();

        for (Object[] row : results) {

            commitMap.put(
                    ((java.sql.Date) row[0]).toLocalDate(),
                    ((Number) row[1]).longValue()
            );

        }

        List<ContributionDay> heatmap =
                new ArrayList<>();

        LocalDate today = LocalDate.now();

        LocalDate start =
                today.minusDays(364);

        while (!start.isAfter(today)) {

            heatmap.add(

                    new ContributionDay(

                            start,

                            commitMap.getOrDefault(
                                    start,
                                    0L
                            )
                    )
            );

            start = start.plusDays(1);

        }

        return heatmap;

    }
}