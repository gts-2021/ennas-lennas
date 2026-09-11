package com.ennaslennas.helpoffers.controller;

import com.ennaslennas.common.dto.ApiResponse;
import com.ennaslennas.helpoffers.dto.request.CreateHelpOfferRequest;
import com.ennaslennas.helpoffers.dto.response.HelpOfferResponse;
import com.ennaslennas.helpoffers.service.HelpOfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cases")
@RequiredArgsConstructor
public class PublicHelpOfferController {

    private final HelpOfferService helpOfferService;

    @PostMapping("/{reference}/help-offers")
    public ResponseEntity<ApiResponse<HelpOfferResponse>> submitHelpOffer(
            @PathVariable String reference,
            @Valid @RequestBody CreateHelpOfferRequest request
    ) {
        HelpOfferResponse response = helpOfferService.submitHelpOffer(reference, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Votre proposition d'aide a été transmise à notre équipe avec succès. Baraka Allahou fikoum.", response));
    }
}
