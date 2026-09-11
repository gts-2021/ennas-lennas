package com.ennaslennas.cases.dto.response;

import com.ennaslennas.cases.domain.Case;
import com.ennaslennas.cases.domain.CaseStatus;
import com.ennaslennas.cases.domain.UrgencyLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicCaseSummaryResponse {

    private String reference;
    private String title;
    private String categoryCode;
    private String categoryNameFr;
    private String categoryNameAr;
    private String categoryIcon;
    private String wilayaCode;
    private String wilayaNameFr;
    private String wilayaNameAr;
    private String communeNameFr;
    private String communeNameAr;
    private UrgencyLevel urgency;
    private CaseStatus status;
    private BigDecimal amountNeeded;
    private BigDecimal amountCollected;
    private String publicImageUrl;
    private Instant publishedAt;

    public static PublicCaseSummaryResponse from(Case c) {
        return PublicCaseSummaryResponse.builder()
                .reference(c.getReference())
                .title(c.getPublicTitle() != null ? c.getPublicTitle() : c.getRawTitle())
                .categoryCode(c.getCategory().getCode())
                .categoryNameFr(c.getCategory().getNameFr())
                .categoryNameAr(c.getCategory().getNameAr())
                .categoryIcon(c.getCategory().getIcon())
                .wilayaCode(c.getWilaya().getCode())
                .wilayaNameFr(c.getWilaya().getNameFr())
                .wilayaNameAr(c.getWilaya().getNameAr())
                .communeNameFr(c.getCommune().getNameFr())
                .communeNameAr(c.getCommune().getNameAr())
                .urgency(c.getUrgency())
                .status(c.getStatus())
                .amountNeeded(c.getAmountNeeded())
                .amountCollected(c.getAmountCollected())
                .publicImageUrl(c.getPublicImageUrl())
                .publishedAt(c.getPublishedAt())
                .build();
    }
}
