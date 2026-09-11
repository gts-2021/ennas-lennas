package com.ennaslennas.locations.controller;

import com.ennaslennas.common.dto.ApiResponse;
import com.ennaslennas.locations.dto.CommuneResponse;
import com.ennaslennas.locations.dto.WilayaResponse;
import com.ennaslennas.locations.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/wilayas")
    public ResponseEntity<ApiResponse<List<WilayaResponse>>> getWilayas() {
        List<WilayaResponse> wilayas = locationService.getAllWilayas();
        return ResponseEntity.ok(ApiResponse.ok(wilayas));
    }

    @GetMapping("/wilayas/{id}/communes")
    public ResponseEntity<ApiResponse<List<CommuneResponse>>> getCommunes(@PathVariable Long id) {
        List<CommuneResponse> communes = locationService.getCommunesByWilayaId(id);
        return ResponseEntity.ok(ApiResponse.ok(communes));
    }
}
