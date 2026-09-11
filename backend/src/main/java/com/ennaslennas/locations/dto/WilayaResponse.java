package com.ennaslennas.locations.dto;

import com.ennaslennas.locations.domain.Wilaya;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WilayaResponse {
    private Long id;
    private String code;
    private String nameFr;
    private String nameAr;

    public static WilayaResponse from(Wilaya wilaya) {
        return WilayaResponse.builder()
                .id(wilaya.getId())
                .code(wilaya.getCode())
                .nameFr(wilaya.getNameFr())
                .nameAr(wilaya.getNameAr())
                .build();
    }
}
