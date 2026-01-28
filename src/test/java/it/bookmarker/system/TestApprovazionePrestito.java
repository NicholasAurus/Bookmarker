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

public class TestApprovazionePrestito {

    private WebDriver driver;
    

    private final String EMAIL_ADMIN = "n.bibliotecario@gmail.com";
    private final String PASS_ADMIN = "password"; 


    private final String EMAIL_UTENTE = "mario.prestito@test.com";
    private final int ID_LIBRO = 600;

    @BeforeEach
    public void setup() {

        DatabaseTestHelper.createUtente(EMAIL_UTENTE, "LETTORE", "attivo");

        DatabaseTestHelper.createLibro(ID_LIBRO, "Libro Da Approvare", 5);

        DatabaseTestHelper.createPrestito(EMAIL_UTENTE, ID_LIBRO, "Richiesto");


        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    
    @Test
    public void testApprovaPrenotazione_Successo() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8080/BookMarker/login.jsp");
        driver.findElement(By.id("email")).sendKeys(EMAIL_ADMIN);
        driver.findElement(By.id("password")).sendKeys(PASS_ADMIN);
        driver.findElement(By.xpath("//button[@type='submit']")).click();


        driver.get("http://localhost:8080/BookMarker/GestioneUtentiServlet");

        WebElement tabPrestiti = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@onclick, 'prestiti')]")));
        tabPrestiti.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tabPrestiti")));

        WebElement btnApprova = driver.findElement(By.xpath("//tr[contains(., '" + EMAIL_UTENTE + "')]//button"));
        btnApprova.click();


        WebElement btnConferma = wait.until(ExpectedConditions.elementToBeClickable(By.id("confirmBtn")));
        btnConferma.click();

        wait.until(ExpectedConditions.invisibilityOf(btnConferma));


        WebElement toastSuccesso = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//*[contains(@class, 'success') or contains(@class, 'toast')]")
        ));
        
        Assertions.assertTrue(toastSuccesso.getText().toLowerCase().contains("success") || 
                              toastSuccesso.getText().toLowerCase().contains("approvat"), 
                "Il messaggio di successo non è apparso. Testo: " + toastSuccesso.getText());
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();

        DatabaseTestHelper.deleteUtente(EMAIL_UTENTE);
        DatabaseTestHelper.deleteLibro(ID_LIBRO);
    }
}