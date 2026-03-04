package com.banque.compte.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table (name="COMPTE")
public class Compte {
	
	@Id
	@GeneratedValue (strategy=GenerationType.IDENTITY)
	private Long numeroCompte;
	
	private String nom;
	
	private String prenom;
	
	private String mail;

}
