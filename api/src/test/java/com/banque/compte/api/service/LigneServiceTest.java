package com.banque.compte.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.banque.compte.api.model.Ligne;
import com.banque.compte.api.repository.LigneRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("LigneService")
class LigneServiceTest {

	@Mock
	LigneRepository ligneRepository;

	LigneService ligneService;

	@BeforeEach
	public void setUp() {
		ligneService = new LigneService(ligneRepository);
	}

	@Test
	@Tag("lecture")
	@DisplayName("La recherche de toutes les lignes filtre par numéro de compte")
	public void rechercherLignes_doitUtiliserLigneRepository_pourRechercherToutesLesLignes() {
		// GIVEN
		Long numeroCompte = 1L;

		Ligne l1 = new Ligne();
		l1.setNumeroCompte(1L);

		Ligne l2 = new Ligne();
		l2.setNumeroCompte(2L);

		Ligne l3 = new Ligne();
		l3.setNumeroCompte(1L);

		when(ligneRepository.findAll()).thenReturn(List.of(l1, l2, l3));

		// WHEN
		List<Ligne> resultat = (List<Ligne>) ligneService.rechercherLignes(numeroCompte);

		// THEN
		verify(ligneRepository, times(1)).findAll();
		assertThat(resultat).containsExactly(l1, l3);
	}

	@Test
	@Tag("lecture")
	@DisplayName("La recherche d'une ligne par id délègue au repository")
	public void rechercherLigne_doitUtiliserLigneRepository_pourRechercherUneLigneDonnee() {
		// GIVEN
		Long id = 1L;

		Ligne ligne = new Ligne();

		when(ligneRepository.findById(id)).thenReturn(Optional.of(ligne));

		// WHEN
		Optional<Ligne> resultat = ligneService.rechercherLigne(id);

		// THEN
		verify(ligneRepository, times(1)).findById(id);
		assertThat(resultat.get()).isEqualTo(ligne);
	}

	@Nested
	@Tag("suppression")
	@DisplayName("Suppression")
	class SuppressionTest {

		@Test
		@DisplayName("La suppression d'une ligne par id délègue au repository")
		public void supprimerLigne_doitUtiliserLigneRepository_pourSupprimerUneLigneDonnee() {
			// GIVEN
			Long id = 1L;

			Ligne ligne = new Ligne();

			doNothing().when(ligneRepository).deleteById(id);

			// WHEN
			ligneService.supprimerLigne(id);

			// THEN
			verify(ligneRepository, times(1)).deleteById(id);
		}

		@Test
		@DisplayName("La suppression des lignes d'un compte ne supprime que les lignes de ce compte")
		public void supprimerLignesDunCompte_doitUtiliserLigneRepository_pourSupprimerToutesLesLignesDUnCompte() {
			// GIVEN
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

			when(ligneRepository.findAll()).thenReturn(List.of(l1, l2, l3));
			doNothing().when(ligneRepository).deleteById(any(Long.class));

			// WHEN
			ligneService.supprimerLignesDUnCompte(1L);

			// THEN
			verify(ligneRepository, times(1)).deleteById(1L);
			verify(ligneRepository, times(1)).deleteById(3L);
			verify(ligneRepository, never()).deleteById(2L);
		}
	}

	@Test
	@Tag("ecriture")
	@DisplayName("La sauvegarde d'une ligne délègue au repository")
	public void sauvegarderLigne_doitUtiliserLigneRepository_pourSauvegarderUneLigneDonnee() {
		// GIVEN
		Ligne ligne = new Ligne();

		when(ligneRepository.save(ligne)).thenReturn(ligne);

		// WHEN
		Ligne resultat = ligneService.sauvegarderLigne(ligne);

		// THEN
		verify(ligneRepository, times(1)).save(ligne);
		assertThat(resultat).isEqualTo(ligne);
	}

}
