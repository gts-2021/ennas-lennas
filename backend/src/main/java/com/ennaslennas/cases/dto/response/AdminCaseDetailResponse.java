package com.ennaslennas.cases.dto.response;

import com.ennaslennas.cases.domain.Case;
import com.ennaslennas.cases.domain.CaseDocument;
import com.ennaslennas.cases.domain.CaseStatus;
import com.ennaslennas.cases.domain.UrgencyLevel;
import com.ennaslennas.requesters.domain.Requester;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCaseDetailResponse {

    private Long id;
    private String reference;

    // Private Requester Info
    private RequesterDto requester;

    // Raw Private Submission
    private String rawTitle;
    private String rawDescription;

    // Public Curated Content
    private String publicTitle;
    private String publicDescription;
    private String publicImageUrl;

    // Classification & Location
    private Long categoryId;
    private String categoryNameFr;
    private Long wilayaId;
    private String wilayaNameFr;
    private Long communeId;
    private String communeNameFr;

    // Status & Financials
    private UrgencyLevel urgency;
    private CaseStatus status;
    private BigDecimal amountNeeded;
    private BigDecimal amountCollected;

    // Private Justificative Documents
    private List<DocumentDto> documents;

    // Timestamps
    private Instant createdAt;
    private Instant updatedAt;
    private Instant publishedAt;
    private Instant closedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequesterDto {
        private Long id;
        private String firstName;
        private String lastName;
        private String phone;
        private String email;
        private String wilayaName;
        private String communeName;
        private String internalNotes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentDto {
        private Long id;
        private String originalFilename;
        private String mimeType;
        private Long fileSize;
        private boolean isVerified;
        private String downloadUrl;
        private Instant createdAt;
    }

    public static AdminCaseDetailResponse from(Case c, List<DocumentDto> documentDtos) {
        Requester r = c.getRequester();
        RequesterDto requesterDto = r == null ? null : RequesterDto.builder()
                .id(r.getId())
                .firstName(r.getFirstName())
                .lastName(r.getLastName())
                .phone(r.getPhone())
                .email(r.getEmail())
                .wilayaName(r.getWilaya() != null ? r.getWilaya().getNameFr() : null)
                .communeName(r.getCommune() != null ? r.getCommune().getNameFr() : null)
                .internalNotes(r.getInternalNotes())
                .build();

        return AdminCaseDetailResponse.builder()
                .id(c.getId())
                .reference(c.getReference())
                .requester(requesterDto)
                .rawTitle(c.getRawTitle())
                .rawDescription(c.getRawDescription())
                .publicTitle(c.getPublicTitle())
                .publicDescription(c.getPublicDescription())
                .publicImageUrl(c.getPublicImageUrl())
                .categoryId(c.getCategory() != null ? c.getCategory().getId() : null)
                .categoryNameFr(c.getCategory() != null ? c.getCategory().getNameFr() : null)
                .wilayaId(c.getWilaya() != null ? c.getWilaya().getId() : null)
                .wilayaNameFr(c.getWilaya() != null ? c.getWilaya().getNameFr() : null)
                .communeId(c.getCommune() != null ? c.getCommune().getId() : null)
                .communeNameFr(c.getCommune() != null ? c.getCommune().getNameFr() : null)
                .urgency(c.getUrgency())
                .status(c.getStatus())
                .amountNeeded(c.getAmountNeeded())
                .amountCollected(c.getAmountCollected())
                .documents(documentDtos != null ? documentDtos : List.of())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .publishedAt(c.getPublishedAt())
                .closedAt(c.getClosedAt())
                .build();
    }
}
