package com.banque.compte.api.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.banque.compte.api.model.Compte;
import com.banque.compte.api.model.Ligne;
import com.banque.compte.api.service.LigneService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LigneController {
	
	private final LigneService ligneService;
	
	@GetMapping("/lignes/{numeroCompte}")
	public ResponseEntity<Iterable<Ligne>> rechercherLignes(@PathVariable("numeroCompte") final Long numeroCompte){
		return ResponseEntity.ok(ligneService.rechercherLignes(numeroCompte));
	}

	@GetMapping("/ligne/{id}")
	public ResponseEntity<Ligne> rechercherLigne(@PathVariable("id") final Long id){
		return ligneService.rechercherLigne(id).map(ResponseEntity :: ok)
				.orElse(ResponseEntity.notFound().build());
	}
	
	@DeleteMapping("/ligne/{id}")
	public ResponseEntity<Void> supprimerLigne(@PathVariable("id") final Long id) {
		if(ligneService.rechercherLigne(id).isPresent()) {
		ligneService.supprimerLigne(id);
		return ResponseEntity.noContent().build();
		}
		else
			return ResponseEntity.notFound().build();
	}
	
	@PostMapping("/ligne")
	public ResponseEntity<Ligne> creerLigne(@RequestBody Ligne ligne) {
		Ligne ligneCreee = ligneService.sauvegarderLigne(ligne);
	    return ResponseEntity.status(HttpStatus.CREATED).body(ligneCreee);
	}
	
	@PutMapping("/ligne/{id}")
	public ResponseEntity<Ligne> mettreAJourLigne (@PathVariable("id") final Long id, @RequestBody Ligne ligne) {
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
			
			return ResponseEntity.ok(ligneService.sauvegarderLigne(ligneAChanger));
		}
		else
			return ResponseEntity.notFound().build();
	}
}
