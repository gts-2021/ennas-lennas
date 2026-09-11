package com.ennaslennas.cases.service;

import com.ennaslennas.audit.service.AuditService;
import com.ennaslennas.cases.domain.Case;
import com.ennaslennas.cases.domain.CaseStatus;
import com.ennaslennas.cases.repository.CaseRepository;
import com.ennaslennas.common.exception.BusinessException;
import com.ennaslennas.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CaseWorkflowService {

    private final CaseRepository caseRepository;
    private final AuditService auditService;

    public Case takeUnderReview(Long caseId, String adminEmail) {
        Case c = getCase(caseId);
        if (c.getStatus() != CaseStatus.SUBMITTED && c.getStatus() != CaseStatus.NEED_MORE_INFO) {
            throw new BusinessException("Impossible de passer en revue un dossier avec le statut : " + c.getStatus());
        }

        c.setStatus(CaseStatus.UNDER_REVIEW);
        c.setUpdatedAt(Instant.now());
        Case saved = caseRepository.save(c);

        auditService.logAction(adminEmail, "TAKE_UNDER_REVIEW", "CASE", c.getId(), "Dossier pris en charge pour vérification.");
        return saved;
    }

    public Case requestMoreInfo(Long caseId, String notes, String adminEmail) {
        Case c = getCase(caseId);
        if (c.getStatus() != CaseStatus.UNDER_REVIEW) {
            throw new BusinessException("La demande d'informations complémentaires requiert le statut UNDER_REVIEW.");
        }

        c.setStatus(CaseStatus.NEED_MORE_INFO);
        c.setUpdatedAt(Instant.now());
        Case saved = caseRepository.save(c);

        auditService.logAction(adminEmail, "REQUEST_MORE_INFO", "CASE", c.getId(), notes);
        return saved;
    }

    public Case approveCase(Long caseId, String adminEmail) {
        Case c = getCase(caseId);
        if (c.getStatus() != CaseStatus.UNDER_REVIEW) {
            throw new BusinessException("Seul un dossier en cours de revue (UNDER_REVIEW) peut être approuvé.");
        }

        c.setStatus(CaseStatus.APPROVED);
        c.setUpdatedAt(Instant.now());

        // Pre-fill public drafts if empty
        if (c.getPublicTitle() == null || c.getPublicTitle().isBlank()) {
            c.setPublicTitle(c.getRawTitle());
        }
        if (c.getPublicDescription() == null || c.getPublicDescription().isBlank()) {
            c.setPublicDescription(c.getRawDescription());
        }

        Case saved = caseRepository.save(c);
        auditService.logAction(adminEmail, "APPROVE", "CASE", c.getId(), "Dossier validé sur le fond.");
        return saved;
    }

    public Case publishCase(Long caseId, String publicTitle, String publicDescription, String publicImageUrl, String adminEmail) {
        Case c = getCase(caseId);
        if (c.getStatus() != CaseStatus.APPROVED && c.getStatus() != CaseStatus.PUBLISHED) {
            throw new BusinessException("Le dossier doit d'abord être validé (APPROVED) avant d'être publié.");
        }

        if (publicTitle == null || publicTitle.trim().length() < 5) {
            throw new BusinessException("Un titre public soigné (min 5 caractères) est requis pour publier.");
        }
        if (publicDescription == null || publicDescription.trim().length() < 10) {
            throw new BusinessException("Une description publique respectueuse et anonymisée (min 10 caractères) est requise.");
        }

        c.setPublicTitle(publicTitle.trim());
        c.setPublicDescription(publicDescription.trim());
        if (publicImageUrl != null && !publicImageUrl.isBlank()) {
            c.setPublicImageUrl(publicImageUrl.trim());
        }

        c.setStatus(CaseStatus.PUBLISHED);
        c.setUpdatedAt(Instant.now());
        if (c.getPublishedAt() == null) {
            c.setPublishedAt(Instant.now());
        }

        Case saved = caseRepository.save(c);
        auditService.logAction(adminEmail, "PUBLISH", "CASE", c.getId(), "Cas publié sur le portail public.");
        return saved;
    }

    public Case markInProgress(Long caseId, String adminEmail) {
        Case c = getCase(caseId);
        if (c.getStatus() != CaseStatus.PUBLISHED) {
            throw new BusinessException("Seul un cas publié peut passer au statut IN_PROGRESS.");
        }

        c.setStatus(CaseStatus.IN_PROGRESS);
        c.setUpdatedAt(Instant.now());
        Case saved = caseRepository.save(c);

        auditService.logAction(adminEmail, "MARK_IN_PROGRESS", "CASE", c.getId(), "Mise en relation aidant effectuée.");
        return saved;
    }

    public Case markCompleted(Long caseId, String adminEmail) {
        Case c = getCase(caseId);
        if (c.getStatus() != CaseStatus.IN_PROGRESS && c.getStatus() != CaseStatus.PUBLISHED) {
            throw new BusinessException("Seul un cas publié ou en cours peut être marqué comme COMPLETED.");
        }

        c.setStatus(CaseStatus.COMPLETED);
        c.setUpdatedAt(Instant.now());
        Case saved = caseRepository.save(c);

        auditService.logAction(adminEmail, "MARK_COMPLETED", "CASE", c.getId(), "Besoin satisfait avec succès.");
        return saved;
    }

    public Case closeCase(Long caseId, String notes, String adminEmail) {
        Case c = getCase(caseId);
        if (c.getStatus() == CaseStatus.CLOSED || c.getStatus() == CaseStatus.REJECTED) {
            throw new BusinessException("Ce dossier est déjà clôturé.");
        }

        c.setStatus(CaseStatus.CLOSED);
        c.setClosedAt(Instant.now());
        c.setUpdatedAt(Instant.now());
        Case saved = caseRepository.save(c);

        auditService.logAction(adminEmail, "CLOSE", "CASE", c.getId(), notes != null ? notes : "Clôture administrative.");
        return saved;
    }

    public Case rejectCase(Long caseId, String reason, String adminEmail) {
        Case c = getCase(caseId);
        if (c.getStatus() != CaseStatus.SUBMITTED && c.getStatus() != CaseStatus.UNDER_REVIEW) {
            throw new BusinessException("Seul un dossier au stade initial peut être rejeté.");
        }

        c.setStatus(CaseStatus.REJECTED);
        c.setClosedAt(Instant.now());
        c.setUpdatedAt(Instant.now());
        Case saved = caseRepository.save(c);

        auditService.logAction(adminEmail, "REJECT", "CASE", c.getId(), "Motif du rejet: " + reason);
        return saved;
    }

    private Case getCase(Long caseId) {
        return caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Dossier introuvable avec l'ID: " + caseId));
    }
}
