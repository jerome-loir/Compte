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

import com.banque.compte.api.model.Compte;
import com.banque.compte.api.service.CompteService;

@RestController
public class CompteController {
	
	@Autowired
	private CompteService compteService;
	
	@GetMapping("/comptes")
	public Iterable<Compte> rechercherComptes(){
		return compteService.rechercherComptes();
	}
	
	@GetMapping("/compte/{id}")
	public Compte rechercherCompte(@PathVariable("id") final Long id){
		Optional<Compte> compte = compteService.rechercherCompte(id);
		if (compte.isPresent())
			return compte.get();
		else
			return null;
	}
	
	@DeleteMapping("/compte/{id}")
	public void supprimerCompte(@PathVariable("id") final Long id){
		compteService.supprimerCompte(id);
	}

	@PostMapping("/compte")
	public Compte creerCompte(@RequestBody Compte compte){
		return compteService.sauvegarderCompte(compte);
	}
	
	@PutMapping("/compte/{id}")
	public Compte mettreAJourCompte(@PathVariable("id") final Long id, @RequestBody Compte compte){
		Optional<Compte> ancienCompte = compteService.rechercherCompte(id);
		if (ancienCompte.isPresent()) {
			Compte compteAChanger = ancienCompte.get();
			
			if (compte.getNom() != null)
				compteAChanger.setNom(compte.getNom());
			if (compte.getPrenom() != null)
				compteAChanger.setPrenom(compte.getPrenom());
			if (compte.getMail() != null)
					compteAChanger.setMail(compte.getMail());
			
			compteService.sauvegarderCompte(compteAChanger);
			return compteAChanger;
		}
		else
			return null;
		
	}
}
