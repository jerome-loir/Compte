package com.banque.compte.webapp.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OperationsSurCompteE2E {
	
	private static Process processusApi;
	
	@LocalServerPort
	private Integer port;
	private WebDriver webDriver = null;
	private String baseUrl;

	private record DonneesCompte(String nom, String prenom, String mail) {

	    static DonneesCompte uniques() {
	        return new DonneesCompte(nomUnique("test"), "rené", "rene@test.fr");
	    }

	    static String nomUnique(String prefixe) {
	        return prefixe + "_" + System.currentTimeMillis(); // nom unique, donc facile à retrouver
	    }

	    String nomAffiche() {
	        return nom.toUpperCase(); // l'application affiche les noms en majuscules
	    }
	}

	private DonneesCompte creerUnCompte(PageAccueil pageAccueil) {
	    DonneesCompte compte = DonneesCompte.uniques();
	    pageAccueil.ajouterCompte(compte.nom(), compte.prenom(), compte.mail());
	    return compte;
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
    static void demarrerApi() throws IOException, InterruptedException {
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
    }

    @AfterAll
    static void arreterApi() throws InterruptedException {
        if (processusApi != null) {
            processusApi.destroy();
            processusApi.waitFor(10, TimeUnit.SECONDS);
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
    @DisplayName("Créer un compte via le formulaire doit le rendre visible sur la page d'accueil")
    public void unUtilisateurUtiliseLApplicationPourCreerUnCompte() {
		// GIVEN
		final PageAccueil pageAccueil= new PageAccueil(webDriver);
		final DonneesCompte nouveauCompte = DonneesCompte.uniques();

		// WHEN
		pageAccueil.ajouterCompte(nouveauCompte.nom(), nouveauCompte.prenom(), nouveauCompte.mail());

		// THEN
		final List<String> listeNomsComptesActualisee = pageAccueil.lireNomsComptes();
		assertThat(listeNomsComptesActualisee).contains(nouveauCompte.nomAffiche());
    	
    }
    
    @Test
    @DisplayName("Modifier un compte (nom ou prénom) via le formulaire doit rendre la modification visible sur la page d'accueil")
    public void unUtilisateurUtiliseLApplicationPourModifierUnCompte() {
		// GIVEN
		final PageAccueil pageAccueil= new PageAccueil(webDriver);
		final DonneesCompte compte = creerUnCompte(pageAccueil);

		// WHEN
		final String nomModifieCompte = DonneesCompte.nomUnique("modification");
		pageAccueil.modifierCompteParNomCompte(compte.nom(), nomModifieCompte, null, null);

		// THEN
		final List<String> listeNomsComptesActualisee = pageAccueil.lireNomsComptes();
		assertThat(listeNomsComptesActualisee).contains(nomModifieCompte.toUpperCase());
		assertThat(listeNomsComptesActualisee).doesNotContain(compte.nomAffiche());
    }
    
    @Test
    @DisplayName("Supprimer un compte doit le rendre invisible sur la page d'accueil")
    public void unUtilisateurUtiliseLApplicationPourSupprimerUnCompte() {
		// GIVEN
		final PageAccueil pageAccueil= new PageAccueil(webDriver);
		final DonneesCompte compte = creerUnCompte(pageAccueil);
	
		// WHEN		
		pageAccueil.supprimerCompteParNomCompte(compte.nom());
		
		// THEN
		final List<String> listeNomsComptesActualisee = pageAccueil.lireNomsComptes();
		assertThat(listeNomsComptesActualisee).doesNotContain(compte.nomAffiche());
    }
}
