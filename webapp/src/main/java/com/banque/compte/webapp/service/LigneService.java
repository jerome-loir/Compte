package com.banque.compte.webapp.service;

import org.springframework.stereotype.Service;

import com.banque.compte.webapp.model.Ligne;
import com.banque.compte.webapp.repository.LigneProxy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LigneService {

	private final LigneProxy ligneProxy;
	
	public Iterable<Ligne> rechercherLignes(int numeroCompte){
		return ligneProxy.rechercherLignes(numeroCompte);
	}
	
	public Ligne rechercherLigne(int id) {
		return ligneProxy.rechercherLigne(id);
	}
	
	public void supprimerLigne(int id) {
		ligneProxy.supprimerLigne(id);
	}
	
	public Ligne sauvegarderLigne(Ligne ligne) {
		Ligne ligneSauvegardee;
		
		if (ligne.getId() != null)
			// Si l'id n'est pas nul, alors il s'agit d'une modification de ligne.
			ligneSauvegardee = ligneProxy.mettreAJourLigne(ligne);
		else
			ligneSauvegardee = ligneProxy.creerLigne(ligne);
		
		return ligneSauvegardee;
	}
}
