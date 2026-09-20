package com.banque.compte.webapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.banque.compte.webapp.model.Compte;
import com.banque.compte.webapp.repository.CompteProxy;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompteService")
class CompteServiceTest {

	@Mock
	private CompteProxy compteProxy;

	private CompteService compteService;

	@BeforeEach
	public void setUp() {
		compteService = new CompteService(compteProxy);
	}

	@Test
	@Tag("lecture")
	@DisplayName("La recherche de tous les comptes délègue au proxy")
	public void rechercherComptes_doitUtiliserCompteProxy_pourRechercherTousLesComptes() {
		// GIVEN
		Compte compte1 = new Compte();
		Compte compte2 = new Compte();

		when(compteProxy.rechercherComptes()).thenReturn(List.of(compte1, compte2));

		// WHEN
		List<Compte> resultat = (List<Compte>) compteService.rechercherComptes();

		// THEN
		verify(compteProxy, times(1)).rechercherComptes();
		assertThat(resultat).containsExactly(compte1, compte2);
	}

	@Test
	@Tag("lecture")
	@DisplayName("La recherche d'un compte par id délègue au proxy")
	public void rechercherCompte_doitUtiliserCompteProxy_pourRechercherUnCompteDonne() {
		// GIVEN
		int id = 1;
		Compte compte = new Compte();

		when(compteProxy.rechercherCompte(id)).thenReturn(compte);

		// WHEN
		Compte resultat = compteService.rechercherCompte(id);

		// THEN
		verify(compteProxy, times(1)).rechercherCompte(id);
		assertThat(resultat).isEqualTo(compte);
	}

	@Test
	@Tag("suppression")
	@DisplayName("La suppression d'un compte délègue au proxy")
	public void supprimerCompte_doitUtiliserCompteProxy_pourSupprimerLeCompte() {
		// GIVEN
		int id = 1;

		doNothing().when(compteProxy).supprimerCompte(id);

		// WHEN
		compteService.supprimerCompte(id);

		// THEN
		verify(compteProxy, times(1)).supprimerCompte(id);
	}

	@Nested
	@Tag("ecriture")
	class SauvegardeCompteTest {

		@Test
		@DisplayName("Le nom est mis en majuscules à la sauvegarde")
		void sauvegarderCompte_doitMettreLeNomEnMajuscules() {
			// GIVEN
			Compte compte = new Compte();
			compte.setNom("dupont");
			compte.setPrenom("jean");

			when(compteProxy.creerCompte(any(Compte.class))).thenReturn(compte);

			// WHEN
			compteService.sauvegarderCompte(compte);

			// THEN
			assertThat(compte.getNom()).isEqualTo("DUPONT");
		}

		@Test
		@DisplayName("La première lettre du prénom est capitalisée")
		void sauvegarderCompte_doitCapitaliserLePrenom() {
			// GIVEN
			Compte compte = new Compte();
			compte.setNom("dupont");
			compte.setPrenom("jean");

			when(compteProxy.creerCompte(any(Compte.class))).thenReturn(compte);

			// WHEN
			compteService.sauvegarderCompte(compte);

			// THEN
			assertThat(compte.getPrenom()).isEqualTo("Jean");
		}

		@Test
		@DisplayName("Un compte sans numéro est créé")
		void sauvegarderCompte_doitAppelerCreerCompte_quandNumeroCompteEstNull() {
			// GIVEN
			Compte compte = new Compte();
			compte.setNom("dupont");
			compte.setPrenom("jean");

			when(compteProxy.creerCompte(any(Compte.class))).thenReturn(compte);

			// WHEN
			compteService.sauvegarderCompte(compte);

			// THEN
			verify(compteProxy, times(1)).creerCompte(compte);
			verify(compteProxy, never()).mettreAJourCompte(any(Compte.class));
		}

		@Test
		@DisplayName("Un compte avec un numéro est mis à jour")
		void sauvegarderCompte_doitAppelerMettreAJourCompte_quandNumeroCompteExiste() {
			// GIVEN
			Compte compte = new Compte();
			compte.setNumeroCompte(1L);
			compte.setNom("dupont");
			compte.setPrenom("jean");

			when(compteProxy.mettreAJourCompte(any(Compte.class))).thenReturn(compte);

			// WHEN
			compteService.sauvegarderCompte(compte);

			// THEN
			verify(compteProxy, times(1)).mettreAJourCompte(compte);
			verify(compteProxy, never()).creerCompte(any(Compte.class));
		}
	}
}
