package com.banque.compte.api.model;





import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table (name="LIGNE")
public class Ligne {
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long numeroCompte;
	
	private String description;
	
	private String categorie;
	
	private Float montant;
	
	private Date dateOperation;

}
