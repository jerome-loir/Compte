package com.banque.compte.webapp.e2e.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class PageFormulaireCompte {

	   @FindBy(id = "nomInput")
	    private WebElement champNom;
	    
	    @FindBy(id = "prenomInput")
	    private WebElement champPrenom;

	    @FindBy(id = "mailInput")
	    private WebElement champMail;
	    
	    @FindBy(id = "boutonValider")
	    private WebElement boutonValider;
	    
	    @FindBy(id = "boutonAnnuler")
	    private WebElement boutonAnnuler;
	    
	    
	    public PageFormulaireCompte(WebDriver webDriver) {
	    	PageFactory.initElements(webDriver, this);
	    }
	    
		public void remplirChampNom(String nom) {
			if (nom != null && !nom.isBlank()) {
				champNom.clear();
				champNom.sendKeys(nom);
			}
		}
	    
	    public void remplirChampPrenom(String prenom) {
	    	if (prenom != null && !prenom.isBlank()) {
	    		champPrenom.clear();
	    		champPrenom.sendKeys(prenom);
			}
	    }
	    
	    public void remplirChampMail(String mail) {
	    	if (mail != null && !mail.isBlank()) {
	    		champMail.clear();
	    		champMail.sendKeys(mail);
			}
	    }
	    
	    public void valider() {
	    	boutonValider.click();
	    }
	    
	    public void annuler() {
	    	boutonAnnuler.click();
	    }
}
