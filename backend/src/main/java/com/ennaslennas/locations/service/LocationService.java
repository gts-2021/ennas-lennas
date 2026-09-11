package com.ennaslennas.locations.service;

import com.ennaslennas.locations.domain.Commune;
import com.ennaslennas.locations.domain.Wilaya;
import com.ennaslennas.locations.dto.CommuneResponse;
import com.ennaslennas.locations.dto.WilayaResponse;
import com.ennaslennas.locations.repository.CommuneRepository;
import com.ennaslennas.locations.repository.WilayaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {

    private final WilayaRepository wilayaRepository;
    private final CommuneRepository communeRepository;

    public List<WilayaResponse> getAllWilayas() {
        return wilayaRepository.findAllByOrderByCodeAsc().stream()
                .map(WilayaResponse::from)
                .toList();
    }

    public List<CommuneResponse> getCommunesByWilayaId(Long wilayaId) {
        return communeRepository.findByWilayaIdOrderByNameFrAsc(wilayaId).stream()
                .map(CommuneResponse::from)
                .toList();
    }
}
