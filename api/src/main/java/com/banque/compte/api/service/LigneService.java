package com.banque.compte.api.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.banque.compte.api.model.Ligne;
import com.banque.compte.api.repository.LigneRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LigneService {

	private final LigneRepository ligneRepository;
	
	public Iterable<Ligne> rechercherLignes(final Long numeroCompte){
		List<Ligne> list = new ArrayList<>();
		Iterator<Ligne> iterator = ligneRepository.findAll().iterator();
        while (iterator.hasNext()) {
            Ligne ligne = iterator.next();
            if (ligne.getNumeroCompte() == numeroCompte)
            	list.add(ligne);
        }
		return list;
	}
	
	public Optional<Ligne> rechercherLigne(final Long id){
		return ligneRepository.findById(id);
	}
	
	public void supprimerLigne(final Long id) {
		ligneRepository.deleteById(id);
	}
	
	public void supprimerLignesDUnCompte(final Long numeroCompte) {
		Iterator<Ligne> iterator = ligneRepository.findAll().iterator();
        while (iterator.hasNext()) {
            Ligne ligne = iterator.next();
            if (ligne.getNumeroCompte() == numeroCompte)
            	ligneRepository.deleteById(ligne.getId());
        }
	}
	
	public Ligne sauvegarderLigne(Ligne ligne) {
		return ligneRepository.save(ligne);
	}
}
