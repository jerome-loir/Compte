package com.banque.compte.webapp.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.banque.compte.webapp.model.Ligne;
import com.banque.compte.webapp.service.LigneService;

@WebMvcTest(LigneController.class)
class LigneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LigneService ligneService;

    private Ligne creerLigne() {
        Ligne l = new Ligne();
        l.setNumeroCompte(1001L);
        return l;
    }

    @Test
    void testSupprimerLigne() throws Exception {
        Ligne ligne = creerLigne();

        when(ligneService.rechercherLigne(1))
                .thenReturn(ligne);

        mockMvc.perform(get("/supprimerLigne/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/afficherCompte/1001"));

        verify(ligneService).supprimerLigne(1);
    }

    @Test
    void testCreerLigne() throws Exception {
        mockMvc.perform(get("/creerLigne/1001"))
                .andExpect(status().isOk())
                .andExpect(view().name("formNewLigne"))
                .andExpect(model().attributeExists("ligne"));
    }

    @Test
    void testMettreAJourLigne() throws Exception {
        when(ligneService.rechercherLigne(1))
                .thenReturn(creerLigne());

        mockMvc.perform(get("/mettreAJourLigne/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("formUpdateLigne"))
                .andExpect(model().attributeExists("ligne"));
    }

    @Test
    void testSauvegarderLigne() throws Exception {
        mockMvc.perform(post("/sauvegarderLigne")
                        .param("numeroCompte", "1001")
                        .param("description", "Courses"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/afficherCompte/1001"));

        verify(ligneService).sauvegarderLigne(any());
    }
}
