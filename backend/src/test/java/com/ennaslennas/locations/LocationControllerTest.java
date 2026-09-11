package com.ennaslennas.locations;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Devrait retourner la liste des 58 wilayas algériennes")
    void shouldReturnAll58Wilayas() throws Exception {
        mockMvc.perform(get("/api/v1/locations/wilayas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(58)))
                .andExpect(jsonPath("$.data[0].code").value("01"))
                .andExpect(jsonPath("$.data[0].nameFr").value("Adrar"))
                .andExpect(jsonPath("$.data[15].code").value("16"))
                .andExpect(jsonPath("$.data[15].nameFr").value("Alger"));
    }

    @Test
    @DisplayName("Devrait retourner les communes d'une wilaya donnée")
    void shouldReturnCommunesForWilaya() throws Exception {
        mockMvc.perform(get("/api/v1/locations/wilayas/16/communes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", not(empty())))
                .andExpect(jsonPath("$.data[?(@.nameFr == 'Alger Centre')]").exists());
    }
}
