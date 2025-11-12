package com.bvv.filegeneration.service;

import com.bvv.filegeneration.dto.TemplateDTO;
import com.bvv.filegeneration.entity.Template;
import com.bvv.filegeneration.common.enums.TemplateType;
import com.bvv.filegeneration.common.exceptions.TemplateNotFoundException;
import com.bvv.filegeneration.mapper.TemplateMapper;
import com.bvv.filegeneration.repository.TemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TemplateServiceTest {

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private TemplateMapper templateMapper;

    @InjectMocks
    private TemplateService templateService;

    private Template template;
    private TemplateDTO templateDTO;

    @BeforeEach
    void setUp() {
        template = Template.builder()
                .id(1L)
                .name("CLIENTS_TEMPLATE")
                .prefix("CLIENTS")
                .type(TemplateType.JSON)
                .version(1)
                .nomenclature("CLIENTS_JSON_20251112_v1.json")
                .filePath("files/templates/CLIENTS_JSON_20251112_v1.json")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        templateDTO = TemplateDTO.builder()
                .id(1L)
                .name("CLIENTS_TEMPLATE")
                .prefix("CLIENTS")
                .type(TemplateType.JSON)
                .version(1)
                .nomenclature("CLIENTS_JSON_20251112_v1.json")
                .filePath("files/templates/CLIENTS_JSON_20251112_v1.json")
                .build();
    }

    @Test
    void testGetTemplateById_Success() {
        when(templateRepository.findById(1L)).thenReturn(Optional.of(template));
        when(templateMapper.toDTO(template)).thenReturn(templateDTO);

        TemplateDTO result = templateService.getTemplateById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("CLIENTS_TEMPLATE", result.getName());
        verify(templateRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTemplateById_NotFound() {
        when(templateRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TemplateNotFoundException.class, () -> templateService.getTemplateById(999L));
        verify(templateRepository, times(1)).findById(999L);
    }

    @Test
    void testGetTemplateByName_Success() {
        when(templateRepository.findByName("CLIENTS_TEMPLATE")).thenReturn(Optional.of(template));
        when(templateMapper.toDTO(template)).thenReturn(templateDTO);

        TemplateDTO result = templateService.getTemplateByName("CLIENTS_TEMPLATE");

        assertNotNull(result);
        assertEquals("CLIENTS_TEMPLATE", result.getName());
        verify(templateRepository, times(1)).findByName("CLIENTS_TEMPLATE");
    }
}

