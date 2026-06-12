package com.devpulse.Repository;
import com.devpulse.Entity.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PullRequestRepository
        extends JpaRepository<PullRequest, Long> {

    Optional<PullRequest> findByGithubPrId(Long githubPrId);
    List<PullRequest> findByMergedAtIsNotNull();
}