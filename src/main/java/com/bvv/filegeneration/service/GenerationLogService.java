package com.bvv.filegeneration.service;

import com.bvv.filegeneration.dto.GenerationLogDTO;
import com.bvv.filegeneration.entity.GenerationJob;
import com.bvv.filegeneration.entity.GenerationLog;
import com.bvv.filegeneration.common.enums.ActionType;
import com.bvv.filegeneration.common.exceptions.JobNotFoundException;
import com.bvv.filegeneration.mapper.GenerationLogMapper;
import com.bvv.filegeneration.repository.GenerationJobRepository;
import com.bvv.filegeneration.repository.GenerationLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GenerationLogService {

    private final GenerationLogRepository logRepository;
    private final GenerationJobRepository jobRepository;
    private final GenerationLogMapper logMapper;

    public GenerationLogService(GenerationLogRepository logRepository,
                                GenerationJobRepository jobRepository,
                                GenerationLogMapper logMapper) {
        this.logRepository = logRepository;
        this.jobRepository = jobRepository;
        this.logMapper = logMapper;
    }

    /**
     * Crée un log pour une tâche
     */
    public GenerationLogDTO createLog(Long jobId, ActionType action,
                                     Integer httpStatus, String responseMessage) {
        GenerationJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jobId));

        GenerationLog log = GenerationLog.builder()
                .job(job)
                .action(action)
                .httpStatus(httpStatus)
                .responseMessage(responseMessage)
                .sentAt(LocalDateTime.now())
                .retryCount(0)
                .build();

        GenerationLog saved = logRepository.save(log);
        return logMapper.toDTO(saved);
    }

    /**
     * Récupère tous les logs d'une tâche
     */
    public List<GenerationLogDTO> getLogsByJob(Long jobId) {
        return logRepository.findByJobId(jobId).stream()
                .map(logMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les logs
     */
    public List<GenerationLogDTO> getAllLogs() {
        return logRepository.findAll().stream()
                .map(logMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crée un log d'envoi réussi
     */
    public GenerationLogDTO logSuccess(Long jobId, Integer httpStatus, String responseMessage) {
        return createLog(jobId, ActionType.SENT, httpStatus, responseMessage);
    }

    /**
     * Crée un log d'erreur
     */
    public GenerationLogDTO logFailure(Long jobId, String errorMessage) {
        return createLog(jobId, ActionType.FAILED, null, errorMessage);
    }
}

