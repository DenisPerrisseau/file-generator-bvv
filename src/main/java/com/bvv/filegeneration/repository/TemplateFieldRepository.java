package com.bvv.filegeneration.repository;

import com.bvv.filegeneration.entity.TemplateField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateFieldRepository extends JpaRepository<TemplateField, Long> {
    List<TemplateField> findByTemplateId(Long templateId);
}

