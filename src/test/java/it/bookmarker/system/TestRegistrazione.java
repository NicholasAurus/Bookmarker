package it.bookmarker.system;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import it.bookmarker.system.utils.DatabaseTestHelper;


public class TestRegistrazione {

    private WebDriver driver;
    
    
    private final String EMAIL_NEW = "nuovo.registrato@test.com";
    
    
    private final String EMAIL_EXISTING = "n.grimaldi@test.com";

    
    private final String EMAIL_FOR_CF_TEST = "holder.cf@test.com"; 
    private final String EMAIL_TRY_CF = "n.grimaldi00@test.com";   
    private final String CF_DUPLICATO = "GRMNHL00A06I483K"; 

 
    private final String CF_GENERICO = "RSSMRA85T10A509Q"; 

    @BeforeEach
    public void setup() {
     
        DatabaseTestHelper.deleteUtente(EMAIL_NEW);
        DatabaseTestHelper.deleteUtente(EMAIL_TRY_CF);

        DatabaseTestHelper.createUtente(EMAIL_EXISTING, "LETTORE", "attivo");

        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testRegistrazioneSuccesso() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8080/BookMarker/");
        driver.findElement(By.linkText("Registrati")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nome")));

        driver.findElement(By.id("nome")).sendKeys("Mario");
        driver.findElement(By.id("cognome")).sendKeys("Rossi");
        driver.findElement(By.id("codice_fiscale")).sendKeys(CF_GENERICO);
        driver.findElement(By.id("email")).sendKeys(EMAIL_NEW);
        driver.findElement(By.id("password")).sendKeys("Password123!");
        driver.findElement(By.id("conferma_password")).sendKeys("Password123!");

        Select selectDomanda = new Select(driver.findElement(By.id("domanda")));
        selectDomanda.selectByIndex(1);
        driver.findElement(By.id("risposta")).sendKeys("RispostaSegreta");

        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("login"));

        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[@id='login-card']//div[contains(@class, 'alert') or contains(@class, 'success') or contains(@style, 'green')]")
        ));
        Assertions.assertTrue(successMessage.isDisplayed());
    }

    @Test
    public void testRegistrazioneEmailGiaPresente() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8080/BookMarker/");
        driver.findElement(By.linkText("Registrati")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nome")));

        driver.findElement(By.id("nome")).sendKeys("Nicholas");
        driver.findElement(By.id("cognome")).sendKeys("Grimaldi");
        driver.findElement(By.id("codice_fiscale")).sendKeys(CF_GENERICO); 
        driver.findElement(By.id("email")).sendKeys(EMAIL_EXISTING); 
        driver.findElement(By.id("password")).sendKeys("Password0!");
        driver.findElement(By.id("conferma_password")).sendKeys("Password0!");

        Select selectDomanda = new Select(driver.findElement(By.id("domanda")));
        selectDomanda.selectByIndex(1);
        driver.findElement(By.id("risposta")).sendKeys("maria");

        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[contains(@class, 'alert') or contains(@class, 'error') or contains(@class, 'danger')]")
        ));
        
        String errorText = errorMessage.getText().toLowerCase();
        System.out.println("Testo errore Email: " + errorText);
        
        Assertions.assertTrue(errorText.contains("esistente") || errorText.contains("presente") || 
                              errorText.contains("errore") || errorText.contains("uso") || errorText.contains("già"));
    }

    @Test
    public void testRegistrazioneCFGiaPresente() {
     
        DatabaseTestHelper.createUtente(EMAIL_FOR_CF_TEST, "LETTORE", "attivo", CF_DUPLICATO);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8080/BookMarker/");
        driver.findElement(By.linkText("Registrati")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nome")));

        driver.findElement(By.id("nome")).sendKeys("Nicholas");
        driver.findElement(By.id("cognome")).sendKeys("Grimaldi");
        
 
        driver.findElement(By.id("codice_fiscale")).sendKeys(CF_DUPLICATO);
        
     
        driver.findElement(By.id("email")).sendKeys(EMAIL_TRY_CF);
        
        driver.findElement(By.id("password")).sendKeys("Password0!");
        driver.findElement(By.id("conferma_password")).sendKeys("Password0!");

        Select selectDomanda = new Select(driver.findElement(By.id("domanda")));
        selectDomanda.selectByIndex(1);
        driver.findElement(By.id("risposta")).sendKeys("maria");

        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[contains(@class, 'alert') or contains(@class, 'error') or contains(@class, 'danger')]")
        ));
        
        String errorText = errorMessage.getText().toLowerCase();
        System.out.println("Testo errore CF: " + errorText);

        Assertions.assertTrue(errorText.contains("codice fiscale") || errorText.contains("esistente") || 
                              errorText.contains("presente") || errorText.contains("già"),
                              "Errore atteso sul CF non trovato. Testo: " + errorText);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
        
        DatabaseTestHelper.deleteUtente(EMAIL_NEW);
        DatabaseTestHelper.deleteUtente(EMAIL_EXISTING);
        DatabaseTestHelper.deleteUtente(EMAIL_FOR_CF_TEST);
        DatabaseTestHelper.deleteUtente(EMAIL_TRY_CF);
    }
}