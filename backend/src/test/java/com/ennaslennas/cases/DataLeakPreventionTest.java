package com.ennaslennas.cases;

import com.ennaslennas.cases.domain.Case;
import com.ennaslennas.cases.domain.CaseStatus;
import com.ennaslennas.cases.domain.UrgencyLevel;
import com.ennaslennas.cases.dto.response.PublicCaseDetailResponse;
import com.ennaslennas.cases.dto.response.PublicCaseSummaryResponse;
import com.ennaslennas.categories.domain.Category;
import com.ennaslennas.locations.domain.Commune;
import com.ennaslennas.locations.domain.Wilaya;
import com.ennaslennas.requesters.domain.Requester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DataLeakPreventionTest {

    private static final List<String> FORBIDDEN_WORDS = Arrays.asList(
            "phone", "telephone", "email", "mail", "firstname", "lastname",
            "internalnotes", "storagekey", "requester"
    );

    @Test
    @DisplayName("Privacy By Design: PublicCaseSummaryResponse ne doit contenir aucun champ privé ou sensible")
    void verifyPublicCaseSummaryDoesNotLeakSensitiveFields() {
        for (Field field : PublicCaseSummaryResponse.class.getDeclaredFields()) {
            String fieldNameLower = field.getName().toLowerCase();
            for (String forbidden : FORBIDDEN_WORDS) {
                assertThat(fieldNameLower)
                        .withFailMessage("Violation de confidentialité : le champ '%s' dans PublicCaseSummaryResponse expose potentiellement une donnée privée.", field.getName())
                        .doesNotContain(forbidden);
            }
        }
    }

    @Test
    @DisplayName("Privacy By Design: PublicCaseDetailResponse ne doit contenir aucun champ privé ou sensible")
    void verifyPublicCaseDetailDoesNotLeakSensitiveFields() {
        for (Field field : PublicCaseDetailResponse.class.getDeclaredFields()) {
            String fieldNameLower = field.getName().toLowerCase();
            for (String forbidden : FORBIDDEN_WORDS) {
                assertThat(fieldNameLower)
                        .withFailMessage("Violation de confidentialité : le champ '%s' dans PublicCaseDetailResponse expose potentiellement une donnée privée.", field.getName())
                        .doesNotContain(forbidden);
            }
        }
    }

    @Test
    @DisplayName("Le mapping vers PublicCaseDetailResponse n'expose que la description publique et non la description brute privée")
    void verifyPublicDescriptionIsUsedWhenAvailable() {
        Wilaya wilaya = Wilaya.builder().code("16").nameFr("Alger").nameAr("الجزائر").build();
        Commune commune = Commune.builder().nameFr("Hydra").nameAr("حيدرة").build();
        Category cat = Category.builder().code("HEALTH").nameFr("Santé").nameAr("صحة").icon("Heart").build();
        Requester req = Requester.builder().firstName("Confidentiel").lastName("Secret").phone("0550999999").build();

        Case testCase = Case.builder()
                .reference("ENL-2026-000999")
                .requester(req)
                .category(cat)
                .wilaya(wilaya)
                .commune(commune)
                .rawTitle("Titre privé contenant des données personnelles de M. Secret")
                .rawDescription("Description privée médicale confidentielle")
                .publicTitle("Titre public anonymisé et soigné")
                .publicDescription("Description publique sans aucun nom ni coordonnée")
                .urgency(UrgencyLevel.HIGH)
                .status(CaseStatus.PUBLISHED)
                .amountNeeded(new BigDecimal("25000.00"))
                .build();

        PublicCaseDetailResponse dto = PublicCaseDetailResponse.from(testCase);

        assertThat(dto.getTitle()).isEqualTo("Titre public anonymisé et soigné");
        assertThat(dto.getDescription()).isEqualTo("Description publique sans aucun nom ni coordonnée");
        assertThat(dto.getTitle()).doesNotContain("Secret");
        assertThat(dto.getDescription()).doesNotContain("médicale confidentielle");
    }
}
