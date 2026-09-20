package com.banque.compte.webapp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

@Tag("exceptions")
@DisplayName("GestionnaireExceptions")
public class GestionnaireExceptionsTest {
	
	private GestionnaireExceptions gestionnaire;
	private Model model;
	
	@BeforeEach
	public void setUp() {
		// GIVEN
		gestionnaire = new GestionnaireExceptions();
		model = new ExtendedModelMap();
	}
	
	@Test
	@DisplayName("Une exception pour ressource introuvable doit rediriger vers la page d'erreur")
	public void ressourceIntrouvable_doitRetournerVueErreurAvecMessage() {
		// WHEN
		String vue = gestionnaire.ressourceIntrouvable(model);

		// THEN
		assertThat(vue).isEqualTo("erreur");
		assertThat(model.getAttribute("message")).isEqualTo("La ressource demandée n'existe pas.");
	}

	@Test
	@DisplayName("Une exception pour données saisies invalides doit rediriger vers la page d'erreur")
	public void donneesSaisiesInvalides_doitRetournerVueErreurAvecMessage() {
		// WHEN
		String vue = gestionnaire.donneesInvalides(model);

		// THEN
		assertThat(vue).isEqualTo("erreur");
		assertThat(model.getAttribute("message")).isEqualTo("Les données saisies sont invalides.");
	}
}
