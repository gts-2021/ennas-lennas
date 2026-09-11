package com.ennaslennas.cases.controller;

import com.ennaslennas.cases.dto.request.CreateHelpRequestForm;
import com.ennaslennas.cases.dto.response.HelpRequestCreatedResponse;
import com.ennaslennas.cases.service.CaseService;
import com.ennaslennas.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/help-requests")
@RequiredArgsConstructor
public class PublicHelpRequestController {

    private final CaseService caseService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<HelpRequestCreatedResponse>> submitHelpRequest(
            @Valid @ModelAttribute CreateHelpRequestForm form) {

        HelpRequestCreatedResponse response = caseService.createHelpRequest(form);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Demande d'aide soumise avec succès.", response));
    }
}
