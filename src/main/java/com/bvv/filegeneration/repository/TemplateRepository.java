package com.bvv.filegeneration.repository;

import com.bvv.filegeneration.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    Optional<Template> findByName(String name);
    Optional<Template> findByNomenclature(String nomenclature);
}

