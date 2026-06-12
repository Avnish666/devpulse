package com.devpulse.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "commit_activity")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommitActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String repoFullName;
    private String sha;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String authorName;
    private LocalDateTime committedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
