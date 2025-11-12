package com.bvv.filegeneration.controller;

import com.bvv.filegeneration.dto.*;
import com.bvv.filegeneration.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class AutoTestController {

    private final FileGenerationService fileGenerationService;
    private final FileHttpSenderService fileSenderService;
    private final TargetUrlService targetUrlService;
    private final TemplateService templateService;
    private final GenerationJobService jobService;

    /**
     * GET /api/test/urls - Récupère la liste des URLs actives
     */
    @GetMapping("/urls")
    public ResponseEntity<List<TargetUrlDTO>> getActiveUrls() {
        List<TargetUrlDTO> urls = targetUrlService.getActiveUrls();
        return ResponseEntity.ok(urls);
    }

    /**
     * POST /api/test/generate-and-send - Génère un fichier et l'envoie immédiatement
     */
    @PostMapping("/generate-and-send")
    public ResponseEntity<TestResultDTO> generateAndSend(@RequestBody GenerateFileRequestDTO request) {
        try {
            log.info("Génération et envoi de fichier - Template: {}, Lignes: {}, Erreurs: {}, Doublons: {}",
                    request.getTemplateId(), request.getTotalLines(), request.getErrorLines(),
                    request.getDuplicateLines());

            // Récupérer le template
            TemplateDTO template = templateService.getTemplateById(request.getTemplateId());

            // Générer le contenu en mémoire
            String fileContent = fileGenerationService.generateContentWithOptions(
                    templateService.getTemplateEntityById(request.getTemplateId()),
                    request.getTotalLines(),
                    request.getErrorLines(),
                    request.getDuplicateLines() != null ? request.getDuplicateLines() : 0,
                    request.getSelectedErrorTypes(),
                    request.getOutputFormat()
            );

            // Si une URL cible est fournie, envoyer le fichier
            TestResultDTO result = TestResultDTO.builder()
                    .fileContent(fileContent)
                    .totalLines(request.getTotalLines())
                    .errorLines(request.getErrorLines())
                    .duplicateLines(request.getDuplicateLines())
                    .build();

            if (request.getTargetUrlId() != null) {
                TargetUrlDTO targetUrl = targetUrlService.getUrlById(request.getTargetUrlId());

                // Créer un job temporaire pour la comparaison
                GenerationJobDTO job = jobService.createJob(request);

                // Envoyer le fichier
                ServerResponseDTO serverResponse = fileSenderService.sendFile(
                        fileContent,
                        targetUrlService.getTargetUrlEntity(request.getTargetUrlId()),
                        jobService.getJobEntity(job.getId())
                );

                // Comparer les résultats
                List<ComparisonResultDTO> comparisons = fileSenderService.compareResults(
                        jobService.getJobEntity(job.getId()),
                        serverResponse,
                        200 // TODO: récupérer le vrai code HTTP
                );

                result.setServerResponse(serverResponse);
                result.setComparisons(comparisons);
                result.setSent(true);
                result.setTargetUrl(targetUrl.getUrl());
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Erreur lors de la génération et envoi", e);
            return ResponseEntity.internalServerError()
                    .body(TestResultDTO.builder()
                            .error(e.getMessage())
                            .build());
        }
    }

    /**
     * GET /api/test/error-types - Récupère la liste des types d'erreurs disponibles
     */
    @GetMapping("/error-types")
    public ResponseEntity<List<ErrorTypeDTO>> getErrorTypes() {
        List<ErrorTypeDTO> errorTypes = List.of(
                new ErrorTypeDTO("NULL_VALUE", "Valeur NULL", "Champ obligatoire laissé vide"),
                new ErrorTypeDTO("EMPTY_STRING", "Chaîne Vide", "Champ texte vide"),
                new ErrorTypeDTO("INVALID_DATE", "Date Invalide", "Format de date incorrect"),
                new ErrorTypeDTO("OUT_OF_BOUNDS", "Hors Limites", "Valeur numérique hors min/max"),
                new ErrorTypeDTO("WRONG_TYPE", "Type Incorrect", "Type de donnée incorrect"),
                new ErrorTypeDTO("TOO_LONG", "Trop Long", "Longueur maximale dépassée"),
                new ErrorTypeDTO("TOO_SHORT", "Trop Court", "Longueur minimale non atteinte"),
                new ErrorTypeDTO("INVALID_FORMAT", "Format Invalide", "Format de données incorrect"),
                new ErrorTypeDTO("DUPLICATE", "Doublon", "Ligne dupliquée"),
                new ErrorTypeDTO("MISSING_REQUIRED", "Champ Manquant", "Champ obligatoire absent")
        );
        return ResponseEntity.ok(errorTypes);
    }
}

/**
 * DTO pour les types d'erreurs
 */
class ErrorTypeDTO {
    private String code;
    private String label;
    private String description;

    public ErrorTypeDTO(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

