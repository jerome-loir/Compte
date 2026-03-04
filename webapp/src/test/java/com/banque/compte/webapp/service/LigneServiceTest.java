package com.banque.compte.webapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.banque.compte.webapp.model.Ligne;
import com.banque.compte.webapp.repository.LigneProxy;

class LigneServiceTest {

    @Mock
    private LigneProxy ligneProxy;

    @InjectMocks
    private LigneService ligneService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Ligne creerLigne() {
        Ligne ligne = new Ligne();
        ligne.setDescription("Achat courses");
        ligne.setCategorie("Alimentation");
        ligne.setMontant(30f);
        return ligne;
    }

    @Test
    void testSauvegarderLigne_Creation() {
        Ligne ligne = creerLigne();

        when(ligneProxy.creerLigne(any(Ligne.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Ligne result = ligneService.sauvegarderLigne(ligne);

        assertThat(result).isNotNull();
        verify(ligneProxy, times(1)).creerLigne(any());
        verify(ligneProxy, never()).mettreAJourLigne(any());
    }

    @Test
    void testSauvegarderLigne_MiseAJour() {
        Ligne ligne = creerLigne();
        ligne.setId(1L);

        when(ligneProxy.mettreAJourLigne(any(Ligne.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Ligne result = ligneService.sauvegarderLigne(ligne);

        assertThat(result).isNotNull();
        verify(ligneProxy, times(1)).mettreAJourLigne(any());
        verify(ligneProxy, never()).creerLigne(any());
    }

    @Test
    void testRechercherLignes() {
        ligneService.rechercherLignes(1001);
        verify(ligneProxy, times(1)).rechercherLignes(1001);
    }

    @Test
    void testRechercherLigne() {
        ligneService.rechercherLigne(1);
        verify(ligneProxy, times(1)).rechercherLigne(1);
    }

    @Test
    void testSupprimerLigne() {
        ligneService.supprimerLigne(1);
        verify(ligneProxy, times(1)).supprimerLigne(1);
    }
}
