package com.devpulse.Repository;

import com.devpulse.Entity.CommitActivity;
import com.devpulse.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommitActivityRepository extends JpaRepository<CommitActivity, Long> {
    List<CommitActivity> findByUserOrderByCommittedAtDesc(User user);
    List<CommitActivity> findByRepoFullName(String repoFullName);
    boolean existsBySha(String sha);
    @Query("""
SELECT DATE(c.committedAt), COUNT(c)
FROM CommitActivity c
WHERE c.user = :user
GROUP BY DATE(c.committedAt)
ORDER BY DATE(c.committedAt)
""")
    List<Object[]> getDailyCommitCounts(User user);
    @Query("""
SELECT c.repoFullName, COUNT(c)
FROM CommitActivity c
WHERE c.user = :user
GROUP BY c.repoFullName
ORDER BY COUNT(c) DESC
""")
    List<Object[]> getMostActiveRepositories(User user);
    List<CommitActivity> findByUserOrderByCommittedAtAsc(User user);
    long countByUser(User user);
    @Query("""
SELECT c.repoFullName, COUNT(c)
FROM CommitActivity c
WHERE c.user = :user
GROUP BY c.repoFullName
ORDER BY COUNT(c) DESC
""")
    List<Object[]> getTopRepository(User user);
}