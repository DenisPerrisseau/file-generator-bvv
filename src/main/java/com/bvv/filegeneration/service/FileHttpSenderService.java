package com.bvv.filegeneration.service;

import com.bvv.filegeneration.dto.ComparisonResultDTO;
import com.bvv.filegeneration.dto.ServerResponseDTO;
import com.bvv.filegeneration.entity.GenerationJob;
import com.bvv.filegeneration.entity.GenerationLog;
import com.bvv.filegeneration.entity.TargetUrl;
import com.bvv.filegeneration.common.enums.ActionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileHttpSenderService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Envoie le contenu du fichier généré à l'URL cible avec Bearer token
     */
    public ServerResponseDTO sendFile(String fileContent, TargetUrl targetUrl, GenerationJob job) {
        log.info("Envoi du fichier vers: {}", targetUrl.getUrl());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Ajouter le Bearer token si présent
        if (targetUrl.getBearerToken() != null && !targetUrl.getBearerToken().isEmpty()) {
            headers.set("Authorization", "Bearer " + targetUrl.getBearerToken());
        }

        HttpEntity<String> request = new HttpEntity<>(fileContent, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    targetUrl.getUrl(),
                    HttpMethod.POST,
                    request,
                    String.class
            );

            log.info("Réponse du serveur - Status: {}, Body: {}", response.getStatusCode(), response.getBody());

            // Parser la réponse JSON du serveur
            ServerResponseDTO serverResponse = parseServerResponse(response.getBody());
            serverResponse.setStatut(response.getBody()); // Garder la réponse complète

            return serverResponse;

        } catch (HttpClientErrorException e) {
            log.error("Erreur HTTP lors de l'envoi: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());

            ServerResponseDTO errorResponse = ServerResponseDTO.builder()
                    .statut("ERROR")
                    .messageAvertissement(e.getMessage())
                    .build();

            return errorResponse;
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du fichier", e);

            ServerResponseDTO errorResponse = ServerResponseDTO.builder()
                    .statut("ERROR")
                    .messageAvertissement(e.getMessage())
                    .build();

            return errorResponse;
        }
    }

    /**
     * Parse la réponse JSON du serveur
     */
    private ServerResponseDTO parseServerResponse(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, ServerResponseDTO.class);
        } catch (Exception e) {
            log.error("Erreur lors du parsing de la réponse serveur", e);
            return ServerResponseDTO.builder()
                    .statut("UNKNOWN")
                    .messageAvertissement("Impossible de parser la réponse: " + jsonResponse)
                    .build();
        }
    }

    /**
     * Compare les résultats attendus avec les résultats réels
     */
    public List<ComparisonResultDTO> compareResults(GenerationJob job, ServerResponseDTO serverResponse, int httpStatus) {
        List<ComparisonResultDTO> comparisons = new ArrayList<>();

        // Comparaison du code HTTP
        if (job.getExpectedHttpStatus() != null) {
            comparisons.add(ComparisonResultDTO.builder()
                    .field("Code HTTP")
                    .expected(job.getExpectedHttpStatus())
                    .actual(httpStatus)
                    .matches(job.getExpectedHttpStatus().equals(httpStatus))
                    .message(getHttpStatusMessage(job.getExpectedHttpStatus(), httpStatus))
                    .build());
        }

        // Comparaison des lignes traitées
        if (job.getExpectedLinesTreated() != null && serverResponse.getLignesTraitees() != null) {
            comparisons.add(ComparisonResultDTO.builder()
                    .field("Lignes Traitées")
                    .expected(job.getExpectedLinesTreated())
                    .actual(serverResponse.getLignesTraitees())
                    .matches(job.getExpectedLinesTreated().equals(serverResponse.getLignesTraitees()))
                    .message("Nombre de lignes traitées par le serveur")
                    .build());
        }

        // Comparaison des lignes insérées
        if (job.getExpectedLinesInsert() != null && serverResponse.getLignesInsert() != null) {
            comparisons.add(ComparisonResultDTO.builder()
                    .field("Lignes Insérées")
                    .expected(job.getExpectedLinesInsert())
                    .actual(serverResponse.getLignesInsert())
                    .matches(job.getExpectedLinesInsert().equals(serverResponse.getLignesInsert()))
                    .message("Nombre de lignes insérées en base")
                    .build());
        }

        // Comparaison des lignes mises à jour
        if (job.getExpectedLinesUpdate() != null && serverResponse.getLignesUpdate() != null) {
            comparisons.add(ComparisonResultDTO.builder()
                    .field("Lignes Mises à Jour")
                    .expected(job.getExpectedLinesUpdate())
                    .actual(serverResponse.getLignesUpdate())
                    .matches(job.getExpectedLinesUpdate().equals(serverResponse.getLignesUpdate()))
                    .message("Nombre de lignes mises à jour")
                    .build());
        }

        // Comparaison des lignes ignorées
        if (job.getExpectedLinesIgnored() != null && serverResponse.getLignesIgnorees() != null) {
            comparisons.add(ComparisonResultDTO.builder()
                    .field("Lignes Ignorées")
                    .expected(job.getExpectedLinesIgnored())
                    .actual(serverResponse.getLignesIgnorees())
                    .matches(job.getExpectedLinesIgnored().equals(serverResponse.getLignesIgnorees()))
                    .message("Nombre de lignes ignorées (erreurs)")
                    .build());
        }

        return comparisons;
    }

    private String getHttpStatusMessage(int expected, int actual) {
        if (expected == actual) {
            return "✓ Code HTTP correct";
        }

        String expectedMsg = getHttpStatusDescription(expected);
        String actualMsg = getHttpStatusDescription(actual);

        return String.format("✗ Attendu: %d (%s), Reçu: %d (%s)",
                expected, expectedMsg, actual, actualMsg);
    }

    private String getHttpStatusDescription(int status) {
        return switch (status) {
            case 200 -> "OK - Toutes les lignes traitées avec succès";
            case 206 -> "Partial Content - Traitement partiel avec erreurs";
            case 400 -> "Bad Request - Toutes les lignes en erreur";
            case 401 -> "Unauthorized - Token invalide";
            case 500 -> "Internal Server Error";
            default -> "Statut inconnu";
        };
    }
}

