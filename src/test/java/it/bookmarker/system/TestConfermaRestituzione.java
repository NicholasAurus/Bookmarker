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

public class TestConfermaRestituzione {

    private WebDriver driver;
    
    private final String EMAIL_ADMIN = "n.bibliotecario@gmail.com";
    private final String PASSWORD = "Password123!";
    
    private final String EMAIL_USER = "mario.restituzione@test.com";
    private final int ID_LIBRO = 900;
    private final String TITOLO_LIBRO = "Libro da Restituire";

    @BeforeEach
    public void setup() {
        DatabaseTestHelper.createUtente(EMAIL_ADMIN, "BIBLIOTECARIO", "attivo");
        DatabaseTestHelper.createUtente(EMAIL_USER, "LETTORE", "attivo");
        DatabaseTestHelper.createLibro(ID_LIBRO, TITOLO_LIBRO, 5);
        
        DatabaseTestHelper.createPrestito(EMAIL_USER, ID_LIBRO, "In Corso");
        
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testConfermaRestituzioneSuccesso() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        driver.get("http://localhost:8080/BookMarker/login.jsp");
        driver.findElement(By.id("email")).sendKeys(EMAIL_ADMIN);
        driver.findElement(By.id("password")).sendKeys(PASSWORD);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(), 'Logout')]")));

        driver.get("http://localhost:8080/BookMarker/GestionePrestitiServlet");

        WebElement tabInCorso = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(@class, 'tab-button') and contains(., 'In Corso')]")
        ));
        js.executeScript("arguments[0].click();", tabInCorso);
        
        wait.until(ExpectedConditions.attributeContains(By.id("tabAttivi"), "class", "active"));

        WebElement rigaPrestito = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='tabAttivi']//tr[contains(., '" + EMAIL_USER + "')]")
        ));
        
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", rigaPrestito);
        
        WebElement btnRestituzione = rigaPrestito.findElement(By.xpath(".//button[contains(@class, 'btn-blue') or contains(., 'Restituzione')]"));
        
        wait.until(ExpectedConditions.elementToBeClickable(btnRestituzione));
        js.executeScript("arguments[0].click();", btnRestituzione);

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmationModal")));
        WebElement btnConfirm = modal.findElement(By.id("confirmBtn"));
        
        wait.until(ExpectedConditions.elementToBeClickable(btnConfirm));
        
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        js.executeScript("arguments[0].click();", btnConfirm);

        try { wait.until(ExpectedConditions.invisibilityOf(modal)); } catch (Exception e) {}

        WebElement msgDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.className("toast-message")
        ));
        
        String toastText = msgDiv.getText().toLowerCase();
        
        Assertions.assertTrue(
            toastText.contains("successo") || 
            toastText.contains("restituit") || 
            toastText.contains("registrato"),
            "Il messaggio di conferma restituzione non è corretto. Testo trovato: [" + toastText + "]"
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