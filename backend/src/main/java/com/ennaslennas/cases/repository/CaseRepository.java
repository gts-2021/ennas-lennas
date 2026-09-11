package com.ennaslennas.cases.repository;

import com.ennaslennas.cases.domain.Case;
import com.ennaslennas.cases.domain.CaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<Case, Long>, JpaSpecificationExecutor<Case> {

    Optional<Case> findByReference(String reference);

    Optional<Case> findByReferenceAndStatus(String reference, CaseStatus status);

    Page<Case> findByStatus(CaseStatus status, Pageable pageable);

    @Query("SELECT c FROM Case c WHERE c.status = 'PUBLISHED' AND c.urgency IN ('HIGH', 'CRITICAL') ORDER BY c.publishedAt DESC")
    List<Case> findUrgentPublishedCases(Pageable pageable);

    @Query("SELECT c FROM Case c WHERE c.status = 'PUBLISHED' ORDER BY c.publishedAt DESC")
    List<Case> findLatestPublishedCases(Pageable pageable);

    long countByStatus(CaseStatus status);
}
