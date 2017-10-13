package core;

        import org.openqa.selenium.*;
        import org.openqa.selenium.safari.SafariDriver;
        import java.math.*;
        import java.util.concurrent.TimeUnit;
        import java.util.logging.*;
        import java.util.regex.*;


public class Safari {

    public static void main(String[] args) throws InterruptedException {
        Logger.getLogger("").setLevel(Level.OFF);
        String url = "http://alex.academy/exe/payment_tax/index.html";

        if (!System.getProperty("os.name").toUpperCase().contains("MAC"))
        throw new IllegalArgumentException("Safari is available only on Mac");
        WebDriver driver = new SafariDriver();
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver.get(url);
        String string_monthly_payment_and_tax =driver.findElement(By.id("id_monthly_payment_and_tax")).getText();
        String regex = "^"
                + "(?:\\D*)?"
                + "((?:\\d{2})?(?:\\.)?(\\d{0,2})?)"
                + "(?:\\D*)?"
                + "((?:\\d{1})?(?:\\.)?(\\d{0,2})?)"
                + "(?:\\%)?"
                + "$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(string_monthly_payment_and_tax); m.find();
        double monthly_payment = Double.parseDouble(m.group(1));
        double tax =             Double.parseDouble(m.group(3));
        // (91.21 * 8.25) / 100 = 7.524825    rounded => 7.52
        double monthly_and_tax_amount = new BigDecimal((monthly_payment * tax) / 100).setScale(2, RoundingMode.HALF_UP).doubleValue();
        // 91.21 + 7.52 = 98.72999999999999   rounded => 98.73
        double monthly_payment_with_tax = new BigDecimal(monthly_payment + monthly_and_tax_amount).setScale(2, RoundingMode.HALF_UP).doubleValue();
        double annual_payment_with_tax = new BigDecimal(monthly_payment_with_tax * 12).setScale(2, RoundingMode.HALF_UP).doubleValue();
        driver.findElement(By.id("id_annual_payment_with_tax")).sendKeys(String.valueOf(annual_payment_with_tax));
        // Safari dynamic click
        WebElement settings = driver.findElement(By.id("id_validate_button"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", settings);

        String actual_result = driver.findElement(By.id("id_result")).getText();
        System.out.println("Browser is: Safari");
        System.out.println("String: \t" +                string_monthly_payment_and_tax);
        System.out.println("Annual Payment with Tax: " + annual_payment_with_tax);
        System.out.println("Result: \t" +                actual_result);
        driver.quit();
    }
}

