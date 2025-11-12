package com.bvv.filegeneration.controller;

import com.bvv.filegeneration.dto.TemplateDTO;
import com.bvv.filegeneration.service.TemplateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@CrossOrigin(origins = "*")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    /**
     * POST /api/templates/import - Importer un fichier exemple et générer un gabarit
     */
    @PostMapping("/import")
    public ResponseEntity<String> importTemplate() {
        // À implémenter
        return ResponseEntity.ok("Template import endpoint placeholder");
    }

    /**
     * GET /api/templates - Récupérer tous les gabarits
     */
    @GetMapping
    public ResponseEntity<List<TemplateDTO>> getAllTemplates() {
        List<TemplateDTO> templates = templateService.getAllTemplates();
        return ResponseEntity.ok(templates);
    }

    /**
     * GET /api/templates/{id} - Récupérer un gabarit par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TemplateDTO> getTemplateById(@PathVariable Long id) {
        TemplateDTO template = templateService.getTemplateById(id);
        return ResponseEntity.ok(template);
    }

    /**
     * PUT /api/templates/{id} - Mettre à jour un gabarit
     */
    @PutMapping("/{id}")
    public ResponseEntity<TemplateDTO> updateTemplate(@PathVariable Long id,
                                                     @RequestBody TemplateDTO dto) {
        TemplateDTO updated = templateService.updateTemplate(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/templates/{id} - Supprimer un gabarit
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
}

