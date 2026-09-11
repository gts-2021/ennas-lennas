package com.ennaslennas.categories.dto;

import com.ennaslennas.categories.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String code;
    private String nameFr;
    private String nameAr;
    private String icon;
    private int displayOrder;

    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .code(category.getCode())
                .nameFr(category.getNameFr())
                .nameAr(category.getNameAr())
                .icon(category.getIcon())
                .displayOrder(category.getDisplayOrder())
                .build();
    }
}
