package com.banque.compte.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.banque.compte.webapp.model.Ligne;
import com.banque.compte.webapp.service.LigneService;

@Controller
public class LigneController {

	@Autowired
	private LigneService ligneService;
	
	@GetMapping("/supprimerLigne/{id}")
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
	public ModelAndView sauvegarderLigne(@ModelAttribute Ligne ligne){//, @RequestParam("dateOperation") String dateOperation) throws ParseException {
		
	/**	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);

		Date parsed = format.parse(dateOperation);
		System.out.println(parsed);*/
		//ligne.setDateOperation(parsed);
		ligneService.sauvegarderLigne(ligne);
		return new ModelAndView("redirect:/afficherCompte/" + ligne.getNumeroCompte());
	}
	
}
