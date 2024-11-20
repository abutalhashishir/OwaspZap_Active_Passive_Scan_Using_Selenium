package QA_Brain_Security;


import static QA_Brain_Security.ZapUtil.proxy;

import java.lang.reflect.Method;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;
import static QA_Brain_Security.ZapUtil.*;

public class PassiveScan {
	private WebDriver driver;
	private final String urlToTest = "https://qabrains.com/";

	@BeforeMethod
	public void setUp() {
		// Configure the proxy settings

		FirefoxOptions options = new FirefoxOptions();
		options.setProxy(proxy);
		options.setAcceptInsecureCerts(true);
		WebDriverManager.firefoxdriver().setup();
		driver = new FirefoxDriver(options);

	}

	@Test
	public void testPassiveScan() throws InterruptedException {
		driver.get(urlToTest);
		Thread.sleep(10000);
		// Wait for passive scan to complete
		ZapUtil.waitTillPassiveScanCompleted();
	}

	@AfterMethod
	public void tearDown(Method method) throws InterruptedException {
		Thread.sleep(10000);
		generateZapReport(urlToTest);
		driver.quit();
	}
}
