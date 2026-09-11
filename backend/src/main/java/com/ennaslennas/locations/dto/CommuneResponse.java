package com.ennaslennas.locations.dto;

import com.ennaslennas.locations.domain.Commune;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommuneResponse {
    private Long id;
    private Long wilayaId;
    private String nameFr;
    private String nameAr;
    private String postalCode;

    public static CommuneResponse from(Commune commune) {
        return CommuneResponse.builder()
                .id(commune.getId())
                .wilayaId(commune.getWilaya().getId())
                .nameFr(commune.getNameFr())
                .nameAr(commune.getNameAr())
                .postalCode(commune.getPostalCode())
                .build();
    }
}
