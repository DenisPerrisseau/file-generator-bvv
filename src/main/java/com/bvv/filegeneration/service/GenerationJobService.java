package com.bvv.filegeneration.service;

import com.bvv.filegeneration.dto.GenerationJobDTO;
import com.bvv.filegeneration.entity.GenerationJob;
import com.bvv.filegeneration.entity.Template;
import com.bvv.filegeneration.common.enums.JobStatus;
import com.bvv.filegeneration.common.enums.OutputFormat;
import com.bvv.filegeneration.common.exceptions.JobNotFoundException;
import com.bvv.filegeneration.common.exceptions.TemplateNotFoundException;
import com.bvv.filegeneration.mapper.GenerationJobMapper;
import com.bvv.filegeneration.repository.GenerationJobRepository;
import com.bvv.filegeneration.repository.TemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GenerationJobService {

    private final GenerationJobRepository jobRepository;
    private final TemplateRepository templateRepository;
    private final GenerationJobMapper jobMapper;
    private final FileGenerationService fileGenerationService;

    public GenerationJobService(GenerationJobRepository jobRepository,
                                TemplateRepository templateRepository,
                                GenerationJobMapper jobMapper,
                                FileGenerationService fileGenerationService) {
        this.jobRepository = jobRepository;
        this.templateRepository = templateRepository;
        this.jobMapper = jobMapper;
        this.fileGenerationService = fileGenerationService;
    }

    /**
     * Crée une nouvelle tâche de génération
     */
    public GenerationJobDTO createJob(Long templateId, Integer totalLines,
                                      Integer errorLines, OutputFormat outputFormat) {
        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found with id: " + templateId));

        if (errorLines > totalLines) {
            throw new IllegalArgumentException("Error lines cannot exceed total lines");
        }

        GenerationJob job = GenerationJob.builder()
                .template(template)
                .totalLines(totalLines)
                .errorLines(errorLines)
                .outputFormat(outputFormat)
                .build();

        GenerationJob saved = jobRepository.save(job);

        // Lancer la génération automatiquement
        try {
            fileGenerationService.generateFileForJob(saved.getId());
        } catch (Exception e) {
            // Log l'erreur mais ne pas bloquer la création du job
            System.err.println("Erreur lors de la génération automatique: " + e.getMessage());
        }

        // Recharger le job mis à jour
        saved = jobRepository.findById(saved.getId()).orElse(saved);

        return jobMapper.toDTO(saved);
    }

    /**
     * Récupère une tâche par ID
     */
    public GenerationJobDTO getJobById(Long id) {
        GenerationJob job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));
        return jobMapper.toDTO(job);
    }

    /**
     * Récupère toutes les tâches
     */
    public List<GenerationJobDTO> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(jobMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les tâches d'un gabarit
     */
    public List<GenerationJobDTO> getJobsByTemplate(Long templateId) {
        return jobRepository.findByTemplateId(templateId).stream()
                .map(jobMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les tâches par statut
     */
    public List<GenerationJobDTO> getJobsByStatus(JobStatus status) {
        return jobRepository.findByStatus(status).stream()
                .map(jobMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Met à jour le statut d'une tâche
     */
    public GenerationJobDTO updateJobStatus(Long id, JobStatus status) {
        GenerationJob job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));

        job.setStatus(status);
        if (status == JobStatus.SUCCESS || status == JobStatus.FAILED) {
            job.setCompletedAt(LocalDateTime.now());
        }

        GenerationJob updated = jobRepository.save(job);
        return jobMapper.toDTO(updated);
    }

    /**
     * Met à jour le chemin du fichier générés
     */
    public GenerationJobDTO updateJobFilePath(Long id, String filePath) {
        GenerationJob job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));

        job.setFilePath(filePath);
        GenerationJob updated = jobRepository.save(job);
        return jobMapper.toDTO(updated);
    }
}

