package com.banque.compte.webapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.banque.compte.webapp.model.Ligne;
import com.banque.compte.webapp.repository.LigneProxy;

@ExtendWith(MockitoExtension.class)
@DisplayName("LigneService")
class LigneServiceTest {

	@Mock
	private LigneProxy ligneProxy;

	private LigneService ligneService;

	@BeforeEach
	public void setUp() {
		ligneService = new LigneService(ligneProxy);
	}

	@Test
	@Tag("lecture")
	@DisplayName("La recherche de toutes les lignes délègue au proxy")
	public void rechercherLignes_doitUtiliserLigneProxy_pourRechercherToutesLesLignes() {
		// GIVEN
		int numeroCompte = 1;
		Ligne ligne1 = new Ligne();
		Ligne ligne2 = new Ligne();

		when(ligneProxy.rechercherLignes(numeroCompte)).thenReturn(List.of(ligne1, ligne2));

		// WHEN
		List<Ligne> resultat = (List<Ligne>) ligneService.rechercherLignes(numeroCompte);

		// THEN
		verify(ligneProxy, times(1)).rechercherLignes(numeroCompte);
		assertThat(resultat).containsExactly(ligne1, ligne2);
	}

	@Test
	@Tag("lecture")
	@DisplayName("La recherche d'une ligne par id délègue au proxy")
	public void rechercherLigne_doitUtiliserLigneProxy_pourRechercherUneLigneDonnee() {
		// GIVEN
		int id = 1;
		Ligne ligne = new Ligne();

		when(ligneProxy.rechercherLigne(id)).thenReturn(ligne);

		// WHEN
		Ligne resultat = ligneService.rechercherLigne(id);

		// THEN
		verify(ligneProxy, times(1)).rechercherLigne(id);
		assertThat(resultat).isEqualTo(ligne);
	}

	@Test
	@Tag("suppression")
	@DisplayName("La suppression d'une ligne délègue au proxy")
	public void supprimerLigne_doitUtiliserLigneProxy_pourSupprimerLaLigne() {
		// GIVEN
		int id = 1;

		doNothing().when(ligneProxy).supprimerLigne(id);

		// WHEN
		ligneService.supprimerLigne(id);

		// THEN
		verify(ligneProxy, times(1)).supprimerLigne(id);
	}

	@Nested
	@Tag("ecriture")
	class SauvegardeLigneTest {

		@Test
		@DisplayName("Une ligne sans numéro est créée")
		void sauvegarderLigne_doitAppelerCreerLigne_quandIdEstNull() {
			// GIVEN
			Ligne ligne = new Ligne();
			ligne.setNumeroCompte(1L);
			ligne.setDescription("Alimentation");
			ligne.setCategorie("Courses");
			ligne.setMontant(-25.50F);
			ligne.setDateOperation(Date.valueOf("2025-09-21"));

			when(ligneProxy.creerLigne(any(Ligne.class))).thenReturn(ligne);

			// WHEN
			ligneService.sauvegarderLigne(ligne);

			// THEN
			verify(ligneProxy, times(1)).creerLigne(ligne);
			verify(ligneProxy, never()).mettreAJourLigne(any(Ligne.class));
		}

		@Test
		@DisplayName("Une ligne avec un id est mise à jour")
		void sauvegarderLigne_doitAppelerMettreAJourLigne_quandIdExiste() {
			// GIVEN
			Ligne ligne = new Ligne();
			ligne.setId(1L);
			ligne.setNumeroCompte(1L);
			ligne.setDescription("Alimentation");
			ligne.setCategorie("Courses");
			ligne.setMontant(-25.50F);
			ligne.setDateOperation(Date.valueOf("2025-09-21"));

			when(ligneProxy.mettreAJourLigne(any(Ligne.class))).thenReturn(ligne);

			// WHEN
			ligneService.sauvegarderLigne(ligne);

			// THEN
			verify(ligneProxy, times(1)).mettreAJourLigne(ligne);
			verify(ligneProxy, never()).creerLigne(any(Ligne.class));
		}
	}
}
