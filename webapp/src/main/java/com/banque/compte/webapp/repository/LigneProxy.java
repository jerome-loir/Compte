package com.banque.compte.webapp.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.banque.compte.webapp.configuration.CustomProperties;
import com.banque.compte.webapp.model.Ligne;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LigneProxy {

	@Autowired
	private CustomProperties props;
	
	public Iterable<Ligne> rechercherLignes(int numeroCompte){
		String baseApiUrl = props.getApiUrl();
		String rechercherLignesUrl = baseApiUrl + "/lignes/" + numeroCompte;
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Iterable<Ligne>> response = restTemplate.exchange(
				rechercherLignesUrl,
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<Iterable<Ligne>>() {}
				);
		
		log.debug("Appel de rechercherLignes " + response.getStatusCode().toString());
		
		return response.getBody();
	}
	
	public Ligne rechercherLigne(int id) {
		String baseApiUrl = props.getApiUrl();
		String rechercherLigneUrl = baseApiUrl + "/ligne/" + id;
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Ligne> response = restTemplate.exchange(
				rechercherLigneUrl,
				HttpMethod.GET,
				null,
				Ligne.class
				);
		
		log.debug("Appel de rechercherLigne " + id + " " + response.getStatusCode().toString());
		
		return response.getBody();
	}
	
	public void supprimerLigne(int id) {
		String baseApiUrl = props.getApiUrl();
		String supprimerLigneUrl = baseApiUrl + "/ligne/" + id;
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Void> response = restTemplate.exchange(
				supprimerLigneUrl,
				HttpMethod.DELETE,
				null,
				Void.class
				);
		
		log.debug("Appel de supprimerLigne " + id + " " + response.getStatusCode().toString());
	}
	
	public Ligne creerLigne(Ligne ligne) {
		String baseApiUrl = props.getApiUrl();
		String creerLigneUrl = baseApiUrl + "/ligne";
		
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<Ligne> request = new HttpEntity<Ligne>(ligne);
		ResponseEntity<Ligne> response = restTemplate.exchange(
				creerLigneUrl,
				HttpMethod.POST,
				request,
				Ligne.class
				);
		
		log.debug("Appel de creerLigne " + response.getStatusCode().toString());
		
		return response.getBody();
	}
	
	public Ligne mettreAJourLigne(Ligne ligne) {
		String baseApiUrl = props.getApiUrl();
		String mettreAJourLigneUrl = baseApiUrl + "/ligne/" + ligne.getId();
		
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<Ligne> request = new HttpEntity<Ligne>(ligne);
		ResponseEntity<Ligne> response = restTemplate.exchange(
				mettreAJourLigneUrl,
				HttpMethod.PUT,
				request,
				Ligne.class
				);
		
		log.debug("Appel de mettreAJourLigne " + ligne.getId() + " " + response.getStatusCode().toString());
		
		return response.getBody();
	}
}
