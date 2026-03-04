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

import com.banque.compte.api.model.Ligne;
import com.banque.compte.api.repository.LigneRepository;

@ExtendWith(MockitoExtension.class)
class LigneServiceTest {

    @Mock
    private LigneRepository ligneRepository;

    @InjectMocks
    private LigneService ligneService;

    @Test
    void rechercherLignes_doitRetournerLignesDuCompte() {

        Long numeroCompte = 1L;

        Ligne l1 = new Ligne();
        l1.setNumeroCompte(1L);

        Ligne l2 = new Ligne();
        l2.setNumeroCompte(2L);

        Ligne l3 = new Ligne();
        l3.setNumeroCompte(1L);

        when(ligneRepository.findAll())
                .thenReturn(List.of(l1, l2, l3));

        Iterable<Ligne> result =
                ligneService.rechercherLignes(numeroCompte);

        List<Ligne> list = (List<Ligne>) result;

        assertEquals(2, list.size());
        assertTrue(list.contains(l1));
        assertTrue(list.contains(l3));
    }

    @Test
    void rechercherLigne_doitRetournerLigne() {

        Long id = 1L;
        Ligne ligne = new Ligne();

        when(ligneRepository.findById(id))
                .thenReturn(Optional.of(ligne));

        Optional<Ligne> result =
                ligneService.rechercherLigne(id);

        assertTrue(result.isPresent());
        assertEquals(ligne, result.get());
    }

    @Test
    void rechercherLigne_neDoitRienRetourner() {

        Long id = 10L;

        when(ligneRepository.findById(id))
                .thenReturn(Optional.empty());

        Optional<Ligne> result =
                ligneService.rechercherLigne(id);

        assertFalse(result.isPresent());
    }

    @Test
    void supprimerLigne_doitSupprimerLigne() {

        Long id = 1L;

        doNothing().when(ligneRepository)
                .deleteById(id);

        ligneService.supprimerLigne(id);

        verify(ligneRepository).deleteById(id);
    }

    @Test
    void supprimerLignesDUnCompte_doitSupprimerBonnesLignes() {

        Long numeroCompte = 1L;

        Ligne l1 = new Ligne();
        l1.setId(1L);
        l1.setNumeroCompte(1L);

        Ligne l2 = new Ligne();
        l2.setId(2L);
        l2.setNumeroCompte(2L);

        Ligne l3 = new Ligne();
        l3.setId(3L);
        l3.setNumeroCompte(1L);

        when(ligneRepository.findAll())
                .thenReturn(List.of(l1, l2, l3));

        ligneService.supprimerLignesDUnCompte(numeroCompte);

        verify(ligneRepository).deleteById(1L);
        verify(ligneRepository).deleteById(3L);
        verify(ligneRepository, never())
                .deleteById(2L);
    }

    @Test
    void sauvegarderLigne_doitSauvegarderEtRetournerLigne() {

        Ligne ligne = new Ligne();

        when(ligneRepository.save(ligne))
                .thenReturn(ligne);

        Ligne result = ligneService.sauvegarderLigne(ligne);

        assertNotNull(result);
        assertEquals(ligne, result);

        verify(ligneRepository).save(ligne);
    }
}
