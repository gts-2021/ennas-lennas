package com.ennaslennas.helpoffers.dto.request;

import com.ennaslennas.helpoffers.domain.HelpOfferStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHelpOfferStatusRequest {

    @NotNull(message = "Le statut est requis.")
    private HelpOfferStatus status;

    private String adminNotes;
}
