package com.ennaslennas.helpoffers.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHelpOfferRequest {

    @NotBlank(message = "Le prénom est requis.")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Le nom est requis.")
    @Size(max = 100)
    private String lastName;

    @NotBlank(message = "Le numéro de téléphone est requis.")
    @Pattern(regexp = "^(0|\\+213|00213)[5-7][0-9]{8}$", message = "Format de numéro algérien invalide (ex: 0550123456).")
    private String phone;

    @Email(message = "Format d'adresse e-mail invalide.")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Le type d'aide proposé est requis.")
    private String offerType;

    @NotBlank(message = "Le message d'accompagnement est requis.")
    @Size(min = 5, message = "Le message doit comporter au moins 5 caractères.")
    private String message;
}
