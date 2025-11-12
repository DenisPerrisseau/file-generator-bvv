package com.bvv.filegeneration.entity;

import com.bvv.filegeneration.common.enums.JobStatus;
import com.bvv.filegeneration.common.enums.OutputFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "generation_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerationJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @Column(nullable = false)
    private Integer totalLines;

    @Column(nullable = false)
    private Integer errorLines;

    @Column(name = "duplicate_lines")
    private Integer duplicateLines;

    @Column(name = "selected_error_types", length = 500)
    private String selectedErrorTypes; // Stocké comme String séparé par virgules (ex: "NULL_VALUE,INVALID_DATE")

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutputFormat outputFormat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    @Column(name = "file_path")
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_url_id")
    private TargetUrl targetUrl;

    @Column(name = "expected_lines_treated")
    private Integer expectedLinesTreated;

    @Column(name = "expected_lines_insert")
    private Integer expectedLinesInsert;

    @Column(name = "expected_lines_update")
    private Integer expectedLinesUpdate;

    @Column(name = "expected_lines_ignored")
    private Integer expectedLinesIgnored;

    @Column(name = "expected_http_status")
    private Integer expectedHttpStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GenerationLog> logs = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = JobStatus.PENDING;
    }
}

