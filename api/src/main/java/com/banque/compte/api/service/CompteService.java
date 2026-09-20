package com.banque.compte.api.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.banque.compte.api.model.Compte;
import com.banque.compte.api.repository.CompteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompteService {
	
	private final CompteRepository compteRepository;
	private final LigneService ligneservice;
	
	public Iterable<Compte> rechercherComptes(){
		return compteRepository.findAll();
	}
	
	public Optional<Compte> rechercherCompte(final Long id){
		return compteRepository.findById(id);
	}
	
	public void supprimerCompte(final Long id) {
		ligneservice.supprimerLignesDUnCompte(id);
		compteRepository.deleteById(id);
	}

	public Compte sauvegarderCompte(Compte compte) {
		return compteRepository.save(compte);
	}
}
