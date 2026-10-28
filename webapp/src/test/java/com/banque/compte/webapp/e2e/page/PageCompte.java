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

public class PageCompte {

    @FindBy(id = "ajoutLigne")
    private WebElement lienAjouterLigne;
    
    @FindBy(id = "aucuneLigne")
    private WebElement messageAucuneLigne;

    @FindBy(className = "date")
    private List<WebElement> datesLignes;
    
    @FindBy(className = "description")
    private List<WebElement> descriptionsLignes;
  
    @FindBy(className = "categorie")
    private List<WebElement> categoriesLignes;
  
    @FindBy(className = "montant")
    private List<WebElement> montantsLignes;
  
    private final WebDriver webDriver;
    
    public PageCompte(WebDriver webDriver) {
    	this.webDriver = webDriver;
    	PageFactory.initElements(webDriver, this);
    }
    
    private void remplirFormulaire(String description, String categorie, String montant, String date) {
    	PageFormulaireLigne pageFormulaireLigne = new PageFormulaireLigne(webDriver);
    	pageFormulaireLigne.remplirChampDescription(description);
    	pageFormulaireLigne.remplirChampCategorie(categorie);
    	pageFormulaireLigne.remplirChampMontant(montant);
    	pageFormulaireLigne.remplirChampDate(date);
    	pageFormulaireLigne.valider();
    }
    
    private String trouverIdAPartirDescription(String description) {
		final WebElement ligne = webDriver.findElement(By.xpath("//tr[.//span[@class='description' and text()='" + description + "']]"));
		WebElement lienModifier = ligne.findElement(By.cssSelector("a[data-testid^='modifier-']"));
		String testId = lienModifier.getDomAttribute("data-testid");
		String id = testId.replace("modifier-", "");
		return id;
	}
    
    private void attenteAffichagePageActualisee(){
    	final WebDriverWait waiter = new WebDriverWait(webDriver, Duration.ofSeconds(10));
    	waiter.until(ExpectedConditions.visibilityOfAllElements(datesLignes));
    }
    
    public List<String> lireDatesLignes() {
    	return datesLignes.stream().map(WebElement::getText).toList();
    }
    
    public List<String> lireDescriptionsLignes() {
    	return descriptionsLignes.stream().map(WebElement::getText).toList();
    }
    
    public List<String> lireCategoriesLignes() {
    	return categoriesLignes.stream().map(WebElement::getText).toList();
    }
    
    public List<String> lireMontantsLignes() {
    	return montantsLignes.stream().map(WebElement::getText).toList();
    }
    
    public boolean aucunLigneAffichee() {
    	return datesLignes.isEmpty();
    }
    
    public void ajouterLigne(String description, String categorie, String montant, String date) {
    	lienAjouterLigne.click();
    	remplirFormulaire (description, categorie, montant, date);
    	attenteAffichagePageActualisee();
    }
    
    public void modifierLigneParId(String id, String description, String categorie, String montant, String date) {
       	final WebElement boutonModifierLigne = webDriver.findElement(By.cssSelector("[data-testid='modifier-" + id + "']"));
    	boutonModifierLigne.click();
    	remplirFormulaire (description, categorie, montant, date);
    	attenteAffichagePageActualisee();
    }
    
    public void modifierLigneParDescription(String descriptionLigne, String description, String categorie, String montant, String date) {
		String id = trouverIdAPartirDescription(descriptionLigne);
		modifierLigneParId(id, description, categorie, montant, date);
    }
    
    public void supprimerLigneParId(String id) {
    	final WebElement boutonSupprimerLigne = webDriver.findElement(By.cssSelector("[data-testid='supprimer-" + id + "']"));
    	boutonSupprimerLigne.click();
    	final PagePopUpConfirmation pagePopUpConfirmation = new PagePopUpConfirmation(webDriver);
    	pagePopUpConfirmation.confirmer();
    	final WebDriverWait waiter = new WebDriverWait(webDriver, Duration.ofSeconds(10));
    	waiter.until(ExpectedConditions.stalenessOf(boutonSupprimerLigne));
    	waiter.until(ExpectedConditions.or(
    		    ExpectedConditions.visibilityOfAllElements(datesLignes),
    		    ExpectedConditions.visibilityOf(messageAucuneLigne)));
    }
    
    public void supprimerLigneParDescription(String descriptionLigne) {
    	String id = trouverIdAPartirDescription(descriptionLigne);
    	supprimerLigneParId(id);
    }
}
