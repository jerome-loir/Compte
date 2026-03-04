package com.banque.compte.webapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.banque.compte.webapp.model.Compte;
import com.banque.compte.webapp.repository.CompteProxy;

class CompteServiceTest {

    @Mock
    private CompteProxy compteProxy;

    @InjectMocks
    private CompteService compteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Compte creerCompte() {
        Compte compte = new Compte();
        compte.setNom("dupont");
        compte.setPrenom("jean");
        compte.setMail("jean.dupont@test.fr");
        return compte;
    }

    @Test
    void testSauvegarderCompte_Creation() {
        Compte compte = creerCompte();

        when(compteProxy.creerCompte(any(Compte.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Compte result = compteService.sauvegarderCompte(compte);

        assertThat(result.getNom()).isEqualTo("DUPONT");
        assertThat(result.getPrenom()).isEqualTo("Jean");

        verify(compteProxy, times(1)).creerCompte(any(Compte.class));
        verify(compteProxy, never()).mettreAJourCompte(any());
    }

    @Test
    void testSauvegarderCompte_MiseAJour() {
        Compte compte = creerCompte();
        compte.setNumeroCompte(1001L);

        when(compteProxy.mettreAJourCompte(any(Compte.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Compte result = compteService.sauvegarderCompte(compte);

        assertThat(result.getNom()).isEqualTo("DUPONT");
        assertThat(result.getPrenom()).isEqualTo("Jean");

        verify(compteProxy, times(1)).mettreAJourCompte(any(Compte.class));
        verify(compteProxy, never()).creerCompte(any());
    }

    @Test
    void testRechercherComptes() {
        compteService.rechercherComptes();
        verify(compteProxy, times(1)).rechercherComptes();
    }

    @Test
    void testRechercherCompte() {
        compteService.rechercherCompte(1);
        verify(compteProxy, times(1)).rechercherCompte(1);
    }

    @Test
    void testSupprimerCompte() {
        compteService.supprimerCompte(1);
        verify(compteProxy, times(1)).supprimerCompte(1);
    }
}
