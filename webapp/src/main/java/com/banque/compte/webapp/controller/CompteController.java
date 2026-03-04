package com.banque.compte.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.banque.compte.webapp.model.Compte;
import com.banque.compte.webapp.model.Ligne;
import com.banque.compte.webapp.service.CompteService;
import com.banque.compte.webapp.service.LigneService;

@Controller
public class CompteController {

	@Autowired
	private CompteService compteService;
	
	@Autowired
	private LigneService ligneService;
	
	@GetMapping("/")
	public String home(Model model) {
		Iterable<Compte> listCompte = compteService.rechercherComptes();
		model.addAttribute("comptes", listCompte);
		return "home";
	}
	
	@GetMapping("/afficherCompte/{id}")
	public String afficherCompte(@PathVariable("id") int id, Model model) {
		String nom = compteService.rechercherCompte(id).getPrenom() + " " + compteService.rechercherCompte(id).getNom();
		Float solde = 0.00f;
		Iterable<Ligne> listLigne = ligneService.rechercherLignes(id);
		for (Ligne ligne : listLigne) {
			solde = solde + ligne.getMontant();
		}
		model.addAttribute("nom",nom);
		model.addAttribute("numeroCompte",id);
		model.addAttribute("lignes", listLigne);
		model.addAttribute("solde", solde);
		return "compte";
	}
	
	@GetMapping("/supprimerCompte/{id}")
	public ModelAndView supprimerCompte(@PathVariable("id") int id) {
		compteService.supprimerCompte(id);
		return new ModelAndView("redirect:/");
	}
	
	@GetMapping("/creerCompte")
	public String creerCompte(Model model) {
		Compte compte = new Compte();
		model.addAttribute("compte", compte);
		return "formNewCompte";
	}
	
	@GetMapping("/mettreAJourCompte/{id}")
	public String mettreAJourCompte(@PathVariable("id") int id, Model model) {
		Compte compte = compteService.rechercherCompte(id);
		model.addAttribute("compte", compte);
		return "formUpdateCompte";
	}
	
	@PostMapping("/sauvegarderCompte")
	public ModelAndView sauvegarderCompte(@ModelAttribute Compte compte) {
		compteService.sauvegarderCompte(compte);
		return new ModelAndView("redirect:/");
	}
}
