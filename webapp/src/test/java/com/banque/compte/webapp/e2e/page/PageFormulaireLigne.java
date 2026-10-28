package com.banque.compte.webapp.e2e.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class PageFormulaireLigne {

	   @FindBy(id = "descriptionInput")
	    private WebElement champDescription;
	    
	    @FindBy(id = "categorieInput")
	    private WebElement champCategorie;

	    @FindBy(id = "montantInput")
	    private WebElement champMontant;
	    
	    @FindBy(id = "dateInput")
	    private WebElement champDate;
	    
	    @FindBy(id = "boutonValider")
	    private WebElement boutonValider;
	    
	    @FindBy(id = "boutonAnnuler")
	    private WebElement boutonAnnuler;
	    
	    public PageFormulaireLigne(WebDriver webDriver) {
	    	PageFactory.initElements(webDriver, this);
	    }
	    
	    public void remplirChampDescription(String description) {
			if (description != null && !description.isBlank()) {
				champDescription.clear();
				champDescription.sendKeys(description);
		    }
	    }
	    
	    public void remplirChampCategorie(String categorie) {
			if (categorie != null && !categorie.isBlank()) {
				champCategorie.clear();
				champCategorie.sendKeys(categorie);
			}
	    }
	    
	    public void remplirChampMontant(String montant) {
			if (montant != null && !montant.isBlank()) {
				champMontant.clear();
				champMontant.sendKeys(montant);
			}
	    }
	    
	    public void remplirChampDate(String date) {
			if (date != null && !date.isBlank()) {
				champDate.clear();
				champDate.sendKeys(date);
			}
	    }
	    
	    public void valider() {
	    	boutonValider.click();
	    }
	    
	    public void annuler() {
	    	boutonAnnuler.click();
	    }
}
