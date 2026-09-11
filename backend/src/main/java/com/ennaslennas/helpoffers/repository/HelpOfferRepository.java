package com.ennaslennas.helpoffers.repository;

import com.ennaslennas.helpoffers.domain.HelpOffer;
import com.ennaslennas.helpoffers.domain.HelpOfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HelpOfferRepository extends JpaRepository<HelpOffer, Long> {
    List<HelpOffer> findByCaseEntityIdOrderByCreatedAtDesc(Long caseId);
    Page<HelpOffer> findByStatus(HelpOfferStatus status, Pageable pageable);
    long countByStatus(HelpOfferStatus status);
}
