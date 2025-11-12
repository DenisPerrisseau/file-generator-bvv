package com.bvv.filegeneration.entity;

import com.bvv.filegeneration.common.enums.ActionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "generation_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private GenerationJob job;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType action;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "response_message", columnDefinition = "TEXT")
    private String responseMessage;

    @Column(name = "actual_lines_treated")
    private Integer actualLinesTreated;

    @Column(name = "actual_lines_insert")
    private Integer actualLinesInsert;

    @Column(name = "actual_lines_update")
    private Integer actualLinesUpdate;

    @Column(name = "actual_lines_ignored")
    private Integer actualLinesIgnored;

    @Column(name = "response_status")
    private String responseStatus; // PARTIAL, SUCCESS, ERROR

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

