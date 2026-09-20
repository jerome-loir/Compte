package com.banque.compte.webapp.service;

import org.springframework.stereotype.Service;

import com.banque.compte.webapp.model.Compte;
import com.banque.compte.webapp.repository.CompteProxy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor	
public class CompteService {

	private final CompteProxy compteProxy;
	
	public Iterable<Compte> rechercherComptes(){
		return compteProxy.rechercherComptes();
	}
	
	public Compte rechercherCompte(int id){
		return compteProxy.rechercherCompte(id);
	}
	
	public void supprimerCompte(int id) {
		compteProxy.supprimerCompte(id);
	}
	
	public Compte sauvegarderCompte(Compte compte) {

        // Règle de gestion : Le nom de famille doit être mis en majuscule.
		compte.setNom(compte.getNom().toUpperCase());
        
        // Règle de gestion : La première lettre du prénom doit être mise en majuscule.
		compte.setPrenom(compte.getPrenom().substring(0,1).toUpperCase()+compte.getPrenom().substring(1,compte.getPrenom().length()));

		Compte compteSauvegarde;
		
		if (compte.getNumeroCompte() != null)
			// Si l'id n'est pas nul, alors il s'agit d'une modification de compte.
			compteSauvegarde = compteProxy.mettreAJourCompte(compte);
		else
			compteSauvegarde = compteProxy.creerCompte(compte);
		return compteSauvegarde;
	}
	
}
