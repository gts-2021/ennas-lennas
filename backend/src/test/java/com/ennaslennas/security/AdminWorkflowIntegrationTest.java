package com.ennaslennas.security;

import com.ennaslennas.cases.domain.Case;
import com.ennaslennas.cases.domain.CaseStatus;
import com.ennaslennas.cases.domain.UrgencyLevel;
import com.ennaslennas.cases.dto.request.PublishCaseRequest;
import com.ennaslennas.cases.repository.CaseRepository;
import com.ennaslennas.categories.domain.Category;
import com.ennaslennas.categories.repository.CategoryRepository;
import com.ennaslennas.locations.domain.Commune;
import com.ennaslennas.locations.domain.Wilaya;
import com.ennaslennas.locations.repository.CommuneRepository;
import com.ennaslennas.locations.repository.WilayaRepository;
import com.ennaslennas.requesters.domain.Requester;
import com.ennaslennas.requesters.repository.RequesterRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private RequesterRepository requesterRepository;

    @Autowired
    private WilayaRepository wilayaRepository;

    @Autowired
    private CommuneRepository communeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private String getAdminToken() throws Exception {
        String loginJson = """
                {
                    "email": "admin@ennaslennas.org",
                    "password": "Admin123!"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.path("data").path("token").asText();
    }

    private Case createTestCase() {
        Wilaya wilaya = wilayaRepository.findAll().get(0);
        Commune commune = communeRepository.findByWilayaIdOrderByNameFrAsc(wilaya.getId()).get(0);
        Category category = categoryRepository.findAll().get(0);

        Requester requester = requesterRepository.save(Requester.builder()
                .firstName("Fatima")
                .lastName("Mansouri")
                .phone("0661998877")
                .wilaya(wilaya)
                .commune(commune)
                .build());

        return caseRepository.save(Case.builder()
                .reference("ENL-TEST-00001")
                .requester(requester)
                .category(category)
                .wilaya(wilaya)
                .commune(commune)
                .rawTitle("Demande de soutien scolaire")
                .rawDescription("Besoin d'équipement informatique pour études supérieures.")
                .urgency(UrgencyLevel.MEDIUM)
                .status(CaseStatus.SUBMITTED)
                .amountNeeded(new BigDecimal("35000.00"))
                .build());
    }

    @Test
    @DisplayName("L'accès sans token aux endpoints admin doit être rejeté (401/403)")
    void shouldDenyUnauthorizedAdminAccess() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Cycle de workflow complet contrôlé par l'administrateur")
    void shouldExecuteFullWorkflowCycle() throws Exception {
        String token = getAdminToken();
        Case testCase = createTestCase();
        Long caseId = testCase.getId();

        // 1. Dashboard stats accessible
        mockMvc.perform(get("/api/v1/admin/dashboard/stats")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 2. Prise en charge (SUBMITTED -> UNDER_REVIEW)
        mockMvc.perform(post("/api/v1/admin/cases/" + caseId + "/review")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("UNDER_REVIEW"));

        // 3. Demande de complément (UNDER_REVIEW -> NEED_MORE_INFO)
        mockMvc.perform(post("/api/v1/admin/cases/" + caseId + "/request-info")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\": \"Veuillez joindre le certificat de scolarité.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("NEED_MORE_INFO"));

        // 4. Reprise en revue (NEED_MORE_INFO -> UNDER_REVIEW)
        mockMvc.perform(post("/api/v1/admin/cases/" + caseId + "/review")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("UNDER_REVIEW"));

        // 5. Approbation (UNDER_REVIEW -> APPROVED)
        mockMvc.perform(post("/api/v1/admin/cases/" + caseId + "/approve")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("APPROVED"));

        // 6. Publication publique (APPROVED -> PUBLISHED)
        PublishCaseRequest pubReq = PublishCaseRequest.builder()
                .publicTitle("Soutien pour matériel d'études d'une étudiante méritante")
                .publicDescription("Étudiante en cycle d'ingénierie ayant besoin d'un ordinateur portable.")
                .publicImageUrl("https://images.unsplash.com/photo-1516321318423-f06f85e504b3")
                .build();

        mockMvc.perform(post("/api/v1/admin/cases/" + caseId + "/publish")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pubReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PUBLISHED"))
                .andExpect(jsonPath("$.data.publicTitle").value(pubReq.getPublicTitle()))
                .andExpect(jsonPath("$.data.publishedAt").isNotEmpty());

        // 7. Mise en relation (PUBLISHED -> IN_PROGRESS)
        mockMvc.perform(post("/api/v1/admin/cases/" + caseId + "/in-progress")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));

        // 8. Besoin satisfait (IN_PROGRESS -> COMPLETED)
        mockMvc.perform(post("/api/v1/admin/cases/" + caseId + "/completed")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));

        // 9. Clôture du dossier (COMPLETED -> CLOSED)
        mockMvc.perform(post("/api/v1/admin/cases/" + caseId + "/close")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\": \"Ordinateur remis en main propre par le donateur.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CLOSED"))
                .andExpect(jsonPath("$.data.closedAt").isNotEmpty());

        // Verify final DB state
        Case finalCase = caseRepository.findById(caseId).orElseThrow();
        assertThat(finalCase.getStatus()).isEqualTo(CaseStatus.CLOSED);
        assertThat(finalCase.getClosedAt()).isNotNull();
    }

    @Test
    @DisplayName("Une transition illégale doit être bloquée avec une erreur 400")
    void shouldBlockIllegalTransition() throws Exception {
        String token = getAdminToken();
        Case testCase = createTestCase(); // Status is SUBMITTED

        // Try to approve directly from SUBMITTED (must pass via UNDER_REVIEW)
        mockMvc.perform(post("/api/v1/admin/cases/" + testCase.getId() + "/approve")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("UNDER_REVIEW")));
    }
}
