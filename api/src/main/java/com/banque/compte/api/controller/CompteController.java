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
import com.banque.compte.api.service.CompteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CompteController {

	private final CompteService compteService;

	@GetMapping("/comptes")
	public ResponseEntity<Iterable<Compte>> rechercherComptes() {
		return ResponseEntity.ok(compteService.rechercherComptes());
	}

	@GetMapping("/compte/{id}")
	public ResponseEntity<Compte> rechercherCompte(@PathVariable("id") final Long id) {
		return compteService.rechercherCompte(id).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/compte/{id}")
	public ResponseEntity<Void> supprimerCompte(@PathVariable("id") final Long id) {
		if (compteService.rechercherCompte(id).isPresent()) {
			compteService.supprimerCompte(id);
			return ResponseEntity.noContent().build();
		} else
			return ResponseEntity.notFound().build();
	}

	@PostMapping("/compte")
	public ResponseEntity<Compte> creerCompte(@RequestBody Compte compte) {
		Compte compteCree = compteService.sauvegarderCompte(compte);
	    return ResponseEntity.status(HttpStatus.CREATED).body(compteCree);
	}

	@PutMapping("/compte/{id}")
	public ResponseEntity<Compte> mettreAJourCompte(@PathVariable("id") final Long id, @RequestBody Compte compte) {
		Optional<Compte> ancienCompte = compteService.rechercherCompte(id);
		if (ancienCompte.isPresent()) {
			Compte compteAChanger = ancienCompte.get();

			if (compte.getNom() != null)
				compteAChanger.setNom(compte.getNom());
			if (compte.getPrenom() != null)
				compteAChanger.setPrenom(compte.getPrenom());
			if (compte.getMail() != null)
				compteAChanger.setMail(compte.getMail());

		    return ResponseEntity.ok(compteService.sauvegarderCompte(compteAChanger));
		} else
			return ResponseEntity.notFound().build();

	}
}
