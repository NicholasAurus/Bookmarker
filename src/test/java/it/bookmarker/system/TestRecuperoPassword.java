package it.bookmarker.system;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import it.bookmarker.system.utils.DatabaseTestHelper;

public class TestRecuperoPassword {

	
    private WebDriver driver;
    
    private final String EMAIL_TEST = "recupero.password@test.com";
    private final String EMAIL_NOT_FOUND = "email.finta@test.com";
    private final String NUOVA_PASSWORD_OK = "PasswordNuova123!";
    private final String PASSWORD_DEBOLE = "password"; 

    @BeforeEach
    public void setup() {
        DatabaseTestHelper.createUtente(EMAIL_TEST, "LETTORE", "attivo");
        DatabaseTestHelper.deleteUtente(EMAIL_NOT_FOUND);

        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testRecuperoPasswordSuccesso() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8080/BookMarker/login.jsp");
        
        driver.findElement(By.linkText("Hai dimenticato la password?")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
        driver.findElement(By.name("email")).sendKeys(EMAIL_TEST);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("risposta")));
        driver.findElement(By.name("risposta")).sendKeys("RispostaTest");
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
        driver.findElement(By.name("password")).sendKeys(NUOVA_PASSWORD_OK);
        driver.findElement(By.name("conferma_password")).sendKeys(NUOVA_PASSWORD_OK);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("login.jsp"));

        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[@id='login-card']//div[contains(@class, 'alert') or contains(@class, 'success') or contains(text(), 'successo')]")
        ));
        
        Assertions.assertTrue(successMessage.isDisplayed());
    }

    @Test
    public void testRecuperoPassword_EmailNonTrovata() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8080/BookMarker/login.jsp");
        
        driver.findElement(By.linkText("Hai dimenticato la password?")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
        driver.findElement(By.name("email")).sendKeys(EMAIL_NOT_FOUND);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[contains(@class, 'alert') or contains(@class, 'error') or contains(@class, 'danger')]")
        ));

        String errorText = errorMessage.getText().toLowerCase();
        
        Assertions.assertTrue(errorText.contains("non trovata") || errorText.contains("inesistente") || errorText.contains("errore"),
            "Il messaggio di errore non è corretto. Testo trovato: " + errorText);
    }

    @Test
    public void testRecuperoPassword_RispostaSbagliata() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8080/BookMarker/login.jsp");
        
        driver.findElement(By.linkText("Hai dimenticato la password?")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
        driver.findElement(By.name("email")).sendKeys(EMAIL_TEST);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("risposta")));
        driver.findElement(By.name("risposta")).sendKeys("RispostaSbagliata");
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[contains(@class, 'alert') or contains(@class, 'error') or contains(@class, 'danger')]")
        ));

        String errorText = errorMessage.getText().toLowerCase();
        
        Assertions.assertTrue(errorText.contains("errata") || errorText.contains("sbagliata") || errorText.contains("non corretta"),
            "Il messaggio di errore per risposta sbagliata non è corretto. Testo trovato: " + errorText);
    }

    @Test
    public void testRecuperoPassword_FormatoSbagliato() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8080/BookMarker/login.jsp");
        
        driver.findElement(By.linkText("Hai dimenticato la password?")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
        driver.findElement(By.name("email")).sendKeys(EMAIL_TEST);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("risposta")));
        driver.findElement(By.name("risposta")).sendKeys("RispostaTest");
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
        
        driver.findElement(By.name("password")).sendKeys(PASSWORD_DEBOLE);
        driver.findElement(By.name("conferma_password")).sendKeys(PASSWORD_DEBOLE);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[contains(@class, 'alert') or contains(@class, 'error') or contains(@class, 'danger')]")
        ));

        String errorText = errorMessage.getText().toLowerCase();
        
        Assertions.assertTrue(
            errorText.contains("formato") || 
            errorText.contains("caratteri") || 
            errorText.contains("valida") || 
            errorText.contains("debole") ||
            errorText.contains("requisiti"),
            "Il messaggio di errore per password debole non è corretto. Testo trovato: " + errorText
        );
    }

    @Test
    public void testRecuperoPassword_PasswordNonCorrispondono() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8080/BookMarker/login.jsp");
        
        driver.findElement(By.linkText("Hai dimenticato la password?")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
        driver.findElement(By.name("email")).sendKeys(EMAIL_TEST);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("risposta")));
        driver.findElement(By.name("risposta")).sendKeys("RispostaTest");
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
        
        driver.findElement(By.name("password")).sendKeys("PasswordA123!");
        driver.findElement(By.name("conferma_password")).sendKeys("PasswordB456!");
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[contains(@class, 'alert') or contains(@class, 'error') or contains(@class, 'danger')]")
        ));

        String errorText = errorMessage.getText().toLowerCase();
        
        Assertions.assertTrue(
            errorText.contains("corrispondono") || 
            errorText.contains("uguali") || 
            errorText.contains("coincidono") ||
            errorText.contains("diverse"),
            "Il messaggio di errore per password diverse non è corretto. Testo trovato: " + errorText
        );
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
        
        DatabaseTestHelper.deleteUtente(EMAIL_TEST);
        DatabaseTestHelper.deleteUtente(EMAIL_NOT_FOUND);
    }
}