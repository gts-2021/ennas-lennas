package com.ennaslennas.cases.service;

import com.ennaslennas.cases.domain.Case;
import com.ennaslennas.cases.domain.CaseDocument;
import com.ennaslennas.cases.domain.CaseStatus;
import com.ennaslennas.cases.domain.UrgencyLevel;
import com.ennaslennas.cases.dto.request.CreateHelpRequestForm;
import com.ennaslennas.cases.dto.response.HelpRequestCreatedResponse;
import com.ennaslennas.cases.dto.response.PublicCaseDetailResponse;
import com.ennaslennas.cases.dto.response.PublicCaseSummaryResponse;
import com.ennaslennas.cases.repository.CaseDocumentRepository;
import com.ennaslennas.cases.repository.CaseRepository;
import com.ennaslennas.categories.domain.Category;
import com.ennaslennas.categories.repository.CategoryRepository;
import com.ennaslennas.common.exception.ResourceNotFoundException;
import com.ennaslennas.locations.domain.Commune;
import com.ennaslennas.locations.domain.Wilaya;
import com.ennaslennas.locations.repository.CommuneRepository;
import com.ennaslennas.locations.repository.WilayaRepository;
import com.ennaslennas.requesters.domain.Requester;
import com.ennaslennas.requesters.repository.RequesterRepository;
import com.ennaslennas.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CaseService {

    private final CaseRepository caseRepository;
    private final CaseDocumentRepository caseDocumentRepository;
    private final RequesterRepository requesterRepository;
    private final WilayaRepository wilayaRepository;
    private final CommuneRepository communeRepository;
    private final CategoryRepository categoryRepository;
    private final StorageService storageService;
    private final ReferenceGenerator referenceGenerator;

    /**
     * Submit a new help request from public multi-step form.
     */
    public HelpRequestCreatedResponse createHelpRequest(CreateHelpRequestForm form) {
        Wilaya wilaya = wilayaRepository.findById(form.getWilayaId())
                .orElseThrow(() -> new ResourceNotFoundException("Wilaya introuvable"));

        Commune commune = communeRepository.findById(form.getCommuneId())
                .orElseThrow(() -> new ResourceNotFoundException("Commune introuvable"));

        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable"));

        // Find or create Requester by phone number
        Requester requester = requesterRepository.findByPhone(form.getPhone().trim())
                .orElseGet(() -> Requester.builder()
                        .firstName(form.getFirstName().trim())
                        .lastName(form.getLastName().trim())
                        .phone(form.getPhone().trim())
                        .email(form.getEmail() != null ? form.getEmail().trim() : null)
                        .wilaya(wilaya)
                        .commune(commune)
                        .build()
                );

        // Update name or email if needed
        requester.setFirstName(form.getFirstName().trim());
        requester.setLastName(form.getLastName().trim());
        if (form.getEmail() != null && !form.getEmail().isBlank()) {
            requester.setEmail(form.getEmail().trim());
        }
        requester = requesterRepository.save(requester);

        // Generate unique reference
        String reference = referenceGenerator.generateNextReference();

        // Create Case
        Case caseEntity = Case.builder()
                .reference(reference)
                .requester(requester)
                .category(category)
                .wilaya(wilaya)
                .commune(commune)
                .rawTitle(form.getTitle().trim())
                .rawDescription(form.getDescription().trim())
                .urgency(form.getUrgency() != null ? form.getUrgency() : UrgencyLevel.MEDIUM)
                .status(CaseStatus.SUBMITTED)
                .amountNeeded(form.getAmountNeeded())
                .build();

        // Handle file uploads if any
        if (form.getDocuments() != null && !form.getDocuments().isEmpty()) {
            for (MultipartFile file : form.getDocuments()) {
                if (file != null && !file.isEmpty()) {
                    StorageService.StoredFileInfo stored = storageService.storeFile(file);
                    CaseDocument document = CaseDocument.builder()
                            .originalFilename(stored.originalFilename())
                            .storageKey(stored.storageKey())
                            .mimeType(stored.mimeType())
                            .fileSize(stored.fileSize())
                            .documentType("JUSTIFICATIVE")
                            .isVerified(false)
                            .build();
                    caseEntity.addDocument(document);
                }
            }
        }

        caseRepository.save(caseEntity);
        log.info("Demande d'aide créée avec succès. Réf: {}", reference);

        return HelpRequestCreatedResponse.builder()
                .reference(reference)
                .message("Votre demande d'aide a été enregistrée avec succès. Notre équipe va l'étudier avec soin et discrétion avant toute publication.")
                .build();
    }

    /**
     * Public catalog with search filters (only PUBLISHED status).
     */
    @Transactional(readOnly = true)
    public Page<PublicCaseSummaryResponse> getPublicCases(Long categoryId, Long wilayaId, UrgencyLevel urgency, Pageable pageable) {
        Specification<Case> spec = (root, query, cb) -> cb.equal(root.get("status"), CaseStatus.PUBLISHED);

        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
        }
        if (wilayaId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("wilaya").get("id"), wilayaId));
        }
        if (urgency != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("urgency"), urgency));
        }

        return caseRepository.findAll(spec, pageable).map(PublicCaseSummaryResponse::from);
    }

    /**
     * Public detail by reference.
     */
    @Transactional(readOnly = true)
    public PublicCaseDetailResponse getPublicCaseByReference(String reference) {
        Case c = caseRepository.findByReferenceAndStatus(reference, CaseStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Demande d'aide introuvable ou non encore publiée."));
        return PublicCaseDetailResponse.from(c);
    }

    /**
     * Urgent published cases for homepage.
     */
    @Transactional(readOnly = true)
    public List<PublicCaseSummaryResponse> getUrgentCases(int limit) {
        return caseRepository.findUrgentPublishedCases(PageRequest.of(0, limit)).stream()
                .map(PublicCaseSummaryResponse::from)
                .toList();
    }

    /**
     * Latest published cases for homepage.
     */
    @Transactional(readOnly = true)
    public List<PublicCaseSummaryResponse> getLatestCases(int limit) {
        return caseRepository.findLatestPublishedCases(PageRequest.of(0, limit)).stream()
                .map(PublicCaseSummaryResponse::from)
                .toList();
    }
}
