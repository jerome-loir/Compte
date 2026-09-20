package com.banque.compte.webapp.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.banque.compte.webapp.configuration.CustomProperties;
import com.banque.compte.webapp.model.Compte;
import com.banque.compte.webapp.model.Ligne;

@ExtendWith(MockitoExtension.class)
@DisplayName("LigneProxy")
public class LigneProxyTest {
	
	public static final String BASE_URL = "http://localhost:9001";
	private	RestTemplate restTemplate;
	private MockRestServiceServer mockServer;
	private LigneProxy ligneProxy;
	
	@Mock
	private CustomProperties props;
	
	private Ligne creerLigne() {
		Ligne ligne = new Ligne();
		//ligne.setId(1L);
		ligne.setNumeroCompte(1L);
		ligne.setDescription("Alimentation");
		ligne.setCategorie("Courses");
		ligne.setMontant(-25.50F);
		ligne.setDateOperation(Date.valueOf("2025-09-21"));
		return ligne;
	}

	@BeforeEach
	public void setUp() {
		restTemplate = new RestTemplate();
		mockServer = MockRestServiceServer.createServer(restTemplate);
		ligneProxy = new LigneProxy(props, restTemplate);
		
		when(props.getApiUrl()).thenReturn(BASE_URL);
	}
	
	@Test
	@DisplayName("La recherche des lignes d'un compte appelle la bonne URL et retourne la liste des lignes reçue")
	public void rechercherLignesDUnCompte_doitAppelerApiAvecGetEtRetournerListeLignes() {
		// GIVEN
		int id = 1;
		String jsonReponse = "[{\"id\":1, \"numeroCompte\":1, \"description\":\"Alimentation\", \"categorie\":\"Courses\", \"dateOperation\":\"2025-09-21\"},{\"id\":2, \"numeroCompte\":1, \"description\":\"EDF\", \"categorie\":\"facture\", \"dateOperation\":\"2025-09-24\"}]";
		
		mockServer.expect(requestTo(BASE_URL + "/lignes/" + id))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(jsonReponse, MediaType.APPLICATION_JSON));
		
		// WHEN
		List<Ligne> resultat = (List<Ligne>) ligneProxy.rechercherLignes(id);
		
		// THEN
		assertThat(resultat).hasSize(2);
		assertThat(resultat.get(0).getDescription()).isEqualTo("Alimentation");
		assertThat(resultat.get(1).getDescription()).isEqualTo("EDF");
		mockServer.verify();
	}
	
	@Test
	@DisplayName("La recherche d'une ligne appelle la bonne URL et retourne la ligne reçue")
	public void rechercherLigne_doitAppelerApiAvecGetEtRetournerLaLigne() {
		// GIVEN
		int id = 1;
		String jsonReponse = "{\"id\":1, \"numeroCompte\":1, \"description\":\"Alimentation\", \"categorie\":\"Courses\", \"dateOperation\":\"2025-09-21\"}";
		
		mockServer.expect(requestTo(BASE_URL + "/ligne/" +id))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(jsonReponse, MediaType.APPLICATION_JSON));

		// WHEN
		Ligne resultat = ligneProxy.rechercherLigne(id);
		
		// THEN
		assertThat(resultat.getCategorie()).isEqualTo("Courses");
		mockServer.verify();
	}

	@Test
	@Tag("suppression")
	@DisplayName("La suppression d'une ligne appelle la bonne URL avec DELETE")
	public void supprimerLigne_doitAppelerApiAvecDelete() {
		// GIVEN
		int id = 1;
		
		mockServer.expect(requestTo(BASE_URL + "/ligne/" + id))
				.andExpect(method(HttpMethod.DELETE))
				.andRespond(withSuccess());
		
		// WHEN
		ligneProxy.supprimerLigne(id);
		
		// THEN
		mockServer.verify();
	}

	@Nested
	@Tag("ecriture")
	@DisplayName("Sauvegarde d'une ligne")
	class SauvegardeLigneTest {

		@Test
		@DisplayName("La création d'une ligne appelle la bonne URL avec POST et retourne la ligne créée")
		public void creerLigne_doitAppelerApiAvecPostEtRetournerLaLigneCreee() {
			// GIVEN
			Ligne ligne = creerLigne();
			String jsonReponse = "{\"id\":1, \"numeroCompte\":1, \"description\":\"Alimentation\", \"categorie\":\"Courses\", \"dateOperation\":\"2025-09-21\"}";

			mockServer.expect(requestTo(BASE_URL + "/ligne"))
					.andExpect(method(HttpMethod.POST))
					.andRespond(withSuccess(jsonReponse, MediaType.APPLICATION_JSON));

			// WHEN
			Ligne resultat = ligneProxy.creerLigne(ligne);

			// THEN
			assertThat(resultat.getDateOperation().toString()).isEqualTo("2025-09-21");
			mockServer.verify();
		}

		@Test
		@DisplayName("La mise à jour d'une ligne appelle la bonne URL avec PUT et retourne la ligne mise à jour")
		public void mettreAJourLigne_doitAppelerApiAvecPutEtRetournerLaLigneMiseAJour() {
			// GIVEN
			Ligne ligne = creerLigne();
			ligne.setId(1L);	
			String jsonReponse = "{\"id\":1, \"numeroCompte\":1, \"description\":\"Alimentation\", \"categorie\":\"Courses\", \"dateOperation\":\"2025-09-21\"}";

			mockServer.expect(requestTo(BASE_URL + "/ligne/" + ligne.getId()))
					.andExpect(method(HttpMethod.PUT))
					.andRespond(withSuccess(jsonReponse, MediaType.APPLICATION_JSON));

			// WHEN
			Ligne resultat = ligneProxy.mettreAJourLigne(ligne);

			// THEN
			assertThat(resultat.getCategorie()).isEqualTo("Courses");
			mockServer.verify();
		}
	}
}
