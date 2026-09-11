package com.ennaslennas.cases;

import com.ennaslennas.cases.domain.Case;
import com.ennaslennas.cases.domain.CaseStatus;
import com.ennaslennas.cases.repository.CaseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HelpRequestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CaseRepository caseRepository;

    @Test
    @DisplayName("Devrait enregistrer avec succès une nouvelle demande d'aide avec un justificatif PDF")
    void shouldSubmitHelpRequestSuccessfully() throws Exception {
        MockMultipartFile fakePdf = new MockMultipartFile(
                "documents",
                "ordonnance.pdf",
                "application/pdf",
                "%PDF-1.4 test prescription content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/help-requests")
                        .file(fakePdf)
                        .param("firstName", "Amine")
                        .param("lastName", "Benali")
                        .param("phone", "0550123456")
                        .param("email", "amine.benali@example.dz")
                        .param("wilayaId", "16")
                        .param("communeId", "1")
                        .param("categoryId", "1")
                        .param("title", "Aide pour achat de médicament essentiel")
                        .param("description", "Besoin urgent de traitement pour maladie chronique non remboursée.")
                        .param("urgency", "HIGH")
                        .param("amountNeeded", "45000.00"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reference", matchesPattern("^ENL-\\d{4}-\\d{6}$")));

        // Verify entity in database
        Case savedCase = caseRepository.findAll().stream()
                .filter(c -> c.getRawTitle().equals("Aide pour achat de médicament essentiel"))
                .findFirst()
                .orElse(null);

        assertThat(savedCase).isNotNull();
        assertThat(savedCase.getStatus()).isEqualTo(CaseStatus.SUBMITTED);
        assertThat(savedCase.getRequester().getPhone()).isEqualTo("0550123456");
        assertThat(savedCase.getDocuments()).hasSize(1);
        assertThat(savedCase.getDocuments().get(0).getOriginalFilename()).isEqualTo("ordonnance.pdf");
    }

    @Test
    @DisplayName("Devrait rejeter une demande avec un numéro de téléphone invalide")
    void shouldRejectInvalidPhone() throws Exception {
        mockMvc.perform(multipart("/api/v1/help-requests")
                        .param("firstName", "Karim")
                        .param("lastName", "Ziani")
                        .param("phone", "123456") // Invalide
                        .param("wilayaId", "16")
                        .param("communeId", "1")
                        .param("categoryId", "1")
                        .param("title", "Demande test")
                        .param("description", "Description suffisante pour passer le minimum requis."))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.phone").exists());
    }
}
