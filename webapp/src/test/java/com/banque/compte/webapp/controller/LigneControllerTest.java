package com.banque.compte.webapp.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.banque.compte.webapp.model.Ligne;
import com.banque.compte.webapp.service.LigneService;

@WebMvcTest(LigneController.class)
@DisplayName("LigneController")
class LigneControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private LigneService ligneService;

	@Test
	@Tag("suppression")
	@DisplayName("Supprimer une ligne doit rediriger vers la page d'affichage du compte de la ligne supprimée")
	public void supprimerLigne_doitRedirigerVersPageCompteDeLigneSupprimee() throws Exception {
		// GIVEN
		int id = 1;
		Ligne ligne = new Ligne();
		ligne.setNumeroCompte(1L);

		when(ligneService.rechercherLigne(id)).thenReturn(ligne);
		doNothing().when(ligneService).supprimerLigne(id);

		// WHEN
		ResultActions resultat = mockMvc.perform(post("/supprimerLigne/{id}", id));

		// THEN
		resultat.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/afficherCompte/"+id));
		verify(ligneService, times(1)).rechercherLigne(id);
		verify(ligneService, times(1)).supprimerLigne(id);
	}

	@Nested
	@Tag("lecture")
	@DisplayName("Affichage des formulaires")
	class FormulaireTest {

		@Test
		@DisplayName("Créer une ligne doit ouvrir le formulaire de création de ligne avec le bon numéro de compte")
		public void creerLigne_doitOuvrirFormulaireCreationLigne() throws Exception {
			// GIVEN
			Long numeroCompte = 1L;

			// WHEN
			ResultActions resultat = mockMvc.perform(get("/creerLigne/{numeroCompte}", numeroCompte));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(view().name("formNewLigne"))
					.andExpect(model().attribute("ligne", hasProperty("numeroCompte", is(1L))));
		}

		@Test
		@DisplayName("Mettre à jour une ligne doit ouvrir le formulaire de Mise à jour de ligne")
		public void mettreAJourLigne_doitOuvrirFormulaireMiseAJourLigne() throws Exception {
			// GIVEN
			int id = 1;
			Ligne ligne = new Ligne();

			when(ligneService.rechercherLigne(id)).thenReturn(ligne);

			// WHEN
			ResultActions resultat = mockMvc.perform(get("/mettreAJourLigne/{id}", id));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(view().name("formUpdateLigne"))
					.andExpect(model().attributeExists("ligne"));
			verify(ligneService, times(1)).rechercherLigne(id);
		}
		
		@Test
		@DisplayName("Mettre à jour une ligne avec un paramètre de mauvais type doit afficher la page d'erreur avec le message 'Les données saisies sont invalides.'")
		public void mettreAJourLigne_doitAfficherPageErreur_quandParamètreDeMauvaisType() throws Exception {
			// GIVEN
			String id = "id";
			
			// WHEN
			ResultActions resultat = mockMvc.perform(get("/mettreAJourLigne/{id}", id));
			
			// THEN
			resultat.andExpect(status().isOk())
            		.andExpect(view().name("erreur"))
            		.andExpect(model().attribute("message", "Les données saisies sont invalides."));
		}
	}

	@Nested
	@Tag("ecriture")
	@DisplayName("Sauvegarde d'une ligne")
	class SauvegardeTest {

		@Test
		@DisplayName("Sauvegarder une ligne doit rediriger vers la page d'affichage du compte de la ligne sauvegardée")
		public void sauvegarderLigne_doitRedirigerVersPageCompteDeLigneSauvegardee() throws Exception {
			// GIVEN
			Ligne ligne = new Ligne();
			ligne.setNumeroCompte(1L);

			when(ligneService.sauvegarderLigne(any(Ligne.class))).thenReturn(ligne);

			// WHEN
			ResultActions resultat = mockMvc.perform(post("/sauvegarderLigne")
											.param("numeroCompte", "1")
											.param("description", "Courses")
											.param("categorie", "Alimentation")
											.param("montant", "-25.50")
											.param("dateOperation", "2025-09-21"));

			// THEN
			resultat.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/afficherCompte/" + ligne.getNumeroCompte()));
			verify(ligneService, times(1)).sauvegarderLigne(any(Ligne.class));
		}

		@Test
		@DisplayName("Sauvegarder une ligne sans Id qui a des champs obligatoires manquants doit réafficher le formulaire de création")
		public void sauvegarderLigne_doitReafficherFormulaireCreation_quandChampsObligatoiresManquantsPourCreationLigne()
				throws Exception {
			// GIVEN

			// WHEN
			ResultActions resultat = mockMvc.perform(post("/sauvegarderLigne")
											.param("numeroCompte", "1"));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(view().name("formNewLigne"));
			verify(ligneService, never()).sauvegarderLigne(any(Ligne.class));
		}

		@Test
		@DisplayName("Sauvegarder une ligne avec Id qui a des champs obligatoires manquants doit réafficher le formulaire de misa à jour")
		public void sauvegarderLigne_doitReafficherFormulaireMiseAJour_quandChampsObligatoiresManquantsPourMisaAJourLigne()
				throws Exception {
			// GIVEN

			// WHEN
			ResultActions resultat = mockMvc.perform(post("/sauvegarderLigne")
											.param("id", "1")
											.param("numeroCompte", "1"));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(view().name("formUpdateLigne"));
			verify(ligneService, never()).sauvegarderLigne(any(Ligne.class));
		}
	}
}
