package com.banque.compte.webapp.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.banque.compte.webapp.e2e.page.PageAccueil;
import com.banque.compte.webapp.e2e.page.PageCompte;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OperationsSurLigneE2E {
	
	private static Process processusApi;
	private static final String NOM_COMPTE_TEST = "test_e2e";
	private static final String PRENOM_COMPTE_TEST = "test_e2e";
	private static String numeroCompteTest;
	
	@LocalServerPort
	private Integer port;
	private WebDriver webDriver = null;
	private String baseUrl;

	private record DonneesLigne(String description, String categorie, String montant, String date) {

	    static DonneesLigne uniques() {
	        return new DonneesLigne(descriptionUnique("Alimentation"), "Courses", "-23.98", "2025-09-21");
	    }

	    static String descriptionUnique(String prefixe) {
	        return prefixe + "_" + System.currentTimeMillis(); // description unique, donc facile à retrouver
	    }
	}

	private DonneesLigne creerUneLigne(PageCompte pageCompte) {
		DonneesLigne ligne = DonneesLigne.uniques();
		pageCompte.ajouterLigne(ligne.description(), ligne.categorie(), ligne.montant(), ligne.date());
	    return ligne;
	}
	
	private PageCompte afficherPageCompteTest() {
    	final PageAccueil pageAccueil = new PageAccueil(webDriver);
    	pageAccueil.afficherPageCompte(numeroCompteTest);
		return new PageCompte(webDriver);
	}
	
    private static boolean attendreQueApiSoitPrete() throws InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        for (int i = 0; i < 30; i++) {
            try {
                HttpRequest requete = HttpRequest.newBuilder(URI.create("http://localhost:9000/comptes")).build();
                HttpResponse<String> reponse = client.send(requete, HttpResponse.BodyHandlers.ofString());
                if (reponse.statusCode() == 200) return true; // l'API répond, elle est prête
            } catch (Exception e) {
                // pas encore prête, on réessaie
            }
            Thread.sleep(1000);
        }
        return false;
    }
    
	@BeforeAll
    static void demarrerApiAvecNouveauCompteDeTest() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
            "java", "-jar", "../api/target/api-0.0.1-SNAPSHOT.jar",
            "--spring.profiles.active=h2"
        );
        pb.inheritIO(); // affiche les logs de l'API dans la console du test
        processusApi = pb.start();

        boolean pret = attendreQueApiSoitPrete();
        if (!pret) {
            processusApi.destroy();
            throw new IllegalStateException(
                "L'API n'a pas démarré — vérifiez qu'aucune instance ne tourne déjà sur le port 9000.");
        }
        try {
            creerCompteTestViaApi();
        } catch (Exception e) {
            processusApi.destroy();
            throw e;
        }
    }

    private static void creerCompteTestViaApi() throws IOException, InterruptedException {
		String json = "{\"nom\":\"" + NOM_COMPTE_TEST + "\",\"prenom\":\"" + PRENOM_COMPTE_TEST + "\",\"mail\":\"\"}";
		HttpRequest requete = HttpRequest.newBuilder(URI.create("http://localhost:9000/compte"))
				.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();
		HttpResponse<String> reponse = HttpClient.newHttpClient().send(requete, HttpResponse.BodyHandlers.ofString());
		if (reponse.statusCode() != 201) {
			throw new IllegalStateException("Création du compte de test impossible : " + reponse.statusCode());
		}
		numeroCompteTest = new ObjectMapper().readTree(reponse.body()).get("numeroCompte").asText();
	}
	
    @AfterAll
    static void arreterApi() {
        if (processusApi != null) {
            processusApi.destroy();
        }
    }

	@BeforeEach
	public void setUpWebDriver() {
		webDriver = new FirefoxDriver();
		baseUrl = "http://localhost:" + port + "/";
		webDriver.get(baseUrl);
	}

	@AfterEach
	public void quitWebDriver() {
		if (webDriver != null) {
			webDriver.quit();
		}
	}

    @Test
    @DisplayName("Créer une ligne via le formulaire doit la rendre visible sur la page du compte")
    public void unUtilisateurUtiliseLApplicationPourCreerUneLigne() {
		// GIVEN	
		final PageCompte pageCompte = afficherPageCompteTest();
		final DonneesLigne nouvelleLigne = DonneesLigne.uniques();

		// WHEN
		pageCompte.ajouterLigne(nouvelleLigne.description(), nouvelleLigne.categorie(), nouvelleLigne.montant(), nouvelleLigne.date());

		// THEN
		final List<String> listeDescritionsLignesActualisee = pageCompte.lireDescriptionsLignes();
		assertThat(listeDescritionsLignesActualisee).contains(nouvelleLigne.description());
    	
    }
    
    @Test
    @DisplayName("Modifier une ligne via le formulaire doit rendre la modification visible sur la page du compte")
    public void unUtilisateurUtiliseLApplicationPourModifierUneLigne() {
		// GIVEN
    	final PageCompte pageCompte = afficherPageCompteTest();
		final DonneesLigne ligne = creerUneLigne(pageCompte);
		
		// WHEN
		final String nouvelleDescription = DonneesLigne.descriptionUnique("Vetements");
		pageCompte.modifierLigneParDescription(ligne.description(), nouvelleDescription, null, null, null);

		// THEN
		final List<String> listeDescritionsLignesActualisee = pageCompte.lireDescriptionsLignes();
		assertThat(listeDescritionsLignesActualisee).contains(nouvelleDescription);  	
		assertThat(listeDescritionsLignesActualisee).doesNotContain(ligne.description());
    }
    
    @Test
    @DisplayName("Supprimer une ligne doit la rendre invisible sur la page d'accueil du compte")
    public void unUtilisateurUtiliseLApplicationPourSupprimerUneLigne() {
		// GIVEN
    	final PageCompte pageCompte = afficherPageCompteTest();
		final DonneesLigne ligne = creerUneLigne(pageCompte);
	
		// WHEN		
		pageCompte.supprimerLigneParDescription(ligne.description());

		// THEN
		final List<String> listeDescritionsLignesActualisee = pageCompte.lireDescriptionsLignes(); 	
		assertThat(listeDescritionsLignesActualisee).doesNotContain(ligne.description());
    }
}
