package com.banque.compte.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banque.compte.webapp.model.Ligne;
import com.banque.compte.webapp.repository.LigneProxy;

import lombok.Data;

@Data
@Service
public class LigneService {

	@Autowired
	private LigneProxy ligneProxy;
	
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
