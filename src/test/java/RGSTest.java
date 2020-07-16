import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class RGSTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final static String BASE_URL = "http://www.rgs.ru";

    //test data
    private final static String FIRST_NAME = "Альберт";
    private final static String MIDDLE_NAME = "Германович";
    private final static String LAST_NAME = "Эйнштейн";
    private final static String REGION = "Магаданская область";
    private final static String PHONE = "9259252525";
    private final static String PHONE_MASKED = getPhoneMasked();
    private final static String EMAIL = "qwertyqwerty";
    private final static String CONTACT_DATE = "24.07.2020";
    private final static String COMMENT = "Black holes are where God divided by zero";

    @Before
    public void setUp() {
        System.setProperty("web.driver.chrome.driver", "E:\\projects\\seleniumhw1\\driver\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);

        //limit conditional timeout
        wait = new WebDriverWait(driver, 10);

        driver.get(BASE_URL);
    }

    @Test
    public void test() {
        WebElement mainMenu = driver.findElement(By.xpath("//div[@id='main-navbar-collapse']//a[contains(text(), 'Меню')]"));
        mainMenu.click();

        WebElement dmsElement = driver.findElement(By.xpath("//a[contains(text(), 'ДМС')]"));
        dmsElement.click();

        //avoid finding next element earlier than previous
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        WebElement titleElement = driver.findElement(By.xpath("//h1[@class='content-document-header']"));

        //prevent reading from title before it's present
        wait.until(ExpectedConditions.visibilityOf(titleElement));

        String titleElementText = titleElement.getText();
        assertTrue(titleElementText.contains("добровольное медицинское страхование"));

        WebElement sendApplicationButton = driver.findElement(By.xpath("//a[contains(text(), 'Отправить заявку')]"));
        sendApplicationButton.click();

        String src = driver.getPageSource();
        assertTrue(src.contains("Заявка на добровольное медицинское страхование"));

        //fill the application form
        WebElement lastNameInput = driver.findElement(By.name("LastName"));
        fillTheField(lastNameInput, LAST_NAME);

        WebElement firstNameInput = driver.findElement(By.name("FirstName"));
        fillTheField(firstNameInput, FIRST_NAME);

        WebElement middleNameInput = driver.findElement(By.name("MiddleName"));
        fillTheField(middleNameInput, MIDDLE_NAME);

        Select dropRegion = new Select(driver.findElement(By.name("Region")));
        dropRegion.selectByVisibleText(REGION);

        WebElement phoneInput = driver.findElement(By.xpath("//label[text()='Телефон']/following-sibling::input"));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[text()='Телефон']/following-sibling::input")));
        fillTheField(phoneInput, PHONE);

        WebElement emailInput = driver.findElement(By.name("Email"));
        fillTheField(emailInput, EMAIL);

        WebElement contactDateInput = driver.findElement(By.name("ContactDate"));
        fillTheContactDate(contactDateInput);

        WebElement commentInput = driver.findElement(By.name("Comment"));
        fillTheField(commentInput, COMMENT);

        //select a checkbox
        driver.findElement(By.xpath("//input[@class='checkbox']")).click();

        //assert that input fields are filled with test values
        assertEquals(LAST_NAME, lastNameInput.getAttribute("value"));
        assertEquals(FIRST_NAME, firstNameInput.getAttribute("value"));
        assertEquals(MIDDLE_NAME, middleNameInput.getAttribute("value"));
        assertEquals(REGION, dropRegion.getFirstSelectedOption().getText());
        assertEquals(PHONE_MASKED, phoneInput.getAttribute("value"));
        assertEquals(EMAIL, emailInput.getAttribute("value"));
        assertEquals(CONTACT_DATE, contactDateInput.getAttribute("value"));
        assertEquals(COMMENT, commentInput.getAttribute("value"));

        //try to send form
        WebElement submitButton = driver.findElement(By.xpath("//button[@id='button-m']"));
        submitButton.click();

        //check if email input is not validated
        assertTrue(driver.findElement(By.xpath("//span[contains(text(), 'Введите адрес электронной почты')]")).isDisplayed());
    }

    //UTIL METHODS
    //configure input
    private void fillTheField(WebElement element, String input) {
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        element.sendKeys(input);
    }

    //configure contact date input
    private void fillTheContactDate(WebElement dateInput) {
        dateInput.click();
        List<WebElement> enabledDays = driver.findElements(By.xpath("//table[@class='table-condensed']//tbody//td[@class='datepicker-day']"));
        for (WebElement enabledDay : enabledDays) {
            if (enabledDay.getText().equals(CONTACT_DATE.substring(0, 2))) {
                enabledDay.click();
                break;
            }
        }
    }

    //mask phone input according to pattern
    private static String getPhoneMasked() {
        return PHONE.replaceFirst("(\\d{3})(\\d{3})(\\d{2})(\\d+)", "+7 ($1) $2-$3-$4");
    }

    @After
    public void tearDown() {
        driver.quit();
    }


}
