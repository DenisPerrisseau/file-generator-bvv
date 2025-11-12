package com.bvv.filegeneration.controller;

import com.bvv.filegeneration.dto.GenerationLogDTO;
import com.bvv.filegeneration.service.GenerationLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class GenerationLogController {

    private final GenerationLogService logService;

    public GenerationLogController(GenerationLogService logService) {
        this.logService = logService;
    }

    /**
     * GET /api/logs - Récupérer tous les logs
     */
    @GetMapping
    public ResponseEntity<List<GenerationLogDTO>> getAllLogs() {
        List<GenerationLogDTO> logs = logService.getAllLogs();
        return ResponseEntity.ok(logs);
    }

    /**
     * GET /api/logs/job/{jobId} - Récupérer les logs d'une tâche
     */
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<GenerationLogDTO>> getLogsByJob(@PathVariable Long jobId) {
        List<GenerationLogDTO> logs = logService.getLogsByJob(jobId);
        return ResponseEntity.ok(logs);
    }
}

