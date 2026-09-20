package com.banque.compte.webapp.model;

import java.sql.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Ligne {

	private Long id;
	
	@NotNull(message = "Le vuméro de compte est obligatoire")
	private Long numeroCompte;
	
	@NotBlank(message = "La description est obligatoire")
	private String description;
	
	private String categorie;
	
	@NotNull(message = "Le montant est obligatoire")
	private Float montant;
	
	@NotNull(message = "La date de l'opération est obligatoire")
	private Date dateOperation;
}
