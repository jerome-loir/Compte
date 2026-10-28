package com.banque.compte.webapp.e2e.page;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PagePopUpConfirmation {
	
    @FindBy(className = "swal2-confirm")
    private WebElement boutonConfirmer;
    
    private final WebDriver webDriver;
    
    public PagePopUpConfirmation(WebDriver webDriver) {
    	this.webDriver = webDriver;
    	PageFactory.initElements(webDriver, this);
    }
    
    public void confirmer() {
    	final WebDriverWait waiter = new WebDriverWait(webDriver, Duration.ofSeconds(10));
		waiter.until(ExpectedConditions.elementToBeClickable(boutonConfirmer));
		boutonConfirmer.click();
    }

}
