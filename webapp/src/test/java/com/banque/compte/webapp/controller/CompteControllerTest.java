package com.banque.compte.webapp.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.HttpClientErrorException;

import com.banque.compte.webapp.model.Compte;
import com.banque.compte.webapp.model.Ligne;
import com.banque.compte.webapp.service.CompteService;
import com.banque.compte.webapp.service.LigneService;

@WebMvcTest(CompteController.class)
@DisplayName("CompteController")
class CompteControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CompteService compteService;

	@MockitoBean
	private LigneService ligneService;

	private Compte creerCompte() {
		Compte c = new Compte();
		c.setNom("DUPONT");
		c.setPrenom("Jean");
		return c;
	}

	@Nested
	@Tag("lecture")
	@DisplayName("Page d'accueil")
	class HomeTest {

		@Test
		@DisplayName("La page d'accueil doit afficher les comptes quand ils existent")
		public void home_doitAfficherLesComptesExistants() throws Exception {
			// GIVEN
			Compte compte = creerCompte();

			when(compteService.rechercherComptes()).thenReturn(List.of(compte));

			// WHEN
			ResultActions resultat = mockMvc.perform(get("/"));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(view().name("home"))
					.andExpect(model().attribute("comptes", hasSize(1)));
			verify(compteService, times(1)).rechercherComptes();
		}

		@Test
		@DisplayName("La page d'accueil ne doit rien afficher quand il n'y a pas de compte")
		public void home_neDoitRienAfficherQuandPasDeComptesExistant() throws Exception {
			// GIVEN
			when(compteService.rechercherComptes()).thenReturn(List.of());

			// WHEN
			ResultActions resultat = mockMvc.perform(get("/"));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(view().name("home"))
					.andExpect(model().attribute("comptes", hasSize(0)));
			verify(compteService, times(1)).rechercherComptes();
		}
	}

	@Nested
	@Tag("lecture")
	@DisplayName("Affichage d'un compte")
	class AfficherCompteTest {

		@Test
		@DisplayName("L'affichage d'un compte avec des lignes doit afficher le compte et les lignes")
		public void afficherCompte_doitAfficherLeCompteEtLesLignesDuCompteSiLignesPresentes() throws Exception {
			// GIVEN
			int id = 1;
			Compte compte = creerCompte();
			Ligne ligne = new Ligne();
			ligne.setMontant(-72.45F);
			;

			when(ligneService.rechercherLignes(id)).thenReturn(List.of(ligne));
			when(compteService.rechercherCompte(id)).thenReturn(compte);

			// WHEN
			ResultActions resultat = mockMvc.perform(get("/afficherCompte/{id}", id));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(view().name("compte"))
					.andExpect(model().attribute("nom", "Jean DUPONT"))
					.andExpect(model().attribute("numeroCompte", 1))
					.andExpect(model().attribute("lignes", hasSize(1)))
					.andExpect(model().attribute("solde", -72.45F));
			verify(ligneService, times(1)).rechercherLignes(id);
			verify(compteService, times(1)).rechercherCompte(id);
		}

		@Test
		@DisplayName("L'affichage d'un compte sans ligne doit afficher le compte")
		public void afficherCompte_doitAfficherLeCompteSiLignesAbsentes() throws Exception {
			// GIVEN
			int id = 1;
			Compte compte = creerCompte();

			when(ligneService.rechercherLignes(id)).thenReturn(List.of());
			when(compteService.rechercherCompte(id)).thenReturn(compte);

			// WHEN
			ResultActions resultat = mockMvc.perform(get("/afficherCompte/{id}", id));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(view().name("compte"))
					.andExpect(model().attribute("nom", "Jean DUPONT"))
					.andExpect(model().attribute("numeroCompte", 1))
					.andExpect(model().attribute("lignes", hasSize(0)))
					.andExpect(model().attribute("solde", 0.00f));
			verify(ligneService, times(1)).rechercherLignes(id);
			verify(compteService, times(1)).rechercherCompte(id);
		}
		
		@Test
		@DisplayName("Une erreur 404 de l'API doit afficher la page d'erreur avec le message 'La ressource demandée n'existe pas.'")
		public void afficherCompte_doitAfficherPageErreur_quandCompteInexistant() throws Exception {
		    // GIVEN
		    int id = 999;
		    when(compteService.rechercherCompte(id)).thenThrow(HttpClientErrorException.NotFound.class);

		    // WHEN
		    ResultActions resultat = mockMvc.perform(get("/afficherCompte/{id}", id));

		    // THEN
		    resultat.andExpect(status().isOk())
		            .andExpect(view().name("erreur"))
		            .andExpect(model().attribute("message", "La ressource demandée n'existe pas."));
		}
	}

	@Test
	@Tag("suppression")
	@DisplayName("Supprimer un compte doit rediriger vers la page d'accueil")
	public void supprimerCompte_doitRedirigerVersPageDAccueil() throws Exception {
		// GIVEN
		int id = 1;

		doNothing().when(compteService).supprimerCompte(id);

		// WHEN
		ResultActions resultat = mockMvc.perform(post("/supprimerCompte/{id}", id));

		// THEN
		resultat.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
		verify(compteService, times(1)).supprimerCompte(id);
	}

	@Nested
	@Tag("lecture")
	@DisplayName("Affichage des formulaires")
	class FormulaireTest {

		@Test
		@DisplayName("Créer un compte doit ouvrir le formulaire de création de compte")
		public void creerCompte_doitOuvrirFormulaireCreationCompte() throws Exception {
			// GIVEN

			// WHEN
			ResultActions resultat = mockMvc.perform(get("/creerCompte"));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(view().name("formNewCompte"))
					.andExpect(model().attributeExists("compte"));
		}

		@Test
		@DisplayName("Mettre à jour un compte doit ouvrir le formulaire de Mise à jour de compte")
		public void mettreAJourCompte_doitOuvrirFormulaireMiseAJourCompte() throws Exception {
			// GIVEN
			int id = 1;
			Compte compte = creerCompte();

			when(compteService.rechercherCompte(id)).thenReturn(compte);

			// WHEN
			ResultActions resultat = mockMvc.perform(get("/mettreAJourCompte/{id}", id));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(view().name("formUpdateCompte"))
					.andExpect(model().attributeExists("compte"));
			verify(compteService, times(1)).rechercherCompte(id);
		}
	}

	@Nested
	@Tag("ecriture")
	@DisplayName("Sauvegarde d'un compte")
	class SauvegardeTest {

		@Test
		@DisplayName("Sauvegarder un compte doit rediriger vers la page d'accueil")
		public void sauvegarderCompte_doitRedirigerVersPageDAccueil() throws Exception {
			// GIVEN
			Compte compte = creerCompte();

			when(compteService.sauvegarderCompte(any(Compte.class))).thenReturn(compte);

			// WHEN
			ResultActions resultat = mockMvc.perform(post("/sauvegarderCompte")
											.param("nom", "Dupont")
											.param("prenom", "Jean"));

			// THEN
			resultat.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/"));
			verify(compteService, times(1)).sauvegarderCompte(any(Compte.class));
		}

		@Test
		@DisplayName("Sauvegarder un compte sans numéro de compte qui a des champs obligatoires manquants doit réafficher le formulaire de création")
		public void sauvegarderCompte_doitReafficherFormulaireCreation_quandChampsObligatoiresManquantsPourCreationCompte()
				throws Exception {
			// GIVEN

			// WHEN
			ResultActions resultat = mockMvc.perform(post("/sauvegarderCompte")
											.param("nom", "DUPONT"));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(view().name("formNewCompte"));
			verify(compteService, never()).sauvegarderCompte(any(Compte.class));
		}

		@Test
		@DisplayName("Sauvegarder un compte avec numéro de compte qui a des champs obligatoires manquants doit réafficher le formulaire de misa à jour")
		public void sauvegarderCompte_doitReafficherFormulaireMiseAJour_quandChampsObligatoiresManquantsPourMisaAJourCompte()
				throws Exception {
			// GIVEN

			// WHEN
			ResultActions resultat = mockMvc.perform(post("/sauvegarderCompte")
											.param("numeroCompte", "1")
											.param("nom", "DUPONT"));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(view().name("formUpdateCompte"));
			verify(compteService, never()).sauvegarderCompte(any(Compte.class));
		}
	}
}
