package com.ennaslennas.cases.controller;

import com.ennaslennas.cases.domain.UrgencyLevel;
import com.ennaslennas.cases.dto.response.PublicCaseDetailResponse;
import com.ennaslennas.cases.dto.response.PublicCaseSummaryResponse;
import com.ennaslennas.cases.service.CaseService;
import com.ennaslennas.common.dto.ApiResponse;
import com.ennaslennas.common.dto.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cases")
@RequiredArgsConstructor
public class PublicCaseController {

    private final CaseService caseService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<PublicCaseSummaryResponse>>> getPublicCases(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long wilayaId,
            @RequestParam(required = false) UrgencyLevel urgency,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<PublicCaseSummaryResponse> casesPage = caseService.getPublicCases(categoryId, wilayaId, urgency, PageRequest.of(page, size, sort));

        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.from(casesPage)));
    }

    @GetMapping("/{reference}")
    public ResponseEntity<ApiResponse<PublicCaseDetailResponse>> getPublicCaseByReference(@PathVariable String reference) {
        PublicCaseDetailResponse response = caseService.getPublicCaseByReference(reference);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/urgent")
    public ResponseEntity<ApiResponse<List<PublicCaseSummaryResponse>>> getUrgentCases(
            @RequestParam(defaultValue = "4") int limit
    ) {
        List<PublicCaseSummaryResponse> cases = caseService.getUrgentCases(limit);
        return ResponseEntity.ok(ApiResponse.ok(cases));
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<List<PublicCaseSummaryResponse>>> getLatestCases(
            @RequestParam(defaultValue = "6") int limit
    ) {
        List<PublicCaseSummaryResponse> cases = caseService.getLatestCases(limit);
        return ResponseEntity.ok(ApiResponse.ok(cases));
    }
}
