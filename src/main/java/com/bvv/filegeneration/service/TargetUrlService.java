package com.bvv.filegeneration.service;

import com.bvv.filegeneration.dto.TargetUrlDTO;
import com.bvv.filegeneration.entity.TargetUrl;
import com.bvv.filegeneration.mapper.TargetUrlMapper;
import com.bvv.filegeneration.repository.TargetUrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TargetUrlService {

    private final TargetUrlRepository targetUrlRepository;
    private final TargetUrlMapper targetUrlMapper;

    @Transactional(readOnly = true)
    public List<TargetUrlDTO> getAllUrls() {
        return targetUrlRepository.findAllByOrderByNameAsc().stream()
                .map(targetUrlMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TargetUrlDTO> getActiveUrls() {
        return targetUrlRepository.findByActiveTrue().stream()
                .map(targetUrlMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TargetUrlDTO getUrlById(Long id) {
        TargetUrl url = targetUrlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL cible non trouvée avec ID: " + id));
        return targetUrlMapper.toDTO(url);
    }

    @Transactional
    public TargetUrlDTO createUrl(TargetUrlDTO dto) {
        TargetUrl url = targetUrlMapper.toEntity(dto);
        TargetUrl saved = targetUrlRepository.save(url);
        log.info("URL cible créée: {} - {}", saved.getName(), saved.getUrl());
        return targetUrlMapper.toDTO(saved);
    }

    @Transactional
    public TargetUrlDTO updateUrl(Long id, TargetUrlDTO dto) {
        TargetUrl existing = targetUrlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL cible non trouvée avec ID: " + id));

        existing.setName(dto.getName());
        existing.setUrl(dto.getUrl());
        existing.setBearerToken(dto.getBearerToken());
        existing.setDescription(dto.getDescription());
        existing.setActive(dto.getActive());

        TargetUrl updated = targetUrlRepository.save(existing);
        log.info("URL cible mise à jour: {}", updated.getName());
        return targetUrlMapper.toDTO(updated);
    }

    @Transactional
    public void deleteUrl(Long id) {
        if (!targetUrlRepository.existsById(id)) {
            throw new RuntimeException("URL cible non trouvée avec ID: " + id);
        }
        targetUrlRepository.deleteById(id);
        log.info("URL cible supprimée avec ID: {}", id);
    }

    /**
     * Récupère l'entité TargetUrl par ID (pour usage interne)
     */
    @Transactional(readOnly = true)
    public TargetUrl getTargetUrlEntity(Long id) {
        return targetUrlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL cible non trouvée avec ID: " + id));
    }
}

