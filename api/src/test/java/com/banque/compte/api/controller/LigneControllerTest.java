package com.banque.compte.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Date;
import java.text.ParseException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.banque.compte.api.model.Ligne;
import com.banque.compte.api.service.LigneService;

@WebMvcTest(LigneController.class)
class LigneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LigneService ligneService;

    private Ligne creerLigne() throws ParseException {
        Ligne ligne = new Ligne();
        ligne.setId(1L);
        ligne.setDescription("Courses");
        ligne.setCategorie("Alimentation");
        ligne.setMontant(25.50F);
        ligne.setDateOperation(Date.valueOf("2025-09-21"));
        return ligne;
    }

    @Test
    void testRechercherLignesParNumeroCompte() throws Exception {
        when(ligneService.rechercherLignes(1L))
                .thenReturn(List.of(creerLigne()));

        mockMvc.perform(get("/lignes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categorie").value("Alimentation"));
    }
}
