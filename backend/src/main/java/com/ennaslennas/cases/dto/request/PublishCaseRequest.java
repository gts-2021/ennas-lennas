package com.ennaslennas.cases.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishCaseRequest {

    @NotBlank(message = "Le titre public est requis.")
    @Size(min = 5, max = 250, message = "Le titre public doit contenir au moins 5 caractères.")
    private String publicTitle;

    @NotBlank(message = "La description publique est requise.")
    @Size(min = 10, message = "La description publique doit contenir au moins 10 caractères.")
    private String publicDescription;

    private String publicImageUrl;
}
