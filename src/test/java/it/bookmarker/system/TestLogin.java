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

public class TestLogin {

    private WebDriver driver;
    
    private final String EMAIL_OK = "mario.login@test.com";
    private final String EMAIL_ATTESA = "mario.attesa@test.com";
    private final String EMAIL_NOT_FOUND = "mario.inesistente@test.com"; 
    private final String PASSWORD_CHIARA = "Password123!"; 

    @BeforeEach
    public void setup() {
        DatabaseTestHelper.createUtente(EMAIL_OK, "LETTORE", "attivo");
        DatabaseTestHelper.createUtente(EMAIL_ATTESA, "LETTORE", "in_attesa");
        
        DatabaseTestHelper.deleteUtente(EMAIL_NOT_FOUND);

        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testLoginSuccesso() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8080/BookMarker/login.jsp");

        driver.findElement(By.id("email")).sendKeys(EMAIL_OK);
        driver.findElement(By.id("password")).sendKeys(PASSWORD_CHIARA);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("index.jsp"));
        
        WebElement userGreeting = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("user-greeting")));
        
        Assertions.assertTrue(userGreeting.getText().contains("Test"), 
                "Il messaggio di benvenuto non contiene il nome utente corretto.");
        
        WebElement btnLogout = driver.findElement(By.xpath("//a[contains(text(), 'Logout')]"));
        Assertions.assertTrue(btnLogout.isDisplayed());
    }

    @Test
    public void testLogin_UtenteInAttesa() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8080/BookMarker/login.jsp");
        
        driver.findElement(By.id("email")).sendKeys(EMAIL_ATTESA);
        driver.findElement(By.id("password")).sendKeys(PASSWORD_CHIARA);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebElement errorDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[@id='login-card']//div[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'attesa')]")
        ));

        String testoErrore = errorDiv.getText().toLowerCase();

        Assertions.assertTrue(testoErrore.contains("attesa") || testoErrore.contains("non attivo"), 
                "Messaggio di errore non trovato. Testo: " + testoErrore);
                
        Assertions.assertTrue(driver.getCurrentUrl().contains("login") || driver.getCurrentUrl().contains("LoginServlet"));
    }

    @Test
    public void testLogin_EmailNonTrovata() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8080/BookMarker/login.jsp");

        driver.findElement(By.id("email")).sendKeys(EMAIL_NOT_FOUND);
        driver.findElement(By.id("password")).sendKeys("QualsiasiPassword");
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebElement errorDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[@id='login-card']//div[contains(@class, 'alert') or contains(@class, 'error') or contains(@style, 'color')]")
        ));

        String testoErrore = errorDiv.getText().toLowerCase();

        Assertions.assertTrue(testoErrore.contains("email") || 
                              testoErrore.contains("password") || 
                              testoErrore.contains("validi"), 
                "Il messaggio di errore per email non trovata non è corretto. Testo trovato: " + testoErrore);

        Assertions.assertTrue(driver.getCurrentUrl().contains("login") || driver.getCurrentUrl().contains("LoginServlet"));
    }

    @Test
    public void testLogin_PasswordErrata() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8080/BookMarker/login.jsp");

        driver.findElement(By.id("email")).sendKeys(EMAIL_OK);
        driver.findElement(By.id("password")).sendKeys("PasswordSBAGLIATA!"); 
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebElement errorDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[@id='login-card']//div[contains(@class, 'alert') or contains(@class, 'error') or contains(@style, 'color')]")
        ));

        String testoErrore = errorDiv.getText().toLowerCase();

        Assertions.assertTrue(testoErrore.contains("email") || 
                testoErrore.contains("password") || 
                testoErrore.contains("validi"), 
                "Il messaggio di errore per password errata non è corretto. Testo trovato: " + testoErrore);

        Assertions.assertTrue(driver.getCurrentUrl().contains("login") || driver.getCurrentUrl().contains("LoginServlet"));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
        
        DatabaseTestHelper.deleteUtente(EMAIL_OK);
        DatabaseTestHelper.deleteUtente(EMAIL_ATTESA);
        DatabaseTestHelper.deleteUtente(EMAIL_NOT_FOUND);
    }
}