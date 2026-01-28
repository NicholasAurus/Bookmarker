package it.bookmarker.system;


import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import it.bookmarker.system.utils.DatabaseTestHelper;

public class TestApprovazioneUtente {

    private WebDriver driver;
    
    private final String EMAIL_ADMIN = "n.bibliotecario@gmail.com";
    private final String PASSWORD = "Password123!";
    
    private final String EMAIL_TARGET = "utente.attesa@test.com";

    @BeforeEach
    public void setup() {
        DatabaseTestHelper.createUtente(EMAIL_ADMIN, "BIBLIOTECARIO", "attivo");
        DatabaseTestHelper.createUtente(EMAIL_TARGET, "LETTORE", "in_attesa");
        
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testApprovazioneUtenteSuccesso() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        driver.get("http://localhost:8080/BookMarker/login.jsp");
        driver.findElement(By.id("email")).sendKeys(EMAIL_ADMIN);
        driver.findElement(By.id("password")).sendKeys(PASSWORD);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(), 'Logout')]")));

        driver.get("http://localhost:8080/BookMarker/GestioneUtentiServlet");

        try {
            WebElement searchInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[@type='search' or contains(@class, 'search') or @aria-controls]")
            ));
            if (searchInput.isDisplayed()) {
                searchInput.clear();
                searchInput.sendKeys(EMAIL_TARGET);
                Thread.sleep(1000); 
            }
        } catch (Exception e) {
        }

        WebElement rigaUtente = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//tr[contains(., '" + EMAIL_TARGET + "')]")
        ));
        
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", rigaUtente);
        
        WebElement btnApprova = rigaUtente.findElement(By.xpath(".//button[contains(@class, 'btn') or @type='button']"));
        
        wait.until(ExpectedConditions.elementToBeClickable(btnApprova));
        js.executeScript("arguments[0].click();", btnApprova);

        WebElement btnConfirm = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmBtn")));
        wait.until(ExpectedConditions.elementToBeClickable(btnConfirm));
        
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        js.executeScript("arguments[0].click();", btnConfirm);

        try {
            wait.until(ExpectedConditions.invisibilityOf(btnConfirm));
        } catch (Exception e) {
        }

        WebElement msgDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.className("toast-message")
        ));
        
        wait.until(ExpectedConditions.textToBePresentInElement(msgDiv, "attivato"));
        
        String toastText = msgDiv.getText().toLowerCase();
        
        Assertions.assertTrue(
            toastText.contains("attivato") || 
            toastText.contains("successo") || 
            toastText.contains("approvato"),
            "Il messaggio non è corretto. Testo trovato: [" + toastText + "]"
        );
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
        
        DatabaseTestHelper.deleteUtente(EMAIL_ADMIN);
        DatabaseTestHelper.deleteUtente(EMAIL_TARGET);
    }
}