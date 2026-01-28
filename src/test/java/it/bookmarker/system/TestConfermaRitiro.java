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

public class TestConfermaRitiro {

    private WebDriver driver;
    
    private final String EMAIL_ADMIN = "n.bibliotecario@gmail.com";
    private final String PASSWORD = "Password123!";
    
    private final String EMAIL_USER = "mario.ritiro@test.com";
    private final int ID_LIBRO = 800;
    private final String TITOLO_LIBRO = "Libro da Ritirare";

    @BeforeEach
    public void setup() {
        DatabaseTestHelper.createUtente(EMAIL_ADMIN, "BIBLIOTECARIO", "attivo");
        DatabaseTestHelper.createUtente(EMAIL_USER, "LETTORE", "attivo");
        DatabaseTestHelper.createLibro(ID_LIBRO, TITOLO_LIBRO, 5);
        
        DatabaseTestHelper.createPrestito(EMAIL_USER, ID_LIBRO, "Approvato");
        
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testConfermaRitiroSuccesso() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        driver.get("http://localhost:8080/BookMarker/login.jsp");
        driver.findElement(By.id("email")).sendKeys(EMAIL_ADMIN);
        driver.findElement(By.id("password")).sendKeys(PASSWORD);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(), 'Logout')]")));

        driver.get("http://localhost:8080/BookMarker/GestionePrestitiServlet");

        WebElement tabApprovati = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//ul[contains(@class, 'nav')]//a[contains(text(), 'Approvati') or contains(text(), 'Da Ritirare') or contains(@href, 'Approvati')] | //button[contains(text(), 'Approvati')]")
        ));
        js.executeScript("arguments[0].click();", tabApprovati);
        
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        try {
            WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'tab-pane') and contains(@class, 'active')]//input[@type='search'] | //input[@type='search']")
            ));
            searchInput.clear();
            searchInput.sendKeys(EMAIL_USER);
            Thread.sleep(1000); 
        } catch (Exception e) {
        }

        WebElement rigaPrestito = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//tr[contains(., '" + EMAIL_USER + "')]")
        ));
        
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", rigaPrestito);
        
        WebElement btnRitiro = rigaPrestito.findElement(By.xpath(".//button[contains(@class, 'btn') or contains(., 'Ritiro') or @type='button']"));
        
        wait.until(ExpectedConditions.elementToBeClickable(btnRitiro));
        js.executeScript("arguments[0].click();", btnRitiro);

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
        
        String toastText = msgDiv.getText().toLowerCase();
        
        Assertions.assertTrue(
            toastText.contains("successo") || 
            toastText.contains("registrato") || 
            toastText.contains("avviato") ||
            toastText.contains("ok") || 
            toastText.contains("approvato"),
            "Il messaggio di conferma ritiro non è corretto. Testo trovato: [" + toastText + "]"
        );
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
        
        DatabaseTestHelper.deleteUtente(EMAIL_ADMIN);
        DatabaseTestHelper.deleteUtente(EMAIL_USER);
        DatabaseTestHelper.deleteLibro(ID_LIBRO);
    }
}