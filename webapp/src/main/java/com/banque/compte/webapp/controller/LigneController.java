package com.banque.compte.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.banque.compte.webapp.model.Ligne;
import com.banque.compte.webapp.service.LigneService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LigneController {

	private final LigneService ligneService;
	
	@PostMapping("/supprimerLigne/{id}")
	public ModelAndView supprimerLigne(@PathVariable("id") int id) {
		Long numeroCompte = ligneService.rechercherLigne(id).getNumeroCompte();
		ligneService.supprimerLigne(id);
		return new ModelAndView("redirect:/afficherCompte/" + numeroCompte);
	}
	
	@GetMapping("/creerLigne/{numeroCompte}")
	public String creerLigne(@PathVariable("numeroCompte") Long numeroCompte, Model model) {
		Ligne ligne = new Ligne();
		ligne.setNumeroCompte(numeroCompte);
		model.addAttribute("ligne",ligne);
		return "formNewLigne";
	}
	
	@GetMapping("/mettreAJourLigne/{id}")
	public String mettreAJourLigne(@PathVariable("id") int id, Model model) {
		Ligne ligne = ligneService.rechercherLigne(id);
		model.addAttribute("ligne", ligne);
		return "formUpdateLigne";
	}
	
	@PostMapping("/sauvegarderLigne")
	public ModelAndView sauvegarderLigne(@Valid @ModelAttribute Ligne ligne, BindingResult bindingResult){
		if (bindingResult.hasErrors()) {
	        String vue = (ligne.getId() != null) ? "formUpdateLigne" : "formNewLigne";
	        ModelAndView mav = new ModelAndView(vue);
	        mav.addObject("ligne", ligne);
	        return mav;
	    }
		ligneService.sauvegarderLigne(ligne);
		return new ModelAndView("redirect:/afficherCompte/" + ligne.getNumeroCompte());
	}
	
}
