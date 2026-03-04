package com.banque.compte.webapp.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.banque.compte.webapp.model.Compte;
import com.banque.compte.webapp.model.Ligne;
import com.banque.compte.webapp.service.CompteService;
import com.banque.compte.webapp.service.LigneService;

@WebMvcTest(CompteController.class)
class CompteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompteService compteService;

    @MockitoBean
    private LigneService ligneService;

    private Compte creerCompte() {
        Compte c = new Compte();
        c.setNom("DUPONT");
        c.setPrenom("Jean");
        return c;
    }

    private Ligne createLigne(float montant) {
        Ligne l = new Ligne();
        l.setMontant(montant);
        return l;
    }

    @Test
    void testHome() throws Exception {
        when(compteService.rechercherComptes())
                .thenReturn(List.of(creerCompte(), creerCompte()));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("comptes"));
    }

    @Test
    void testAfficherCompte() throws Exception {
        when(compteService.rechercherCompte(1))
                .thenReturn(creerCompte());

        when(ligneService.rechercherLignes(1))
                .thenReturn(List.of(createLigne(100f), createLigne(-20f)));

        mockMvc.perform(get("/afficherCompte/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("compte"))
                .andExpect(model().attribute("nom", "Jean DUPONT"))
                .andExpect(model().attribute("numeroCompte", 1))
                .andExpect(model().attribute("solde", 80f));
    }

    @Test
    void testSupprimerCompte() throws Exception {
        mockMvc.perform(get("/supprimerCompte/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(compteService).supprimerCompte(1);
    }

    @Test
    void testCreerCompte() throws Exception {
        mockMvc.perform(get("/creerCompte"))
                .andExpect(status().isOk())
                .andExpect(view().name("formNewCompte"))
                .andExpect(model().attributeExists("compte"));
    }

    @Test
    void testMettreAJourCompte() throws Exception {
        when(compteService.rechercherCompte(1))
                .thenReturn(creerCompte());

        mockMvc.perform(get("/mettreAJourCompte/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("formUpdateCompte"))
                .andExpect(model().attributeExists("compte"));
    }

    @Test
    void testSauvegarderCompte() throws Exception {
        mockMvc.perform(post("/sauvegarderCompte")
                        .param("nom", "Dupont")
                        .param("prenom", "Jean"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(compteService).sauvegarderCompte(any());
    }
}
