package com.banque.compte.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.banque.compte.api.model.Compte;
import com.banque.compte.api.service.CompteService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CompteController.class)
class CompteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompteService compteService;

    @Autowired
    private ObjectMapper objectMapper;

    private Compte creerCompte() {
        Compte compte = new Compte();
        compte.setNom("Dupont");
        compte.setPrenom("Jean");
        compte.setMail("jean.dupont@test.fr");
        return compte;
    }

    @Test
    void testRechercherComptes() throws Exception {
        when(compteService.rechercherComptes())
                .thenReturn(List.of(creerCompte(), creerCompte()));

        mockMvc.perform(get("/comptes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Dupont"));
    }

    @Test
    void testRechercherCompte_Existe() throws Exception {
        when(compteService.rechercherCompte(1L))
                .thenReturn(Optional.of(creerCompte()));

        mockMvc.perform(get("/compte/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prenom").value("Jean"));
    }

    @Test
    void testRechercherCompte_Inexistant() throws Exception {
        when(compteService.rechercherCompte(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/compte/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void testCreerCompte() throws Exception {
        Compte compte = creerCompte();

        when(compteService.sauvegarderCompte(any(Compte.class)))
                .thenReturn(compte);

        mockMvc.perform(post("/compte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compte)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mail").value("jean.dupont@test.fr"));
    }

    @Test
    void testSupprimerCompte() throws Exception {
        doNothing().when(compteService).supprimerCompte(1L);

        mockMvc.perform(delete("/compte/1"))
                .andExpect(status().isOk());

        verify(compteService, times(1)).supprimerCompte(1L);
    }

    @Test
    void testMettreAJourCompte() throws Exception {
        Compte existant = creerCompte();

        Compte modif = new Compte();
        modif.setMail("nouveau@mail.fr");

        when(compteService.rechercherCompte(1L))
                .thenReturn(Optional.of(existant));

        when(compteService.sauvegarderCompte(any(Compte.class)))
                .thenReturn(existant);

        mockMvc.perform(put("/compte/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modif)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mail").value("nouveau@mail.fr"));
    }

    @Test
    void testMettreAJourCompte_Inexistant() throws Exception {
        when(compteService.rechercherCompte(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/compte/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creerCompte())))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
}
