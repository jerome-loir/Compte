package com.banque.compte.webapp.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GestionnaireExceptions {

	@ExceptionHandler(HttpClientErrorException.NotFound.class)
	public String ressourceIntrouvable(Model model) {
		model.addAttribute("message", "La ressource demandée n'existe pas.");
		return "erreur";
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public String donneesInvalides(Model model) {
		model.addAttribute("message", "Les données saisies sont invalides.");
		return "erreur";
	}
}