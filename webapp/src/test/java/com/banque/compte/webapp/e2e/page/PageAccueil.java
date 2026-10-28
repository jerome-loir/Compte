package com.banque.compte.webapp.e2e.page;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PageAccueil {

    @FindBy(id = "ajoutCompte")
    private WebElement lienAjouterCompte;
    
    @FindBy(id = "aucunCompte")
    private WebElement messageAucunCompte;
    
    @FindBy(className = "nom")
    private List<WebElement> nomsComptes;
    
    private final WebDriver webDriver;
    
    public PageAccueil(WebDriver webDriver) {
    	this.webDriver = webDriver;
    	PageFactory.initElements(webDriver, this);
    }
    
    private void remplirFormulaire(String nom, String prenom, String mail) {
    	PageFormulaireCompte pageFormulaireCompte = new PageFormulaireCompte(webDriver);
    	pageFormulaireCompte.remplirChampNom(nom);
    	pageFormulaireCompte.remplirChampPrenom(prenom);
    	pageFormulaireCompte.remplirChampMail(mail);
    	pageFormulaireCompte.valider();
    }
    
    private void attenteAffichagePageActualisee(){
    	final WebDriverWait waiter = new WebDriverWait(webDriver, Duration.ofSeconds(10));
    	waiter.until(ExpectedConditions.visibilityOfAllElements(nomsComptes));
    }
    
    public String trouverNumeroCompteAPartirDuNom(String nomCompte) {
 		final WebElement ligneCompte = webDriver.findElement(By.xpath("//tr[.//span[@class='nom' and text()='" + nomCompte.toUpperCase() + "']]"));
 		WebElement lienOperations = ligneCompte.findElement(By.cssSelector("a[data-testid^='operations-']"));
 		String testId = lienOperations.getDomAttribute("data-testid");
 		String numeroCompte = testId.replace("operations-", "");
 		return numeroCompte;
 	}
    
    public List<String> lireNomsComptes() {
    	return nomsComptes.stream().map(WebElement::getText).toList();
    }
    
    public boolean aucunCompteAffiche() {
    	return nomsComptes.isEmpty();
    }
    
    public void ajouterCompte(String nom, String prenom, String mail) {
    	lienAjouterCompte.click();
    	remplirFormulaire (nom, prenom, mail);
    	attenteAffichagePageActualisee();
    }
  
    public void afficherPageCompte(String numeroCompte) {
    	WebElement lienOperations = webDriver.findElement(By.cssSelector("[data-testid='operations-" + numeroCompte + "']"));
    	lienOperations.click();
    }
    
    public void modifierCompteParNumeroCompte(String numeroCompte, String nom, String prenom, String mail) {
       	final WebElement boutonModifierCompte = webDriver.findElement(By.cssSelector("[data-testid='modifier-" + numeroCompte + "']"));
    	boutonModifierCompte.click();
    	remplirFormulaire (nom, prenom, mail);
    	attenteAffichagePageActualisee();
    }
    
    public void modifierCompteParNomCompte(String nomCompte, String nom, String prenom, String mail) {
		String numeroCompte = trouverNumeroCompteAPartirDuNom(nomCompte);
		modifierCompteParNumeroCompte(numeroCompte, nom, prenom, mail);
    }
    
    public void supprimerCompteParNumeroCompte(String numeroCompte) {
    	final WebElement boutonSupprimerCompte = webDriver.findElement(By.cssSelector("[data-testid='supprimer-" + numeroCompte + "']"));
    	boutonSupprimerCompte.click();
    	final PagePopUpConfirmation pagePopUpConfirmation = new PagePopUpConfirmation(webDriver);
    	pagePopUpConfirmation.confirmer();
    	final WebDriverWait waiter = new WebDriverWait(webDriver, Duration.ofSeconds(10));
    	waiter.until(ExpectedConditions.stalenessOf(boutonSupprimerCompte));
    	waiter.until(ExpectedConditions.or(
    		    ExpectedConditions.visibilityOfAllElements(nomsComptes),
    		    ExpectedConditions.visibilityOf(messageAucunCompte)));
    }
    
    public void supprimerCompteParNomCompte(String nomCompte) {
    	String numeroCompte = trouverNumeroCompteAPartirDuNom(nomCompte);
    	supprimerCompteParNumeroCompte(numeroCompte);
    }
}
