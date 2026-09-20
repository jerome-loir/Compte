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

import com.banque.compte.api.model.Compte;
import com.banque.compte.api.service.CompteService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CompteController.class)
@DisplayName("CompteController")
class CompteControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CompteService compteService;

	@Autowired
	private ObjectMapper objectMapper;

	private Compte creerCompte() {
		Compte compte = new Compte();
		compte.setNom("Dupont");
		compte.setPrenom("Jean");
		compte.setMail("jean.dupont@test.fr");
		return compte;
	}

	@Nested
	@Tag("lecture")
	@DisplayName("Recherche de l'ensemble des comptes")
	class RechercheComptesTest {

		@Test
		@DisplayName("La recherche de tous les comptes retourne 200 et la liste quand des comptes existent")
		void rechercherComptes_doitRetourner200EtLaListeSiComptesPresents() throws Exception {
			// GIVEN
			Compte compte = creerCompte();

			when(compteService.rechercherComptes()).thenReturn(List.of(compte));

			// WHEN
			ResultActions resultat = mockMvc.perform(get("/comptes"));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].nom").value("Dupont"));
			verify(compteService, times(1)).rechercherComptes();
		}

		@Test
		@DisplayName("La recherche de tous les comptes retourne 200 et une liste vide quand il n'y a aucun compte")
		void rechercherComptes_doitRetourner200EtListeVideSiPasDeCompte() throws Exception {
			// GIVEN
			when(compteService.rechercherComptes()).thenReturn(List.of());

			// WHEN
			ResultActions resultat = mockMvc.perform(get("/comptes"));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(jsonPath("$").isArray())
					.andExpect(jsonPath("$").isEmpty());
			verify(compteService, times(1)).rechercherComptes();
		}
	}

	@Nested
	@Tag("lecture")
	@DisplayName("Recherche d'un compte par id")
	class RechercheCompteTest {

		@Test
		@DisplayName("Retourne 200 et le compte quand il existe")
		void rechercherCompteExistant_doitRetourner200EtLeCompte() throws Exception {
			// GIVEN
			Long id = 1L;
			Compte compte = creerCompte();

			when(compteService.rechercherCompte(id)).thenReturn(Optional.of(compte));

			// WHEN
			ResultActions resultat = mockMvc.perform(get("/compte/{id}", id));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(jsonPath("$.prenom").value("Jean"));
			verify(compteService, times(1)).rechercherCompte(id);
		}

		@Test
		@DisplayName("Retourne 404 quand le compte n'existe pas")
		void rechercherCompteInexistant_doitRetourner404() throws Exception {
			// GIVEN
			Long id = 1L;

			when(compteService.rechercherCompte(id)).thenReturn(Optional.empty());

			// WHEN
			ResultActions resultat = mockMvc.perform(get("/compte/{id}", id));

			// THEN
			resultat.andExpect(status().isNotFound());
			verify(compteService, times(1)).rechercherCompte(id);
		}
	}

	@Nested
	@Tag("suppression")
	@DisplayName("Suppression d'un compte")
	class SuppressionCompteTest {

		@Test
		@DisplayName("Retourne 204 quand le compte existe")
		void supprimerCompteExistant_doitRetourner204() throws Exception {
			// GIVEN
			Long id = 1L;

			when(compteService.rechercherCompte(id)).thenReturn(Optional.of(new Compte()));
			doNothing().when(compteService).supprimerCompte(id);

			// WHEN
			ResultActions resultat = mockMvc.perform(delete("/compte/{id}", id));

			// THEN
			resultat.andExpect(status().isNoContent());
			verify(compteService, times(1)).rechercherCompte(id);
			verify(compteService, times(1)).supprimerCompte(id);
		}

		@Test
		@DisplayName("Retourne 404 quand le compte n'existe pas")
		void supprimerCompteInexistant_doitRetourner404() throws Exception {
			// GIVEN
			Long id = 1L;

			when(compteService.rechercherCompte(id)).thenReturn(Optional.empty());

			// WHEN
			ResultActions resultat = mockMvc.perform(delete("/compte/{id}", id));

			// THEN
			resultat.andExpect(status().isNotFound());
			verify(compteService, times(1)).rechercherCompte(id);
			verify(compteService, never()).supprimerCompte(any(Long.class));
		}
	}

	@Test
	@Tag("ecriture")
	@DisplayName("La création d'un compte retourne 201 et le compte créé")
	void creerCompte_doitRetourner201EtLeCompteCree() throws Exception {
		// GIVEN
		Compte compte = creerCompte();

		when(compteService.sauvegarderCompte(any(Compte.class))).thenReturn(compte);

		// WHEN
		ResultActions resultat = mockMvc.perform(post("/compte")
										.contentType(MediaType.APPLICATION_JSON)
										.content(objectMapper.writeValueAsString(compte)));

		// THEN
		resultat.andExpect(status().isCreated())
				.andExpect(jsonPath("$.mail").value("jean.dupont@test.fr"));
		verify(compteService, times(1)).sauvegarderCompte(any(Compte.class));
	}

	@Nested
	@Tag("ecriture")
	@DisplayName("Mise à jour d'un compte")
	class MiseAJourCompteTest {

		@Test
		@DisplayName("Retourne 200 et le compte mis à jour quand il existe")
		void mettreAJourCompteExistant_doitRetourner200EtLeCompteMisAJour() throws Exception {
			// GIVEN
			Long id = 1L;
			Compte compteExistant = creerCompte();
			Compte modification = new Compte();
			modification.setNom("Martin");
		    modification.setPrenom("Alice");
			modification.setMail("nouveau@mail.fr");

			when(compteService.rechercherCompte(id)).thenReturn(Optional.of(compteExistant));
			when(compteService.sauvegarderCompte(any(Compte.class))).thenReturn(compteExistant);

			// WHEN
			ResultActions resultat = mockMvc.perform(put("/compte/{id}", id)
											.contentType(MediaType.APPLICATION_JSON)
											.content(objectMapper.writeValueAsString(modification)));

			// THEN
			resultat.andExpect(status().isOk())
					.andExpect(jsonPath("$.mail").value("nouveau@mail.fr"));
			verify(compteService, times(1)).rechercherCompte(id);
			verify(compteService, times(1)).sauvegarderCompte(any(Compte.class));
		}

		@Test
		@DisplayName("Ne modifie pas les champs non transmis dans la requête")
		void mettreAJourCompteExistant_neDoitPasEcraserLesChampsNonTransmis() throws Exception {
			// GIVEN
			Long id = 1L;
			Compte compteExistant = creerCompte();
			Compte modification = new Compte();
			modification.setMail("nouveau@mail.fr");

			when(compteService.rechercherCompte(id)).thenReturn(Optional.of(compteExistant));
			when(compteService.sauvegarderCompte(any(Compte.class))).thenReturn(compteExistant);

			// WHEN
			ResultActions resultat = mockMvc.perform(put("/compte/{id}", id)
											.contentType(MediaType.APPLICATION_JSON)
											.content(objectMapper.writeValueAsString(modification)));

			// THEN
			resultat.andExpect(status().isOk()).andExpect(jsonPath("$.nom").value("Dupont"))
					.andExpect(jsonPath("$.prenom").value("Jean"))
					.andExpect(jsonPath("$.mail").value("nouveau@mail.fr"));
			verify(compteService, times(1)).rechercherCompte(id);
			verify(compteService, times(1)).sauvegarderCompte(any(Compte.class));
		}

		@Test
		@DisplayName("Retourne 404 quand le compte n'existe pas")
		void mettreAJourCompteInexistant_doitRetourner404() throws Exception {
			// GIVEN
			Long id = 1L;
			Compte modification = new Compte();
			modification.setNom("Martin");

			when(compteService.rechercherCompte(id)).thenReturn(Optional.empty());

			// WHEN
			ResultActions resultat = mockMvc.perform(put("/compte/{id}", id)
											.contentType(MediaType.APPLICATION_JSON)
											.content(objectMapper.writeValueAsString(modification)));

			// THEN
			resultat.andExpect(status().isNotFound());
			verify(compteService, times(1)).rechercherCompte(id);
			verify(compteService, never()).sauvegarderCompte(any(Compte.class));
		}
	}
}
