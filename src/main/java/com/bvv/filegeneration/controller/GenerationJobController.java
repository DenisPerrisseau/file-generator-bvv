package com.bvv.filegeneration.controller;

import com.bvv.filegeneration.dto.GenerateFileRequestDTO;
import com.bvv.filegeneration.dto.GenerationJobDTO;
import com.bvv.filegeneration.service.GenerationJobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class GenerationJobController {

    private final GenerationJobService jobService;

    public GenerationJobController(GenerationJobService jobService) {
        this.jobService = jobService;
    }

    /**
     * POST /api/jobs/generate - Créer une nouvelle tâche de génération
     */
    @PostMapping("/generate")
    public ResponseEntity<GenerationJobDTO> generateFile(@Valid @RequestBody GenerateFileRequestDTO request) {
        GenerationJobDTO job = jobService.createJob(
                request.getTemplateId(),
                request.getTotalLines(),
                request.getErrorLines(),
                request.getOutputFormat()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(job);
    }

    /**
     * GET /api/jobs/{id} - Récupérer le statut d'une tâche
     */
    @GetMapping("/{id}")
    public ResponseEntity<GenerationJobDTO> getJobById(@PathVariable Long id) {
        GenerationJobDTO job = jobService.getJobById(id);
        return ResponseEntity.ok(job);
    }

    /**
     * GET /api/jobs - Récupérer toutes les tâches
     */
    @GetMapping
    public ResponseEntity<List<GenerationJobDTO>> getAllJobs() {
        List<GenerationJobDTO> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    /**
     * GET /api/jobs/template/{templateId} - Récupérer les tâches d'un gabarit
     */
    @GetMapping("/template/{templateId}")
    public ResponseEntity<List<GenerationJobDTO>> getJobsByTemplate(@PathVariable Long templateId) {
        List<GenerationJobDTO> jobs = jobService.getJobsByTemplate(templateId);
        return ResponseEntity.ok(jobs);
    }

    /**
     * GET /api/jobs/{id}/preview - Aperçu des premières lignes
     */
    @GetMapping("/{id}/preview")
    public ResponseEntity<String> previewJob(@PathVariable Long id) {
        // À implémenter
        return ResponseEntity.ok("Job preview endpoint placeholder");
    }

    /**
     * POST /api/jobs/{id}/send - Envoyer le fichier via HTTP POST avec HMAC-SHA256
     */
    @PostMapping("/{id}/send")
    public ResponseEntity<String> sendJob(@PathVariable Long id) {
        // À implémenter
        return ResponseEntity.ok("Job send endpoint placeholder");
    }

    /**
     * POST /api/jobs/{id}/process - Traiter manuellement une tâche PENDING
     */
    @PostMapping("/{id}/process")
    public ResponseEntity<GenerationJobDTO> processJob(@PathVariable Long id) {
        // Récupérer et traiter le job
        GenerationJobDTO job = jobService.getJobById(id);
        return ResponseEntity.ok(job);
    }
}

