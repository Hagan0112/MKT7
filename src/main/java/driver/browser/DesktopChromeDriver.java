package driver.browser;

import driver.Driver;
import driver.DriverProperties;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.logging.Level;

public class DesktopChromeDriver extends Driver {
	public DesktopChromeDriver(DriverProperties properties) throws MalformedURLException {
		super(properties);

		System.out.println(String.format("Picking up ChromeDriver at %s", properties.getExecutablePath()));

		ChromeOptions options = new ChromeOptions();

		// Example settings for ChromeOptions
		options.addArguments("--start-maximized");// Open browser in maximized mode
		options.addArguments("--window-size=1920,1080");
		options.addArguments("--force-device-scale-factor=1");
		options.addArguments("--high-dpi-support=1.0");
		options.addArguments("--disable-infobars"); // Disable "Chrome is being controlled" message
		options.addArguments("--disable-notifications"); // Disable browser notifications
		options.addArguments("--disable-extensions"); // Disable extensions
		options.addArguments("--remote-allow-origins=*"); // For specific scenarios
		options.addArguments("--enable-use-zoom-for-dsf");


		//options = configureChromeOptions();

		//For setting download directory
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.prompt_for_download", false);
		chromePrefs.put("download.default_directory", properties.getDownloadPath());
		if (properties.isProxy()){
			System.out.println(String.format("Setting proxy is %s", properties.isProxy()));
			System.setProperty("webdriver.chrome.driver", properties.getExecutablePath());
			// set dowload path

			// creating a mob proxy
			BrowserMobProxy proxy = getProxyServer();

			// creating a selenium proxy
			Proxy seleniumProxy = getSeleniumProxy(proxy);
			options.setCapability(CapabilityType.PROXY, seleniumProxy);

			// Add argument to bypass WebSocket traffic
			options.addArguments("--proxy-bypass-list=ws://*,wss://*");

//			options.setCapability(CapabilityType.ACCEPT_SSL_CERTS,true);
			options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS,true);
			options.setExperimentalOption("prefs",chromePrefs);
			setWebDriver(new ChromeDriver(options));
			proxy.newHar();
			properties.setBrowserMobProxy(proxy);
			System.out.println("DEBUG: Proxy Port is " + proxy.getPort());
		} else if (properties.getRemoteURL() == null || properties.getRemoteURL().equals("")) {
			System.out.println("remote");
			options.setExperimentalOption("prefs",chromePrefs);
			System.setProperty("webdriver.chrome.driver", properties.getExecutablePath());
			System.setProperty("webdriver.http.factory", "jdk-http-client");
			System.setProperty("webdriver.chrome.logfile", "chromedriverlogs.log");
			System.setProperty("webdriver.chrome.verboseLogging", "true");
			// Add user-agent for detect by Silence project
			options.addArguments("user-agent=qa-automation");
			setWebDriver(new ChromeDriver(options));
		} else {
			System.out.println("Normal");
			options.addArguments("--remote-allow-origins=*");
			options.setCapability("browserVersion",properties.getBrowserVersion());
			options.setCapability("platformName",properties.getPlatform());
			options.setCapability("se:name", "My simple test");
			options.setCapability("se:sampleMetadata", "Sample metadata value");
		//	System.setProperty("webdriver.chrome.driver", properties.getExecutablePath());
			setWebDriver(new RemoteWebDriver(new URL(properties.getRemoteURL()), options));
		}
	}


	/********************
	 * Private methods
	 *********************/

	private BrowserMobProxy getProxyServer() {
		BrowserMobProxy proxy = new BrowserMobProxyServer();
		try {
			proxy.setTrustAllServers(true);
			// above line is needed for application with invalid certificates
			proxy.start();
		} catch (RuntimeException ex){
			System.err.println("EXCEPTION: RuntimeException occurs at getProxyServer");
		}
		return proxy;
	}

	private Proxy getSeleniumProxy(BrowserMobProxy proxyServer) {
		Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxyServer);
		try {
			String hostIp = Inet4Address.getLocalHost().getHostAddress();
			seleniumProxy.setHttpProxy(hostIp + ":" + proxyServer.getPort());
			seleniumProxy.setSslProxy(hostIp + ":" + proxyServer.getPort());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (RuntimeException ex){
			System.err.println("Exception: RuntimeException occurs at getSeleniumProxy");
		}
		return seleniumProxy;
	}

	private ChromeOptions configureChromeOptions(){
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless=new");
		options.addArguments("--window-size=1920,1080");
		return options;
	}


}