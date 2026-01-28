package it.bookmarker.system;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import it.bookmarker.system.utils.DatabaseTestHelper;

import java.time.Duration;

public class TestPrenotazioneLibro {

    private WebDriver driver;
    

    private final String EMAIL_TEST = "mario.prenotazione@test.com";
    private final int ID_LIBRO = 500; 

    @BeforeEach
    public void setup() {
  
        DatabaseTestHelper.createUtente(EMAIL_TEST, "LETTORE", "attivo");
        DatabaseTestHelper.createLibro(ID_LIBRO, "Libro Test Prenotazione", 5);


        io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
        
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testPrenotazioneLibro_Successo() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

   
        driver.get("http://localhost:8080/BookMarker/login.jsp");
        driver.findElement(By.id("email")).sendKeys(EMAIL_TEST);
        driver.findElement(By.id("password")).sendKeys("Password123!"); 
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        driver.get("http://localhost:8080/BookMarker/DettaglioLibroServlet?id=" + ID_LIBRO);


        WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataRitiro")));
        Select selectDate = new Select(selectElement);

        selectDate.selectByIndex(1); 


        WebElement btnPrenota = driver.findElement(By.xpath("//button[contains(text(), 'Prenota')]"));
        btnPrenota.click();


        WebElement btnConferma = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@onclick='confermaInvio()']")));
        btnConferma.click();

        WebElement toastSuccesso = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("success-message"))); 

        
        Assertions.assertTrue(toastSuccesso.getText().toLowerCase().contains("success"), 
            "Il messaggio non indica successo. Testo trovato: " + toastSuccesso.getText());
    }
    @Test
    public void testPrenotazioneLibro_GiaPrenotato() {
 
        DatabaseTestHelper.createPrestito(EMAIL_TEST, ID_LIBRO, "Richiesto");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

     
        driver.get("http://localhost:8080/BookMarker/login.jsp");
        driver.findElement(By.id("email")).sendKeys(EMAIL_TEST);
        driver.findElement(By.id("password")).sendKeys("Password123!");
        driver.findElement(By.xpath("//button[@type='submit']")).click();

       
        driver.get("http://localhost:8080/BookMarker/DettaglioLibroServlet?id=" + ID_LIBRO);

    
        
        WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataRitiro")));
        Select selectDate = new Select(selectElement);
        selectDate.selectByIndex(1); 

      
        driver.findElement(By.xpath("//button[contains(text(), 'Prenota')]")).click();

    
        WebElement btnConferma = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@onclick='confermaInvio()']")));
        btnConferma.click();

    
        WebElement messaggioErrore = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".error-message, .toast.error, .alert-danger")
        ));
        
        String testoErrore = messaggioErrore.getText().toLowerCase();
        
      
        Assertions.assertTrue(testoErrore.contains("già prenotato") || testoErrore.contains("esistente"), 
            "Il messaggio di errore non è quello atteso. Testo trovato: " + testoErrore);
    }
    
    private final int ID_LIBRO_1 = 501;
    private final int ID_LIBRO_2 = 502;
    private final int ID_LIBRO_3 = 503;

    @Test
    public void testPrenotazioneLibro_MaxPrenotazioni() {

    
        DatabaseTestHelper.createLibro(ID_LIBRO_1, "Libro Filler 1", 5);
        DatabaseTestHelper.createLibro(ID_LIBRO_2, "Libro Filler 2", 5);
        DatabaseTestHelper.createLibro(ID_LIBRO_3, "Libro Filler 3", 5);

        DatabaseTestHelper.createPrestito(EMAIL_TEST, ID_LIBRO_1, "Richiesto");
        DatabaseTestHelper.createPrestito(EMAIL_TEST, ID_LIBRO_2, "Richiesto");
        DatabaseTestHelper.createPrestito(EMAIL_TEST, ID_LIBRO_3, "Richiesto");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

      
        driver.get("http://localhost:8080/BookMarker/login.jsp");
        driver.findElement(By.id("email")).sendKeys(EMAIL_TEST);
        driver.findElement(By.id("password")).sendKeys("Password123!");
        driver.findElement(By.xpath("//button[@type='submit']")).click();

   
        driver.get("http://localhost:8080/BookMarker/DettaglioLibroServlet?id=" + ID_LIBRO);

        
        WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataRitiro")));
        Select selectDate = new Select(selectElement);
        selectDate.selectByIndex(1); 


        driver.findElement(By.xpath("//button[contains(text(), 'Prenota')]")).click();

    
        WebElement btnConferma = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@onclick='confermaInvio()']")));
        btnConferma.click();

        WebElement messaggioErrore = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".error-message, .toast.error, .alert-danger")
        ));
        
        String testoErrore = messaggioErrore.getText().toLowerCase();
        

        Assertions.assertTrue(testoErrore.contains("limite") || 
                              testoErrore.contains("massimo") || 
                              testoErrore.contains("3"), 
            "Il messaggio non indica il raggiungimento del limite " + testoErrore);
    }
    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();

        DatabaseTestHelper.deleteUtente(EMAIL_TEST); 
        DatabaseTestHelper.deleteLibro(ID_LIBRO);


        DatabaseTestHelper.deleteLibro(ID_LIBRO_1);
        DatabaseTestHelper.deleteLibro(ID_LIBRO_2);
        DatabaseTestHelper.deleteLibro(ID_LIBRO_3);
    }
}