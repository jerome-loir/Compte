package com.banque.compte.webapp.model;

import lombok.Data;

@Data
public class Compte {
	
	private Long numeroCompte;
	
	private String nom;
	
	private String prenom;
	
	private String mail;
}
