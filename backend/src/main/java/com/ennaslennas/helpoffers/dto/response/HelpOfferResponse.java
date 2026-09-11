package com.ennaslennas.helpoffers.dto.response;

import com.ennaslennas.helpoffers.domain.HelpOffer;
import com.ennaslennas.helpoffers.domain.HelpOfferStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HelpOfferResponse {

    private Long id;
    private Long caseId;
    private String caseReference;
    private String caseTitle;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String offerType;
    private String message;
    private HelpOfferStatus status;
    private String adminNotes;
    private Instant createdAt;

    public static HelpOfferResponse from(HelpOffer offer) {
        Long caseId = offer.getCaseEntity() != null ? offer.getCaseEntity().getId() : null;
        String caseRef = offer.getCaseEntity() != null ? offer.getCaseEntity().getReference() : null;
        String caseTitle = null;
        if (offer.getCaseEntity() != null) {
            caseTitle = offer.getCaseEntity().getPublicTitle() != null 
                    ? offer.getCaseEntity().getPublicTitle() 
                    : offer.getCaseEntity().getRawTitle();
        }

        return HelpOfferResponse.builder()
                .id(offer.getId())
                .caseId(caseId)
                .caseReference(caseRef)
                .caseTitle(caseTitle)
                .firstName(offer.getFirstName())
                .lastName(offer.getLastName())
                .phone(offer.getPhone())
                .email(offer.getEmail())
                .offerType(offer.getOfferType())
                .message(offer.getMessage())
                .status(offer.getStatus())
                .adminNotes(offer.getAdminNotes())
                .createdAt(offer.getCreatedAt())
                .build();
    }
}
