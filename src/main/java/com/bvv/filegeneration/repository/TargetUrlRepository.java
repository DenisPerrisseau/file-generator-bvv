package com.bvv.filegeneration.repository;

import com.bvv.filegeneration.entity.TargetUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TargetUrlRepository extends JpaRepository<TargetUrl, Long> {
    List<TargetUrl> findByActiveTrue();
    List<TargetUrl> findAllByOrderByNameAsc();
}

