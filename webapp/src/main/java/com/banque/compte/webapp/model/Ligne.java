package com.banque.compte.webapp.model;





import java.sql.Date;

import lombok.Data;

@Data
public class Ligne {

	private Long id;
	
	private Long numeroCompte;
	
	private String description;
	
	private String categorie;
	
	private Float montant;
	
	private Date dateOperation;
}
