package com.banque.compte.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Date;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.banque.compte.api.model.Ligne;
import com.banque.compte.api.service.LigneService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(LigneController.class)
class LigneControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private LigneService ligneService;

	@Autowired
	private ObjectMapper objectMapper;

	private Ligne creerLigne() throws ParseException {
		Ligne ligne = new Ligne();
		ligne.setId(1L);
		ligne.setNumeroCompte(1L);
		ligne.setDescription("Alimentation");
		ligne.setCategorie("Courses");
		ligne.setMontant(-25.50F);
		ligne.setDateOperation(Date.valueOf("2025-09-21"));
		return ligne;
	}

	@Nested
	@Tag("lecture")
	@DisplayName("Recherche de l'ensemble des lignes pour un compte donné")
	class RechercheLignesTest {

		@Test
		@DisplayName("La recherche des lignes d'un compte retourne 200 et la liste quand les lignes de ce compte existent")
		void rechercherLignes_doitRetourner200EtLaListeSiLignesPresentesPourCompteDonne() throws Exception {
			// GIVEN
			Ligne ligne = creerLigne();

			when(ligneService.rechercherLignes(any(Long.class))).thenReturn(List.of(ligne));

			// WHEN
			ResultActions resultat = mockMvc.perform(get("/lignes/{numeroCompte}", ligne.getNumeroCompte()));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].montant").value(-25.50F));
			verify(ligneService, times(1)).rechercherLignes(any(Long.class));
		}

		@Test
		@DisplayName("La recherche des lignes d'un compte retourne 200 et une liste vide quand il n'y a pas de ligne pour ce compte")
		void rechercherLignes_doitRetourner200EtListeVideSiPasDeLignePourCompteDonne() throws Exception {
			// GIVEN
			when(ligneService.rechercherLignes(any(Long.class))).thenReturn(List.of());

			// WHEN
			ResultActions resultat = mockMvc.perform(get("/lignes/{numeroCompte}", 28L));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(jsonPath("$").isArray())
					.andExpect(jsonPath("$").isEmpty());
			verify(ligneService, times(1)).rechercherLignes(any(Long.class));
		}
	}

	@Nested
	@Tag("lecture")
	@DisplayName("Recherche d'une ligne par id")
	class RechercheLigneTest {

		@Test
		@DisplayName("Retourne 200 et la ligne quand elle existe")
		void rechercherLigneExistante_doitRetourner200EtLaLigne() throws Exception {
			// GIVEN
			Long id = 1L;
			Ligne ligne = creerLigne();

			when(ligneService.rechercherLigne(id)).thenReturn(Optional.of(ligne));

			// WHEN
			ResultActions resultat = mockMvc.perform(get("/ligne/{id}", id));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(jsonPath("$.categorie").value("Courses"));
			verify(ligneService, times(1)).rechercherLigne(id);
		}

		@Test
		@DisplayName("Retourne 404 quand la ligne n'existe pas")
		void rechercherLigneInexistante_doitRetourner404() throws Exception {
			// GIVEN
			Long id = 1L;

			when(ligneService.rechercherLigne(id)).thenReturn(Optional.empty());

			// WHEN
			ResultActions resultat = mockMvc.perform(get("/ligne/{id}", id));

			// THEN
			resultat.andExpect(status().isNotFound());
			verify(ligneService, times(1)).rechercherLigne(id);
		}
	}

	@Nested
	@Tag("suppression")
	@DisplayName("Suppression d'une ligne")
	class SuppressionLigneTest {

		@Test
		@DisplayName("Retourne 204 quand la ligne existe")
		void supprimerLigneExistante_doitRetourner204() throws Exception {
			// GIVEN
			Long id = 1L;
			Ligne ligne = creerLigne();

			when(ligneService.rechercherLigne(id)).thenReturn(Optional.of(ligne));
			doNothing().when(ligneService).supprimerLigne(id);

			// WHEN
			ResultActions resultat = mockMvc.perform(delete("/ligne/{id}", id));

			// THEN
			resultat.andExpect(status().isNoContent());
			verify(ligneService, times(1)).rechercherLigne(id);
			verify(ligneService, times(1)).supprimerLigne(id);
		}

		@Test
		@DisplayName("Retourne 404 quand la ligne n'existe pas")
		void supprimerLigneInexistante_doitRetourner404() throws Exception {
			// GIVEN
			Long id = 1L;

			when(ligneService.rechercherLigne(id)).thenReturn(Optional.empty());

			// WHEN
			ResultActions resultat = mockMvc.perform(delete("/ligne/{id}", id));

			// THEN
			resultat.andExpect(status().isNotFound());
			verify(ligneService, times(1)).rechercherLigne(id);
			verify(ligneService, never()).supprimerLigne(id);
		}
	}

	@Test
	@Tag("ecriture")
	@DisplayName("La création d'une ligne retourne 201 et la ligne créée")
	void creerLigne_doitRetourner201EtLaLigneCreee() throws Exception {
		// GIVEN
		Ligne ligne = creerLigne();

		when(ligneService.sauvegarderLigne(any(Ligne.class))).thenReturn(ligne);

		// WHEN
		ResultActions resultat = mockMvc.perform(post("/ligne")
										.contentType(MediaType.APPLICATION_JSON)
										.content(objectMapper.writeValueAsString(ligne)));

		// THEN
		resultat.andExpect(status().isCreated())
				.andExpect(jsonPath("$.dateOperation").value("2025-09-21"));
		verify(ligneService, times(1)).sauvegarderLigne(any(Ligne.class));
	}

	@Nested
	@Tag("ecriture")
	@DisplayName("Mise à jour d'une ligne")
	class MiseAJourLigneTest {

		@Test
		@DisplayName("Retourne 200 et la ligne mise à jour quand elle existe")
		void mettreAJourLigneExistante_doitRetourner200EtLaLigneMiseAJour() throws Exception {
			// GIVEN
			Long id = 1L;
			Ligne ligneExistante = creerLigne();
			Ligne modification = new Ligne();
			modification.setDescription("Electricité");
			modification.setCategorie("Facture");
			modification.setMontant(-47.90F);
			modification.setDateOperation(Date.valueOf("2025-09-25"));

			when(ligneService.rechercherLigne(id)).thenReturn(Optional.of(ligneExistante));
			when(ligneService.sauvegarderLigne(any(Ligne.class))).thenReturn(ligneExistante);

			// WHEN
			ResultActions resultat = mockMvc.perform(put("/ligne/{id}", id)
											.contentType(MediaType.APPLICATION_JSON)
											.content(objectMapper.writeValueAsString(modification)));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(jsonPath("$.description").value("Electricité"))
					.andExpect(jsonPath("$.categorie").value("Facture"))
					.andExpect(jsonPath("$.montant").value(-47.90F))
					.andExpect(jsonPath("$.dateOperation").value("2025-09-25"));
			verify(ligneService, times(1)).rechercherLigne(id);
			verify(ligneService, times(1)).sauvegarderLigne(any(Ligne.class));
		}

		@Test
		@DisplayName("Ne modifie pas les champs non transmis dans la requête")
		void mettreAJourLigneExistante_neDoitPasEcraserLesChampsNonTransmis() throws Exception {
			// GIVEN
			Long id = 1L;
			Ligne ligneExistante = creerLigne();
			Ligne modification = new Ligne();
			modification.setMontant(-47.90F);

			when(ligneService.rechercherLigne(id)).thenReturn(Optional.of(ligneExistante));
			when(ligneService.sauvegarderLigne(any(Ligne.class))).thenReturn(ligneExistante);

			// WHEN
			ResultActions resultat = mockMvc.perform(put("/ligne/{id}", id)
											.contentType(MediaType.APPLICATION_JSON)
											.content(objectMapper.writeValueAsString(modification)));

			// THEN
			resultat.andExpect(status().isOk()).andExpect(jsonPath("$.montant").value(-47.90F))
					.andExpect(jsonPath("$.description").value("Alimentation"))
					.andExpect(jsonPath("$.categorie").value("Courses"))
					.andExpect(jsonPath("$.montant").value(-47.90F))
					.andExpect(jsonPath("$.dateOperation").value("2025-09-21"));
			verify(ligneService, times(1)).rechercherLigne(id);
			verify(ligneService, times(1)).sauvegarderLigne(any(Ligne.class));
		}

		@Test
		@DisplayName("Retourne 404 quand la ligne n'existe pas")
		void mettreAJourLigneInexistante_doitRetourner404() throws Exception {
			// GIVEN
			Long id = 1L;
			Ligne modification = new Ligne();
			modification.setMontant(47.90F);

			when(ligneService.rechercherLigne(id)).thenReturn(Optional.empty());

			// WHEN
			ResultActions resultat = mockMvc.perform(put("/ligne/{id}", id)
											.contentType(MediaType.APPLICATION_JSON)
											.content(objectMapper.writeValueAsString(modification)));

			// THEN
			resultat.andExpect(status().isNotFound());
			verify(ligneService, times(1)).rechercherLigne(id);
			verify(ligneService, never()).sauvegarderLigne(any(Ligne.class));

		}
	}
}
