package com.banque.compte.webapp.model;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;


@Data
public class Compte {
	
	private Long numeroCompte;
	
	@NotBlank(message = "Le nom est obligatoire")
	private String nom;
	
	@NotBlank(message = "Le prénom est obligatoire")
	private String prenom;
	
	private String mail;
}
