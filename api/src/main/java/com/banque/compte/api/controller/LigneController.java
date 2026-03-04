package com.banque.compte.api.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.banque.compte.api.model.Ligne;
import com.banque.compte.api.service.LigneService;

@RestController
public class LigneController {
	
	@Autowired
	private LigneService ligneService;
	
	@GetMapping("/lignes/{numeroCompte}")
	public Iterable<Ligne> rechercherLignes(@PathVariable("numeroCompte") final Long numeroCompte){
		return ligneService.rechercherLignes(numeroCompte);
	}

	@GetMapping("/ligne/{id}")
	public Ligne rechercherLigne(@PathVariable("id") final Long id){
		Optional<Ligne> ligne = ligneService.rechercherLigne(id);
		if(ligne.isPresent())
			return ligne.get();
		else
			return null;
	}
	
	@DeleteMapping("/ligne/{id}")
	public void supprimerLigne(@PathVariable("id") final Long id) {
		ligneService.supprimerLigne(id);
	}
	
	@PostMapping("/ligne")
	public Ligne creerLigne(@RequestBody Ligne ligne) {
		return ligneService.sauvegarderLigne(ligne);
	}
	
	@PutMapping("/ligne/{id}")
	public Ligne mettreAJourLigne (@PathVariable("id") final Long id, @RequestBody Ligne ligne) {
		Optional<Ligne> ancienneLigne = ligneService.rechercherLigne(id);
		
		if(ancienneLigne.isPresent()) {
			Ligne ligneAChanger = ancienneLigne.get();
			
			if(ligne.getDescription() != null)
				ligneAChanger.setDescription(ligne.getDescription());
			if(ligne.getCategorie() != null)
				ligneAChanger.setCategorie(ligne.getCategorie());
			if(ligne.getMontant() != null)
				ligneAChanger.setMontant(ligne.getMontant());
			if(ligne.getDateOperation() != null)
				ligneAChanger.setDateOperation(ligne.getDateOperation());
			
			ligneService.sauvegarderLigne(ligneAChanger);
			return ligneAChanger;
		}
		else
			return null;
	}
}
