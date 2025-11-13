package com.bvv.filegeneration.controller;

import com.bvv.filegeneration.service.JsonFileGeneratorService;
import com.bvv.filegeneration.service.XmlFileGeneratorService;
import com.bvv.filegeneration.service.MultiKeysJsonGeneratorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur principal - Génération de fichiers (3 formats)
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class GeneratorController {

    private final JsonFileGeneratorService jsonGeneratorService;
    private final XmlFileGeneratorService xmlGeneratorService;
    private final MultiKeysJsonGeneratorService multiKeysJsonGeneratorService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Page d'accueil
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * API - Générer et prévisualiser le fichier
     */
    @PostMapping("/api/generate")
    @ResponseBody
    public ResponseEntity<?> generateFile(@RequestBody Map<String, Object> request) {
        try {
            int totalLines = ((Number) request.get("totalLines")).intValue();
            int errorLines = ((Number) request.get("errorLines")).intValue();
            int duplicateLines = ((Number) request.get("duplicateLines")).intValue();
            java.util.List<String> errorTypes = (java.util.List<String>) request.get("errorTypes");
            String outputType = (String) request.getOrDefault("outputType", "ACQ_MTBORNE");

            // Validations cohérentes
            if (totalLines < 1) {
                return ResponseEntity.badRequest().body("{\"error\": \"Le nombre total de lignes doit être au moins 1\"}");
            }
            if (errorLines < 0) {
                return ResponseEntity.badRequest().body("{\"error\": \"Le nombre d'erreurs ne peut pas être négatif\"}");
            }
            if (errorLines > totalLines) {
                return ResponseEntity.badRequest().body("{\"error\": \"Le nombre d'erreurs ne peut pas dépasser le nombre total de lignes (" + totalLines + ")\"}");
            }
            if (duplicateLines < 0) {
                return ResponseEntity.badRequest().body("{\"error\": \"Le nombre de doublons ne peut pas être négatif\"}");
            }

            int validLines = totalLines - errorLines;
            if (duplicateLines > 0 && validLines == 0) {
                return ResponseEntity.badRequest().body("{\"error\": \"Impossible d'ajouter des doublons sans lignes valides. Réduisez le nombre d'erreurs.\"}");
            }

            // Vérifier que le nombre d'erreurs sélectionnées ne dépasse pas le nombre de lignes en erreur
            if (errorTypes != null && !errorTypes.isEmpty() && errorLines > 0) {
                if (errorTypes.size() > errorLines) {
                    return ResponseEntity.badRequest().body("{\"error\": \"Nombre de types d'erreurs sélectionnés (" + errorTypes.size() + ") dépasse le nombre de lignes en erreur (" + errorLines + ")\"}");
                }
            }

            log.info("Génération: {} lignes, {} erreurs, {} doublons, {} types d'erreurs, type: {}",
                    totalLines, errorLines, duplicateLines,
                    errorTypes != null ? errorTypes.size() : 0, outputType);

            String content;
            MediaType mediaType;

            switch (outputType) {
                case "ACQ_MTCAB":
                    content = xmlGeneratorService.generateXml(totalLines, errorLines, errorTypes, duplicateLines);
                    mediaType = MediaType.APPLICATION_XML;
                    break;
                case "ACQ_PE":
                    content = multiKeysJsonGeneratorService.generateMultiKeysJson(totalLines, errorLines, errorTypes, duplicateLines);
                    mediaType = MediaType.APPLICATION_JSON;
                    break;
                case "ACQ_MTBORNE":
                default:
                    content = jsonGeneratorService.generateJson(totalLines, errorLines, errorTypes, duplicateLines);
                    mediaType = MediaType.APPLICATION_JSON;
                    break;
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(content);

        } catch (IllegalArgumentException e) {
            log.warn("Validation échouée: {}", e.getMessage());
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            log.error("Erreur génération", e);
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * API - Télécharger le fichier
     */
    @PostMapping("/api/download")
    public ResponseEntity<String> downloadFile(@RequestBody Map<String, Object> request) {
        try {
            int totalLines = ((Number) request.get("totalLines")).intValue();
            int errorLines = ((Number) request.get("errorLines")).intValue();
            int duplicateLines = ((Number) request.get("duplicateLines")).intValue();
            java.util.List<String> errorTypes = (java.util.List<String>) request.get("errorTypes");
            String outputType = (String) request.getOrDefault("outputType", "ACQ_MTBORNE");

            // Validations cohérentes
            if (totalLines < 1) {
                return ResponseEntity.badRequest().body("{\"error\": \"Le nombre total de lignes doit être au moins 1\"}");
            }
            if (errorLines < 0) {
                return ResponseEntity.badRequest().body("{\"error\": \"Le nombre d'erreurs ne peut pas être négatif\"}");
            }
            if (errorLines > totalLines) {
                return ResponseEntity.badRequest().body("{\"error\": \"Le nombre d'erreurs ne peut pas dépasser le nombre total de lignes (" + totalLines + ")\"}");
            }
            if (duplicateLines < 0) {
                return ResponseEntity.badRequest().body("{\"error\": \"Le nombre de doublons ne peut pas être négatif\"}");
            }

            int validLines = totalLines - errorLines;
            if (duplicateLines > 0 && validLines == 0) {
                return ResponseEntity.badRequest().body("{\"error\": \"Impossible d'ajouter des doublons sans lignes valides. Réduisez le nombre d'erreurs.\"}");
            }

            // Vérifier que le nombre d'erreurs sélectionnées ne dépasse pas le nombre de lignes en erreur
            if (errorTypes != null && !errorTypes.isEmpty() && errorLines > 0) {
                if (errorTypes.size() > errorLines) {
                    return ResponseEntity.badRequest().body("{\"error\": \"Nombre de types d'erreurs sélectionnés (" + errorTypes.size() + ") dépasse le nombre de lignes en erreur (" + errorLines + ")\"}");
                }
            }

            String content;
            String filename;
            MediaType mediaType;

            switch (outputType) {
                case "ACQ_MTCAB":
                    content = xmlGeneratorService.generateXml(totalLines, errorLines, errorTypes, duplicateLines);
                    filename = "acquittements_mtcab.xml";
                    mediaType = MediaType.APPLICATION_XML;
                    break;
                case "ACQ_PE":
                    content = multiKeysJsonGeneratorService.generateMultiKeysJson(totalLines, errorLines, errorTypes, duplicateLines);
                    filename = "acquittements_pe.json";
                    mediaType = MediaType.APPLICATION_JSON;
                    break;
                case "ACQ_MTBORNE":
                default:
                    content = jsonGeneratorService.generateJson(totalLines, errorLines, errorTypes, duplicateLines);
                    filename = "acquittements_mtborne.json";
                    mediaType = MediaType.APPLICATION_JSON;
                    break;
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename)
                    .body(content);

        } catch (IllegalArgumentException e) {
            log.warn("Validation échouée: {}", e.getMessage());
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            log.error("Erreur téléchargement", e);
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}

