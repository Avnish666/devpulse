package com.devpulse.Repository;
import com.devpulse.Entity.PullRequest;
import com.devpulse.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PullRequestRepository
        extends JpaRepository<PullRequest, Long> {

    Optional<PullRequest> findByGithubPrId(Long githubPrId);
    List<PullRequest> findByUserAndMergedAtIsNotNull(User user);
}