package com.ennaslennas.helpoffers.controller;

import com.ennaslennas.common.dto.ApiResponse;
import com.ennaslennas.common.dto.PagedResponse;
import com.ennaslennas.helpoffers.domain.HelpOfferStatus;
import com.ennaslennas.helpoffers.dto.request.UpdateHelpOfferStatusRequest;
import com.ennaslennas.helpoffers.dto.response.HelpOfferResponse;
import com.ennaslennas.helpoffers.service.HelpOfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/help-offers")
@RequiredArgsConstructor
public class AdminHelpOfferController {

    private final HelpOfferService helpOfferService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<HelpOfferResponse>>> getOffers(
            @RequestParam(required = false) HelpOfferStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<HelpOfferResponse> offers = helpOfferService.getOffers(status, PageRequest.of(page, size, sort));
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.from(offers)));
    }

    @GetMapping("/by-case/{caseId}")
    public ResponseEntity<ApiResponse<List<HelpOfferResponse>>> getOffersByCase(@PathVariable Long caseId) {
        List<HelpOfferResponse> offers = helpOfferService.getOffersByCaseId(caseId);
        return ResponseEntity.ok(ApiResponse.ok(offers));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<HelpOfferResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateHelpOfferStatusRequest request,
            Authentication auth
    ) {
        HelpOfferResponse updated = helpOfferService.updateOfferStatus(id, request, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Statut de l'offre d'aide mis à jour.", updated));
    }
}
