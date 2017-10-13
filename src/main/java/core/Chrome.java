package core;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.support.ui.*;
import java.util.logging.*;
import java.math.*;
import java.text.DecimalFormat;
import java.util.regex.*;



public class Chrome {


    public static void main(String[] args) throws InterruptedException {
        Logger.getLogger("").setLevel(Level.OFF);
        String url = "http://alex.academy/exe/payment_tax/index.html";
        String driverPath = "";
        if (System.getProperty("os.name").toUpperCase().contains("MAC"))
            driverPath = "./resources/webdrivers/mac/chromedriver";
        else if (System.getProperty("os.name").toUpperCase().contains("WINDOWS"))
            driverPath = "./resources/webdrivers/pc/chromedriver.exe";
        else throw new IllegalArgumentException("Unknown OS");

        System.setProperty("webdriver.chrome.driver", driverPath);
        System.setProperty("webdriver.chrome.silentOutput", "true");
        ChromeOptions option = new ChromeOptions();
        option.addArguments("disable-infobars");
        option.addArguments("--disable-notifications");
        if (System.getProperty("os.name").toUpperCase().contains("MAC"))
            option.addArguments("-start-fullscreen");
        else if (System.getProperty("os.name").toUpperCase().contains("WINDOWS"))
            option.addArguments("--start-maximized");
        else throw new IllegalArgumentException("Unknown OS");
        WebDriver driver = new ChromeDriver(option);
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        WebDriverWait wait = new WebDriverWait(driver, 15);

        driver.get(url);

        String string_monthly_payment_and_tax = driver.findElement(By.id("id_monthly_payment_and_tax")).getText();

        String regex = "^"
                + "(?:\\D*)?"
                + "((?:\\d{2})?(?:\\.)?(\\d{0,2})?)"
                + "(?:\\D*)?"
                + "((?:\\d{1})?(?:\\.)?(\\d{0,2})?)"
                + "(?:\\%)?"
                + "$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(string_monthly_payment_and_tax);
        m.find();
        double monthly_payment = Double.parseDouble(m.group(1));
        double tax = Double.parseDouble(m.group(3));
        // (91.21 * 8.25) / 100 = 7.524825    rounded => 7.52
        double monthly_and_tax_amount = new BigDecimal((monthly_payment * tax) / 100).setScale(2, RoundingMode.HALF_UP).doubleValue();
        // 91.21 + 7.52 = 98.72999999999999   rounded => 98.73
        double monthly_payment_with_tax = new BigDecimal(monthly_payment + monthly_and_tax_amount).setScale(2, RoundingMode.HALF_UP).doubleValue();
        // double annual_payment_with_tax = monthly_payment_with_tax * 12;
        double annual_payment_with_tax = new BigDecimal(monthly_payment_with_tax * 12).setScale(2, RoundingMode.HALF_UP).doubleValue();
        driver.findElement(By.id("id_annual_payment_with_tax")).sendKeys(String.valueOf(annual_payment_with_tax));
        driver.findElement(By.id("id_validate_button")).submit();
        String actual_result = driver.findElement(By.id("id_result")).getText();
        System.out.println("Browser is: Chrome");
        System.out.println("String: \t" + string_monthly_payment_and_tax);
        System.out.println("Annual Payment with Tax: " + annual_payment_with_tax);
        System.out.println("Result: \t" + actual_result);
        driver.quit();
    }
}