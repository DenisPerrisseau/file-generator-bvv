package com.bvv.filegeneration.repository;

import com.bvv.filegeneration.entity.GenerationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenerationLogRepository extends JpaRepository<GenerationLog, Long> {
    List<GenerationLog> findByJobId(Long jobId);
}

