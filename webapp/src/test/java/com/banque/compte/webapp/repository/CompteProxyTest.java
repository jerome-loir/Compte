package com.banque.compte.webapp.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

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

@ExtendWith(MockitoExtension.class)
@DisplayName("CompteProxy")
public class CompteProxyTest {
	
	public static final String BASE_URL = "http://localhost:9001";
	private	RestTemplate restTemplate;
	private MockRestServiceServer mockServer;
	private CompteProxy compteProxy;
	
	@Mock
	private CustomProperties props;
	
	private Compte creerCompte() {
		Compte compte = new Compte();
		compte.setNom("MARTIN");
		compte.setPrenom("Michel");
		compte.setMail("michel@test.fr");
		return compte;
	}

	@BeforeEach
	public void setUp() {
		restTemplate = new RestTemplate();
		mockServer = MockRestServiceServer.createServer(restTemplate);
		compteProxy = new CompteProxy(props, restTemplate);
		
		when(props.getApiUrl()).thenReturn(BASE_URL);
	}
	
	@Test
	@DisplayName("La recherche de tous les comptes appelle la bonne URL et retourne la liste des comptes reçue")
	public void rechercherComptes_doitAppelerApiAvecGetEtRetournerListeComptes() {
		// GIVEN
		String jsonReponse = "[{\"numeroCompte\":1, \"nom\":\"DUPONT\", \"prenom\":\"Jean\", \"mail\":\"jean@test.fr\"},{\"numeroCompte\":2, \"nom\":\"DURAND\", \"prenom\":\"Patrick\", \"mail\":\"patrick@test.fr\"}]";
		
		mockServer.expect(requestTo(BASE_URL + "/comptes"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(jsonReponse, MediaType.APPLICATION_JSON));
		
		// WHEN
		List<Compte> resultat = (List<Compte>) compteProxy.rechercherComptes();
		
		// THEN
		assertThat(resultat).hasSize(2);
		assertThat(resultat.get(0).getMail()).isEqualTo("jean@test.fr");
		assertThat(resultat.get(1).getMail()).isEqualTo("patrick@test.fr");
		mockServer.verify();
	}
	
	@Test
	@DisplayName("La recherche d'un compte appelle la bonne URL et retourne le compte reçu")
	public void rechercherCompte_doitAppelerApiAvecGetEtRetournerLeCompte() {
		// GIVEN
		int id = 1;
		String jsonReponse = "{\"numeroCompte\":1, \"nom\":\"DUPONT\", \"prenom\":\"Jean\", \"mail\":\"jean@test.fr\"}";
		
		mockServer.expect(requestTo(BASE_URL + "/compte/" +id))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(jsonReponse, MediaType.APPLICATION_JSON));

		// WHEN
		Compte resultat = compteProxy.rechercherCompte(id);
		
		// THEN
		assertThat(resultat.getNom()).isEqualTo("DUPONT");
		mockServer.verify();
	}

	@Test
	@Tag("suppression")
	@DisplayName("La suppression d'un compte appelle la bonne URL avec DELETE")
	public void supprimerCompte_doitAppelerApiAvecDelete() {
		// GIVEN
		int id = 1;
		
		mockServer.expect(requestTo(BASE_URL + "/compte/" + id))
				.andExpect(method(HttpMethod.DELETE))
				.andRespond(withSuccess());
		
		// WHEN
		compteProxy.supprimerCompte(id);
		
		// THEN
		mockServer.verify();
	}

	@Nested
	@Tag("ecriture")
	@DisplayName("Sauvegarde d'un compte")
	class SauvegardeCompteTest {

		@Test
		@DisplayName("La création d'un compte appelle la bonne URL avec POST et retourne le compte créé")
		public void creerCompte_doitAppelerApiAvecPostEtRetournerLeCompteCree() {
			// GIVEN
			Compte compte = creerCompte();
			String jsonReponse = "{\"numeroCompte\":3, \"nom\":\"MARTIN\", \"prenom\":\"Michel\", \"mail\":\"michel@test.fr\"}";

			mockServer.expect(requestTo(BASE_URL + "/compte"))
					.andExpect(method(HttpMethod.POST))
					.andRespond(withSuccess(jsonReponse, MediaType.APPLICATION_JSON));

			// WHEN
			Compte resultat = compteProxy.creerCompte(compte);

			// THEN
			assertThat(resultat.getPrenom()).isEqualTo("Michel");
			mockServer.verify();
		}

		@Test
		@DisplayName("La mise à jour d'un compte appelle la bonne URL avec PUT et retourne le compte mis à jour")
		public void mettreAJourCompte_doitAppelerApiAvecPutEtRetournerLeCompteMisAJour() {
			// GIVEN
			Compte compte = creerCompte();
			compte.setNumeroCompte(3L);
			String jsonReponse = "{\"numeroCompte\":3, \"nom\":\"MARTIN\", \"prenom\":\"Michel\", \"mail\":\"michel@test.fr\"}";

			mockServer.expect(requestTo(BASE_URL + "/compte/" + compte.getNumeroCompte()))
					.andExpect(method(HttpMethod.PUT))
					.andRespond(withSuccess(jsonReponse, MediaType.APPLICATION_JSON));

			// WHEN
			Compte resultat = compteProxy.mettreAJourCompte(compte);

			// THEN
			assertThat(resultat.getNom()).isEqualTo("MARTIN");
			mockServer.verify();
		}
	}
}
