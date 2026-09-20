package com.banque.compte.webapp.repository;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.banque.compte.webapp.configuration.CustomProperties;
import com.banque.compte.webapp.model.Compte;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompteProxy {

	private final CustomProperties props;
	private final RestTemplate restTemplate;
	
	public Iterable<Compte> rechercherComptes(){
		String baseApiUrl = props.getApiUrl();
		String rechercherComptesUrl = baseApiUrl + "/comptes";
		
		ResponseEntity<Iterable<Compte>> response = restTemplate.exchange(
				rechercherComptesUrl,
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<Iterable<Compte>>() {});
		
		log.debug("Appel de rechercherComptes " + response.getStatusCode().toString());
		
		return response.getBody();
	}
	
	public Compte rechercherCompte(int id) {
		String baseApiUrl = props.getApiUrl();
		String rechercherCompteUrl = baseApiUrl + "/compte/" + id;
		
		ResponseEntity<Compte> response = restTemplate.exchange(
				rechercherCompteUrl,
				HttpMethod.GET,
				null,
				Compte.class);
		
		log.debug("Appel de rechercherCompte " + id + " " + response.getStatusCode().toString());
		
		return response.getBody();
	}
	
	public void supprimerCompte(int id) {
		String baseApiUrl = props.getApiUrl();
		String supprimerCompteUrl = baseApiUrl + "/compte/" + id;
		
		ResponseEntity<Void> response = restTemplate.exchange(
				supprimerCompteUrl,
				HttpMethod.DELETE,
				null,
				Void.class);
		
		log.debug("Appel de supprimerCompte " + id + " " + response.getStatusCode().toString());
	}
	
	public Compte creerCompte(Compte compte) {
		String baseApiUrl = props.getApiUrl();
		String creerCompteUrl = baseApiUrl + "/compte";
		
		HttpEntity<Compte> request = new HttpEntity<Compte>(compte);		
		ResponseEntity<Compte> response = restTemplate.exchange(
				creerCompteUrl,
				HttpMethod.POST,
				request,
				Compte.class
				);
		
		log.debug("Appel de creerCompte" + response.getStatusCode().toString());
		
		return response.getBody();
	}
	
	public Compte mettreAJourCompte(Compte compte) {
		String baseApiUrl = props.getApiUrl();
		String mettreAJourCompteUrl = baseApiUrl + "/compte/" + compte.getNumeroCompte();
		
		HttpEntity<Compte> request = new HttpEntity<Compte>(compte);
		ResponseEntity<Compte> response = restTemplate.exchange(
				mettreAJourCompteUrl,
				HttpMethod.PUT,
				request,
				Compte.class);
		
		log.debug("Appel de mettreAJourCompte " + compte.getNumeroCompte() + " " + response.getStatusCode().toString());
		
		return response.getBody();
	}
}
