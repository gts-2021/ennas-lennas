package com.ennaslennas.cases.dto.request;

import com.ennaslennas.cases.domain.UrgencyLevel;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHelpRequestForm {

    @NotBlank(message = "Le prénom est obligatoire.")
    @Size(max = 100, message = "Le prénom ne peut dépasser 100 caractères.")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 100, message = "Le nom ne peut dépasser 100 caractères.")
    private String lastName;

    @NotBlank(message = "Le numéro de téléphone est obligatoire.")
    @Pattern(regexp = "^(0|\\+213|00213)[5-7][0-9]{8}$", message = "Format de numéro algérien invalide (ex: 0550123456, 0661123456, 0770123456).")
    private String phone;

    @Email(message = "Format d'adresse e-mail invalide.")
    @Size(max = 150, message = "L'adresse email ne peut dépasser 150 caractères.")
    private String email;

    @NotNull(message = "La wilaya est obligatoire.")
    private Long wilayaId;

    @NotNull(message = "La commune est obligatoire.")
    private Long communeId;

    @NotNull(message = "La catégorie est obligatoire.")
    private Long categoryId;

    @NotBlank(message = "Le titre de la demande est obligatoire.")
    @Size(min = 5, max = 250, message = "Le titre doit comporter entre 5 et 250 caractères.")
    private String title;

    @NotBlank(message = "La description détaillée de votre situation est obligatoire.")
    @Size(min = 10, message = "La description doit comporter au moins 10 caractères.")
    private String description;

    @Builder.Default
    private UrgencyLevel urgency = UrgencyLevel.MEDIUM;

    @PositiveOrZero(message = "Le montant estimatif doit être positif.")
    private BigDecimal amountNeeded;

    private List<MultipartFile> documents;
}
