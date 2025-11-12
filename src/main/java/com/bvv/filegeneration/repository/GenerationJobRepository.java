package com.bvv.filegeneration.repository;

import com.bvv.filegeneration.entity.GenerationJob;
import com.bvv.filegeneration.common.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenerationJobRepository extends JpaRepository<GenerationJob, Long> {
    List<GenerationJob> findByStatus(JobStatus status);
    List<GenerationJob> findByTemplateId(Long templateId);
}

