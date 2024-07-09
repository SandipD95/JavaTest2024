package practiceProgram;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
public class Crossbrowser {

	
	//String browserName="chrome";
public static void main(String[] args)
{
	 WebDriver driver;
	
	String browserName="firefox";
		switch(browserName)
		{
		case "chrome":
			System.setProperty("webdriver.chrome.driver","D:\\software\\chromedriver.exe");
			 driver=new ChromeDriver();
			 driver.get("https://www.google.com/");
			 System.out.println("hey Chrome run the ");
			break;
			
	  case "firefox":
		  System.setProperty("webdriver.gecko.driver","D:\\software\\geckodriver.exe");
			 driver=new FirefoxDriver();
			 driver.get("https://www.google.com/");
		  System.out.println("Hey firfox run the script");
			break;
		}
		
	
}
}
