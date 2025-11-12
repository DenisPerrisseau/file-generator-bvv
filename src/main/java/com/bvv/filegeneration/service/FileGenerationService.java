package com.bvv.filegeneration.service;

import com.bvv.filegeneration.common.enums.FieldType;
import com.bvv.filegeneration.common.enums.JobStatus;
import com.bvv.filegeneration.common.enums.OutputFormat;
import com.bvv.filegeneration.common.exceptions.FileGenerationException;
import com.bvv.filegeneration.entity.GenerationJob;
import com.bvv.filegeneration.entity.Template;
import com.bvv.filegeneration.entity.TemplateField;
import com.bvv.filegeneration.repository.GenerationJobRepository;
import com.bvv.filegeneration.repository.TemplateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@Transactional
public class FileGenerationService {

    private final GenerationJobRepository jobRepository;
    private final TemplateRepository templateRepository;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    public FileGenerationService(GenerationJobRepository jobRepository,
                                TemplateRepository templateRepository) {
        this.jobRepository = jobRepository;
        this.templateRepository = templateRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Génère un fichier à partir d'une tâche PENDING
     */
    public void generateFileForJob(Long jobId) {
        GenerationJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new FileGenerationException("Job non trouvé: " + jobId));

        if (job.getStatus() != JobStatus.PENDING) {
            log.warn("Job {} n'est pas en statut PENDING, statut actuel: {}", jobId, job.getStatus());
            return;
        }

        try {
            // Mettre le statut en PROCESSING
            job.setStatus(JobStatus.PROCESSING);
            jobRepository.save(job);

            log.info("Début génération fichier pour job {}", jobId);

            // Récupérer le template
            Template template = job.getTemplate();
            if (template == null) {
                throw new FileGenerationException("Template non associé au job " + jobId);
            }

            // Générer le contenu selon le format
            String fileContent = generateContent(
                    template,
                    job.getTotalLines(),
                    job.getErrorLines(),
                    job.getOutputFormat()
            );

            // Sauvegarder le fichier
            String filePath = saveFile(job, template, fileContent);

            // Mettre à jour le job
            job.setStatus(JobStatus.SUCCESS);
            job.setFilePath(filePath);
            job.setCompletedAt(LocalDateTime.now());
            jobRepository.save(job);

            log.info("Fichier généré avec succès pour job {}: {}", jobId, filePath);

        } catch (Exception e) {
            log.error("Erreur lors de la génération du fichier pour job {}", jobId, e);
            job.setStatus(JobStatus.FAILED);
            job.setCompletedAt(LocalDateTime.now());
            jobRepository.save(job);
            throw new FileGenerationException("Erreur génération: " + e.getMessage(), e);
        }
    }

    /**
     * Génère le contenu du fichier selon le format
     */
    private String generateContent(Template template, int totalLines, int errorLines, OutputFormat format) {
        List<Map<String, Object>> records = generateRecords(template, totalLines, errorLines);

        switch (format) {
            case JSON:
                return generateJson(records);
            case XML:
                return generateXml(records, template);
            case TXT:
                return generateTxt(records, template);
            default:
                throw new FileGenerationException("Format non supporté: " + format);
        }
    }

    /**
     * Génère les enregistrements de données
     */
    private List<Map<String, Object>> generateRecords(Template template, int totalLines, int errorLines) {
        List<Map<String, Object>> records = new ArrayList<>();
        List<TemplateField> fields = new ArrayList<>(template.getFields());
        fields.sort(Comparator.comparing(TemplateField::getPosition));

        // Déterminer quelles lignes auront des erreurs (distribution aléatoire)
        Set<Integer> errorLineIndexes = new HashSet<>();
        while (errorLineIndexes.size() < errorLines) {
            errorLineIndexes.add(random.nextInt(totalLines));
        }

        for (int i = 0; i < totalLines; i++) {
            boolean shouldHaveError = errorLineIndexes.contains(i);
            Map<String, Object> record = shouldHaveError
                    ? generateErrorRecord(fields)
                    : generateValidRecord(fields);
            records.add(record);
        }

        return records;
    }

    /**
     * Génère un enregistrement valide
     */
    private Map<String, Object> generateValidRecord(List<TemplateField> fields) {
        Map<String, Object> record = new LinkedHashMap<>();

        for (TemplateField field : fields) {
            Object value = generateValidValue(field);
            record.put(field.getFieldName(), value);
        }

        return record;
    }

    /**
     * Génère un enregistrement avec erreur
     */
    private Map<String, Object> generateErrorRecord(List<TemplateField> fields) {
        Map<String, Object> record = new LinkedHashMap<>();

        // Choisir aléatoirement 1 ou 2 champs qui auront des erreurs
        int errorCount = random.nextInt(2) + 1;
        Set<Integer> errorFieldIndexes = new HashSet<>();
        while (errorFieldIndexes.size() < errorCount && errorFieldIndexes.size() < fields.size()) {
            errorFieldIndexes.add(random.nextInt(fields.size()));
        }

        for (int i = 0; i < fields.size(); i++) {
            TemplateField field = fields.get(i);
            Object value = errorFieldIndexes.contains(i)
                    ? generateErrorValue(field)
                    : generateValidValue(field);
            record.put(field.getFieldName(), value);
        }

        return record;
    }

    /**
     * Génère une valeur valide pour un champ
     */
    private Object generateValidValue(TemplateField field) {
        switch (field.getFieldType()) {
            case STRING:
                return generateValidString(field);
            case INTEGER:
                return generateValidInteger(field);
            case DECIMAL:
                return generateValidDecimal(field);
            case BOOLEAN:
                return random.nextBoolean();
            case DATE:
                return generateValidDate(field);
            default:
                return "value_" + random.nextInt(1000);
        }
    }

    /**
     * Génère une valeur invalide pour un champ
     */
    private Object generateErrorValue(TemplateField field) {
        int errorType = random.nextInt(4);

        switch (errorType) {
            case 0: // Valeur null pour champ obligatoire
                return field.getRequired() ? null : generateValidValue(field);
            case 1: // Valeur vide pour string
                return field.getFieldType() == FieldType.STRING ? "" : generateValidValue(field);
            case 2: // Valeur hors bornes
                return generateOutOfBoundsValue(field);
            case 3: // Format invalide
                return generateInvalidFormat(field);
            default:
                return null;
        }
    }

    private String generateValidString(TemplateField field) {
        int length = field.getMinLength() != null
                ? field.getMinLength() + random.nextInt(10)
                : 5 + random.nextInt(10);

        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private Integer generateValidInteger(TemplateField field) {
        int min = field.getMinValue() != null ? field.getMinValue().intValue() : 0;
        int max = field.getMaxValue() != null ? field.getMaxValue().intValue() : 10000;
        return min + random.nextInt(max - min + 1);
    }

    private Double generateValidDecimal(TemplateField field) {
        double min = field.getMinValue() != null ? field.getMinValue().doubleValue() : 0.0;
        double max = field.getMaxValue() != null ? field.getMaxValue().doubleValue() : 10000.0;
        return min + (max - min) * random.nextDouble();
    }

    private String generateValidDate(TemplateField field) {
        String format = field.getDateFormat() != null ? field.getDateFormat() : "yyyy-MM-dd";
        LocalDateTime date = LocalDateTime.now().minusDays(random.nextInt(365));
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    private Object generateOutOfBoundsValue(TemplateField field) {
        if (field.getFieldType() == FieldType.INTEGER && field.getMaxValue() != null) {
            return field.getMaxValue().intValue() + 1000;
        }
        if (field.getFieldType() == FieldType.STRING && field.getMaxLength() != null) {
            return "X".repeat(field.getMaxLength() + 10);
        }
        return generateValidValue(field);
    }

    private Object generateInvalidFormat(TemplateField field) {
        if (field.getFieldType() == FieldType.DATE) {
            return "2025-13-45"; // Date invalide
        }
        if (field.getFieldType() == FieldType.INTEGER) {
            return "ABC"; // String au lieu d'integer
        }
        return generateValidValue(field);
    }

    /**
     * Génère le JSON
     */
    private String generateJson(List<Map<String, Object>> records) {
        try {
            Map<String, Object> root = new LinkedHashMap<>();
            root.put("records", records);
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new FileGenerationException("Erreur génération JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Génère le XML
     */
    private String generateXml(List<Map<String, Object>> records, Template template) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<records>\n");

        for (Map<String, Object> record : records) {
            xml.append("  <record>\n");
            for (Map.Entry<String, Object> entry : record.entrySet()) {
                xml.append("    <").append(entry.getKey()).append(">");
                xml.append(entry.getValue() != null ? entry.getValue() : "");
                xml.append("</").append(entry.getKey()).append(">\n");
            }
            xml.append("  </record>\n");
        }

        xml.append("</records>");
        return xml.toString();
    }

    /**
     * Génère le TXT
     */
    private String generateTxt(List<Map<String, Object>> records, Template template) {
        StringBuilder txt = new StringBuilder();

        // Header
        List<TemplateField> fields = new ArrayList<>(template.getFields());
        fields.sort(Comparator.comparing(TemplateField::getPosition));

        List<String> headers = new ArrayList<>();
        for (TemplateField field : fields) {
            headers.add(field.getFieldName());
        }
        txt.append(String.join(",", headers)).append("\n");

        // Data rows
        for (Map<String, Object> record : records) {
            List<String> values = new ArrayList<>();
            for (TemplateField field : fields) {
                Object value = record.get(field.getFieldName());
                values.add(value != null ? value.toString() : "");
            }
            txt.append(String.join(",", values)).append("\n");
        }

        return txt.toString();
    }

    /**
     * Sauvegarde le fichier sur disque
     */
    private String saveFile(GenerationJob job, Template template, String content) throws IOException {
        // Créer le répertoire si nécessaire
        String baseDir = "generated_files";
        Path dirPath = Paths.get(baseDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // Générer le nom du fichier
        String extension = job.getOutputFormat().toString().toLowerCase();
        String fileName = String.format("%s_%s_job%d.%s",
                template.getPrefix(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")),
                job.getId(),
                extension);

        // Chemin complet
        Path filePath = dirPath.resolve(fileName);

        // Écrire le fichier
        Files.writeString(filePath, content);

        return filePath.toString();
    }
}

