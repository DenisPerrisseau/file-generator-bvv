package com.bvv.filegeneration.service;

import com.bvv.filegeneration.dto.TemplateDTO;
import com.bvv.filegeneration.entity.Template;
import com.bvv.filegeneration.entity.TemplateField;
import com.bvv.filegeneration.common.enums.TemplateType;
import com.bvv.filegeneration.common.exceptions.TemplateNotFoundException;
import com.bvv.filegeneration.mapper.TemplateMapper;
import com.bvv.filegeneration.repository.TemplateRepository;
import com.bvv.filegeneration.utils.TemplateNamingRules;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final TemplateMapper templateMapper;

    public TemplateService(TemplateRepository templateRepository, TemplateMapper templateMapper) {
        this.templateRepository = templateRepository;
        this.templateMapper = templateMapper;
    }

    /**
     * Crée un nouveau gabarit
     */
    public TemplateDTO createTemplate(String name, String prefix, TemplateType type,
                                      List<TemplateField> fields) {
        Integer version = 1;
        String nomenclature = TemplateNamingRules.generateNomenclature(prefix, type, version);
        String filePath = TemplateNamingRules.generateTemplateFilePath(prefix, type, version);

        Template template = new Template();
        template.setName(name);
        template.setPrefix(prefix);
        template.setType(type);
        template.setVersion(version);
        template.setNomenclature(nomenclature);
        template.setFilePath(filePath);

        if (fields != null && !fields.isEmpty()) {
            for (TemplateField field : fields) {
                field.setTemplate(template);
                template.getFields().add(field);
            }
        }

        Template saved = templateRepository.save(template);
        return templateMapper.toDTO(saved);
    }

    /**
     * Récupère tous les gabarits
     */
    public List<TemplateDTO> getAllTemplates() {
        return templateRepository.findAll().stream()
                .map(templateMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un gabarit par ID
     */
    public TemplateDTO getTemplateById(Long id) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found with id: " + id));
        return templateMapper.toDTO(template);
    }

    /**
     * Récupère un gabarit par nom
     */
    public TemplateDTO getTemplateByName(String name) {
        Template template = templateRepository.findByName(name)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found with name: " + name));
        return templateMapper.toDTO(template);
    }

    /**
     * Met à jour un gabarit
     */
    public TemplateDTO updateTemplate(Long id, TemplateDTO dto) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found with id: " + id));

        if (dto.getName() != null) template.setName(dto.getName());
        if (dto.getPrefix() != null) template.setPrefix(dto.getPrefix());

        Template updated = templateRepository.save(template);
        return templateMapper.toDTO(updated);
    }

    /**
     * Supprime un gabarit
     */
    public void deleteTemplate(Long id) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found with id: " + id));
        templateRepository.delete(template);
    }
}

