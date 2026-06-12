package com.devpulse.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "repositories")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GitHubRepo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String githubRepoId;
    private String name;
    private String fullName;

    @Column(length = 500)
    private String description;

    private String language;
    private Integer stars;
    private Integer forks;
    private Boolean isPrivate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @CreationTimestamp
    private LocalDateTime syncedAt;
}