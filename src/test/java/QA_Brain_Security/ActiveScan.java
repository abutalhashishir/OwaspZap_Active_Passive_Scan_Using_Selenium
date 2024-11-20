package QA_Brain_Security;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.zaproxy.clientapi.core.ClientApiException;

import static QA_Brain_Security.ZapUtil.*;

import java.lang.reflect.Method;

public class ActiveScan {
    private WebDriver driver;
    private final String urlToTest = "https://qabrains.com/";

    @BeforeMethod
    public void setUp()  {
        // Configure the proxy settings
    	
    	FirefoxOptions options=new FirefoxOptions();
    	options.setProxy(proxy);
    	options.setAcceptInsecureCerts(true);
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver(options);
        
    }

    
    @Test
    public void testActiveScan() throws ClientApiException {
        addURLToScanTree(urlToTest);
        performActiveScan(urlToTest);
    }

    @AfterMethod
    public void tearDown(Method method) throws InterruptedException{
    	Thread.sleep(10000);
        generateZapReport(urlToTest);
        driver.quit();
    }
}
