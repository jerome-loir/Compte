package com.banque.compte.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.banque.compte.api.model.Compte;
import com.banque.compte.api.repository.CompteRepository;

@ExtendWith(MockitoExtension.class)
class CompteServiceTest {

    @Mock
    private CompteRepository compteRepository;

    @Mock
    private LigneService ligneService;

    @InjectMocks
    private CompteService compteService;

    @Test
    void rechercherComptes_doitRetournerTousLesComptes() {

        Compte c1 = new Compte();
        Compte c2 = new Compte();

        when(compteRepository.findAll())
                .thenReturn(List.of(c1, c2));

        Iterable<Compte> result = compteService.rechercherComptes();

        assertNotNull(result);
        assertEquals(2, ((List<Compte>) result).size());

        verify(compteRepository).findAll();
    }

    @Test
    void rechercherCompte_doitRetournerLeCompteAttendu() {

        Long id = 1L;
        Compte compte = new Compte();

        when(compteRepository.findById(id))
                .thenReturn(Optional.of(compte));

        Optional<Compte> result = compteService.rechercherCompte(id);

        assertTrue(result.isPresent());
        assertEquals(compte, result.get());

        verify(compteRepository).findById(id);
    }

    @Test
    void rechercherCompte_neDoitRienRetournerSiAbsent() {

        Long id = 99L;

        when(compteRepository.findById(id))
                .thenReturn(Optional.empty());

        Optional<Compte> result = compteService.rechercherCompte(id);

        assertFalse(result.isPresent());
    }

    @Test
    void supprimerCompte_doitSupprimerLesLignesEtLeCompte() {

        Long id = 1L;

        doNothing().when(ligneService)
                .supprimerLignesDUnCompte(id);

        doNothing().when(compteRepository)
                .deleteById(id);

        compteService.supprimerCompte(id);

        verify(ligneService).supprimerLignesDUnCompte(id);
        verify(compteRepository).deleteById(id);
    }

    @Test
    void sauvegarderCompte_doitSauvegarderEtRetournerCompte() {

        Compte compte = new Compte();

        when(compteRepository.save(compte))
                .thenReturn(compte);

        Compte result = compteService.sauvegarderCompte(compte);

        assertNotNull(result);
        assertEquals(compte, result);

        verify(compteRepository).save(compte);
    }
}
