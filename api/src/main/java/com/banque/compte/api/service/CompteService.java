package com.banque.compte.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banque.compte.api.model.Compte;
import com.banque.compte.api.model.Ligne;
import com.banque.compte.api.repository.CompteRepository;

import lombok.Data;

@Data
@Service
public class CompteService {
	
	@Autowired
	private CompteRepository compteRepository;
	@Autowired
	private LigneService ligneservice;
	
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
