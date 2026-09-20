package com.banque.compte.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.banque.compte.api.model.Compte;
import com.banque.compte.api.repository.CompteRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompteService")
class CompteServiceTest {

    @Mock
    private CompteRepository compteRepository;

    @Mock
    private LigneService ligneService;

    private CompteService compteService;
    
    @BeforeEach
    public void setUp() {
    	compteService = new CompteService(compteRepository, ligneService);
    }

	@Test
	@Tag("lecture")
	@DisplayName("La recherche de tous les comptes délègue au repository")
	public void rechercherLignes_doitUtiliserCompteRepository_pourRechercherTousLesComptes() {
		// GIVEN
		Compte compte1 = new Compte();
		Compte compte2 = new Compte();
		
		when(compteRepository.findAll()).thenReturn(List.of(compte1, compte2));
		
		// WHEN
		List<Compte> resultat = (List<Compte>) compteService.rechercherComptes();
		
		// THEN	
		verify(compteRepository,times(1)).findAll();
		assertThat(resultat).containsExactly(compte1, compte2);
	}
	
	@Test
	@Tag("lecture")
	@DisplayName("La recherche d'un compte par id délègue au repository")
	public void rechercherCompte_doitUtiliserCompteRepository_pourRechercherUnCompteDonne() {
		// GIVEN
		Long id = 1L;
		
		Compte compte = new Compte();
		
		when (compteRepository.findById(id)).thenReturn(Optional.of(compte));
		
		// WHEN
		Optional<Compte> resultat = compteService.rechercherCompte(id);
		
		// THEN
		verify(compteRepository,times(1)).findById(id);
		assertThat(resultat).contains(compte);
	}
	
	@Test
	@Tag("suppression")
	@DisplayName("La suppression d'un compte doit supprimer toutes les lignes du compte")
	public void supprimerCompte_doitSupprimerLignesDuCompte_pourSupprimerLeCompte() {
		// GIVEN
		Long id = 1L;
		
		doNothing().when(ligneService).supprimerLignesDUnCompte(id);
	    doNothing().when(compteRepository).deleteById(id);
		
		// WHEN
		compteService.supprimerCompte(id);
		
		// THEN
		InOrder inOrder = inOrder(ligneService, compteRepository);
		inOrder.verify(ligneService, times(1)).supprimerLignesDUnCompte(id);
		inOrder.verify(compteRepository, times(1)).deleteById(id);
	}
	
	@Test
	@Tag("ecriture")
	@DisplayName("La sauvegarde d'un compte délègue au repository")
	public void sauvegarderLigne_doitUtiliserLigneRepository_pourSauvegarderUneLigneDonnee() {
		// GIVEN
		Compte compte = new Compte();
		
		when(compteRepository.save(compte)).thenReturn(compte);
		
		// WHEN
		Compte resultat = compteService.sauvegarderCompte(compte);
		
		// THEN
		verify(compteRepository,times(1)).save(compte);
		assertThat(resultat).isEqualTo(compte);
	}
}
