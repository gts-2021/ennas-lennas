package com.ennaslennas.cases.controller;

import com.ennaslennas.cases.domain.Case;
import com.ennaslennas.cases.domain.CaseStatus;
import com.ennaslennas.cases.dto.request.PublishCaseRequest;
import com.ennaslennas.cases.dto.request.TransitionReasonRequest;
import com.ennaslennas.cases.dto.request.UpdateAdminCaseRequest;
import com.ennaslennas.cases.dto.response.AdminCaseDetailResponse;
import com.ennaslennas.cases.dto.response.DashboardStatsResponse;
import com.ennaslennas.cases.repository.CaseDocumentRepository;
import com.ennaslennas.cases.repository.CaseRepository;
import com.ennaslennas.cases.service.CaseWorkflowService;
import com.ennaslennas.common.dto.ApiResponse;
import com.ennaslennas.common.dto.PagedResponse;
import com.ennaslennas.common.exception.ResourceNotFoundException;
import com.ennaslennas.helpoffers.repository.HelpOfferRepository;
import com.ennaslennas.storage.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCaseController {

    private final CaseRepository caseRepository;
    private final CaseDocumentRepository caseDocumentRepository;
    private final CaseWorkflowService caseWorkflowService;
    private final StorageService storageService;
    private final HelpOfferRepository helpOfferRepository;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        DashboardStatsResponse stats = DashboardStatsResponse.builder()
                .totalReceived(caseRepository.count())
                .submitted(caseRepository.countByStatus(CaseStatus.SUBMITTED))
                .underReview(caseRepository.countByStatus(CaseStatus.UNDER_REVIEW))
                .needMoreInfo(caseRepository.countByStatus(CaseStatus.NEED_MORE_INFO))
                .published(caseRepository.countByStatus(CaseStatus.PUBLISHED))
                .inProgress(caseRepository.countByStatus(CaseStatus.IN_PROGRESS))
                .completed(caseRepository.countByStatus(CaseStatus.COMPLETED))
                .totalHelpOffers(helpOfferRepository.count())
                .newHelpOffers(helpOfferRepository.countByStatus(com.ennaslennas.helpoffers.domain.HelpOfferStatus.NEW))
                .build();

        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/cases")
    public ResponseEntity<ApiResponse<PagedResponse<AdminCaseDetailResponse>>> getAdminCases(
            @RequestParam(required = false) CaseStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Specification<Case> spec = (root, query, cb) -> cb.conjunction();
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (search != null && !search.isBlank()) {
            String pattern = "%" + search.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("reference")), pattern),
                    cb.like(cb.lower(root.get("rawTitle")), pattern),
                    cb.like(cb.lower(root.get("requester").get("phone")), pattern),
                    cb.like(cb.lower(root.get("requester").get("lastName")), pattern)
            ));
        }

        Page<Case> casesPage = caseRepository.findAll(spec, PageRequest.of(page, size, sort));
        Page<AdminCaseDetailResponse> responsePage = casesPage.map(c -> toAdminCaseDetail(c, false));

        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.from(responsePage)));
    }

    @GetMapping("/cases/{id}")
    public ResponseEntity<ApiResponse<AdminCaseDetailResponse>> getAdminCaseById(@PathVariable Long id) {
        Case c = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dossier introuvable: " + id));

        return ResponseEntity.ok(ApiResponse.ok(toAdminCaseDetail(c, true)));
    }

    @Transactional
    @PatchMapping("/cases/{id}")
    public ResponseEntity<ApiResponse<AdminCaseDetailResponse>> updateAdminCase(
            @PathVariable Long id,
            @RequestBody UpdateAdminCaseRequest request
    ) {
        Case c = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dossier introuvable: " + id));

        if (request.getPublicTitle() != null) c.setPublicTitle(request.getPublicTitle());
        if (request.getPublicDescription() != null) c.setPublicDescription(request.getPublicDescription());
        if (request.getPublicImageUrl() != null) c.setPublicImageUrl(request.getPublicImageUrl());
        if (request.getUrgency() != null) c.setUrgency(request.getUrgency());
        if (request.getAmountNeeded() != null) c.setAmountNeeded(request.getAmountNeeded());
        if (request.getInternalNotes() != null) {
            c.getRequester().setInternalNotes(request.getInternalNotes());
        }

        c.setUpdatedAt(Instant.now());
        Case saved = caseRepository.save(c);

        return ResponseEntity.ok(ApiResponse.ok("Dossier mis à jour.", toAdminCaseDetail(saved, true)));
    }

    @Transactional
    @PostMapping("/cases/{id}/review")
    public ResponseEntity<ApiResponse<AdminCaseDetailResponse>> takeUnderReview(
            @PathVariable Long id,
            Authentication auth
    ) {
        Case updated = caseWorkflowService.takeUnderReview(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Dossier pris en charge pour vérification.", toAdminCaseDetail(updated, true)));
    }

    @Transactional
    @PostMapping("/cases/{id}/request-info")
    public ResponseEntity<ApiResponse<AdminCaseDetailResponse>> requestInfo(
            @PathVariable Long id,
            @RequestBody(required = false) TransitionReasonRequest request,
            Authentication auth
    ) {
        String reason = request != null ? request.getReason() : "Informations complémentaires demandées";
        Case updated = caseWorkflowService.requestMoreInfo(id, reason, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Statut mis à jour : Compléments demandés.", toAdminCaseDetail(updated, true)));
    }

    @Transactional
    @PostMapping("/cases/{id}/approve")
    public ResponseEntity<ApiResponse<AdminCaseDetailResponse>> approveCase(
            @PathVariable Long id,
            Authentication auth
    ) {
        Case updated = caseWorkflowService.approveCase(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Dossier approuvé. Prêt pour publication.", toAdminCaseDetail(updated, true)));
    }

    @Transactional
    @PostMapping("/cases/{id}/publish")
    public ResponseEntity<ApiResponse<AdminCaseDetailResponse>> publishCase(
            @PathVariable Long id,
            @Valid @RequestBody PublishCaseRequest request,
            Authentication auth
    ) {
        Case updated = caseWorkflowService.publishCase(
                id,
                request.getPublicTitle(),
                request.getPublicDescription(),
                request.getPublicImageUrl(),
                auth.getName()
        );
        return ResponseEntity.ok(ApiResponse.ok("Cas publié avec succès sur le portail public.", toAdminCaseDetail(updated, true)));
    }

    @Transactional
    @PostMapping("/cases/{id}/in-progress")
    public ResponseEntity<ApiResponse<AdminCaseDetailResponse>> markInProgress(
            @PathVariable Long id,
            Authentication auth
    ) {
        Case updated = caseWorkflowService.markInProgress(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Statut passé à : En cours d'aide.", toAdminCaseDetail(updated, true)));
    }

    @Transactional
    @PostMapping("/cases/{id}/completed")
    public ResponseEntity<ApiResponse<AdminCaseDetailResponse>> markCompleted(
            @PathVariable Long id,
            Authentication auth
    ) {
        Case updated = caseWorkflowService.markCompleted(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Besoin marqué comme totalement satisfait.", toAdminCaseDetail(updated, true)));
    }

    @Transactional
    @PostMapping("/cases/{id}/close")
    public ResponseEntity<ApiResponse<AdminCaseDetailResponse>> closeCase(
            @PathVariable Long id,
            @RequestBody(required = false) TransitionReasonRequest request,
            Authentication auth
    ) {
        String notes = request != null ? request.getReason() : "Clôture normale";
        Case updated = caseWorkflowService.closeCase(id, notes, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Dossier clôturé et archivé.", toAdminCaseDetail(updated, true)));
    }

    @Transactional
    @PostMapping("/cases/{id}/reject")
    public ResponseEntity<ApiResponse<AdminCaseDetailResponse>> rejectCase(
            @PathVariable Long id,
            @RequestBody(required = false) TransitionReasonRequest request,
            Authentication auth
    ) {
        String reason = request != null ? request.getReason() : "Non conforme aux critères";
        Case updated = caseWorkflowService.rejectCase(id, reason, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Dossier rejeté.", toAdminCaseDetail(updated, true)));
    }

    private AdminCaseDetailResponse toAdminCaseDetail(Case c, boolean includePresignedUrls) {
        List<AdminCaseDetailResponse.DocumentDto> docDtos = (c.getDocuments() == null ? Collections.<com.ennaslennas.cases.domain.CaseDocument>emptyList() : c.getDocuments())
                .stream()
                .map(doc -> {
                    String downloadUrl = null;
                    if (includePresignedUrls && doc.getStorageKey() != null) {
                        try {
                            downloadUrl = storageService.getPresignedUrl(doc.getStorageKey());
                        } catch (Exception ignored) {
                            // S3 presigned URL error should not break the response
                        }
                    }
                    return AdminCaseDetailResponse.DocumentDto.builder()
                            .id(doc.getId())
                            .originalFilename(doc.getOriginalFilename())
                            .mimeType(doc.getMimeType())
                            .fileSize(doc.getFileSize())
                            .isVerified(doc.isVerified())
                            .downloadUrl(downloadUrl)
                            .createdAt(doc.getCreatedAt())
                            .build();
                }).toList();

        return AdminCaseDetailResponse.from(c, docDtos);
    }
}
