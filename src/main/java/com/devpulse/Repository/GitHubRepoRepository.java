package com.devpulse.Repository;

import com.devpulse.Entity.GitHubRepo;
import com.devpulse.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GitHubRepoRepository extends JpaRepository<GitHubRepo, Long> {

    List<GitHubRepo> findByOwner(User owner);
    Optional<GitHubRepo> findByGithubRepoId(String githubRepoId);
    @Query("""
SELECT r.language, COUNT(r)
FROM GitHubRepo r
WHERE r.owner = :owner
AND r.language IS NOT NULL
GROUP BY r.language
ORDER BY COUNT(r) DESC
""")
    List<Object[]> getLanguageBreakdown(User owner);
    long countByOwner(User owner);
}