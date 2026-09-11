package com.ennaslennas.helpoffers.service;

import com.ennaslennas.audit.service.AuditService;
import com.ennaslennas.cases.domain.Case;
import com.ennaslennas.cases.domain.CaseStatus;
import com.ennaslennas.cases.repository.CaseRepository;
import com.ennaslennas.common.exception.BusinessException;
import com.ennaslennas.common.exception.ResourceNotFoundException;
import com.ennaslennas.helpoffers.domain.HelpOffer;
import com.ennaslennas.helpoffers.domain.HelpOfferStatus;
import com.ennaslennas.helpoffers.dto.request.CreateHelpOfferRequest;
import com.ennaslennas.helpoffers.dto.request.UpdateHelpOfferStatusRequest;
import com.ennaslennas.helpoffers.dto.response.HelpOfferResponse;
import com.ennaslennas.helpoffers.repository.HelpOfferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HelpOfferService {

    private final HelpOfferRepository helpOfferRepository;
    private final CaseRepository caseRepository;
    private final AuditService auditService;

    /**
     * Submit an offer to help on a published case.
     */
    public HelpOfferResponse submitHelpOffer(String reference, CreateHelpOfferRequest request) {
        Case caseEntity = caseRepository.findByReferenceAndStatus(reference, CaseStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Cas introuvable ou non disponible aux propositions."));

        HelpOffer offer = HelpOffer.builder()
                .caseEntity(caseEntity)
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .phone(request.getPhone().trim())
                .email(request.getEmail() != null ? request.getEmail().trim() : null)
                .offerType(request.getOfferType().trim())
                .message(request.getMessage().trim())
                .status(HelpOfferStatus.NEW)
                .build();

        HelpOffer saved = helpOfferRepository.save(offer);
        log.info("Nouvelle proposition d'aide reçue pour le cas {}: par {}", reference, saved.getPhone());

        return HelpOfferResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<HelpOfferResponse> getOffers(HelpOfferStatus status, Pageable pageable) {
        if (status != null) {
            return helpOfferRepository.findByStatus(status, pageable).map(HelpOfferResponse::from);
        }
        return helpOfferRepository.findAll(pageable).map(HelpOfferResponse::from);
    }

    @Transactional(readOnly = true)
    public List<HelpOfferResponse> getOffersByCaseId(Long caseId) {
        return helpOfferRepository.findByCaseEntityIdOrderByCreatedAtDesc(caseId).stream()
                .map(HelpOfferResponse::from)
                .toList();
    }

    public HelpOfferResponse updateOfferStatus(Long offerId, UpdateHelpOfferStatusRequest request, String adminEmail) {
        HelpOffer offer = helpOfferRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Proposition d'aide introuvable: " + offerId));

        offer.setStatus(request.getStatus());
        if (request.getAdminNotes() != null) {
            offer.setAdminNotes(request.getAdminNotes());
        }
        offer.setUpdatedAt(Instant.now());

        HelpOffer saved = helpOfferRepository.save(offer);
        auditService.logAction(adminEmail, "UPDATE_HELP_OFFER_STATUS", "HELP_OFFER", offer.getId(),
                "Statut: " + request.getStatus() + ", Notes: " + request.getAdminNotes());

        return HelpOfferResponse.from(saved);
    }
}
