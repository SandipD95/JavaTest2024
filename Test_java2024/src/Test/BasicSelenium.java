package Test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
public class BasicSelenium {
	WebDriver driver;
	//ChromeDriver driver;
	
	BasicSelenium(String URL) throws InterruptedException{
	System.setProperty("webdriver.chrome.driver","D:\\software\\chromedriver.exe");
	 driver=new ChromeDriver();
	driver.get(URL);
	driver.manage().window().maximize();
//	driver.
	//Thread.sleep(5000);
//	System.out.println();
	}
	BasicSelenium(String URL,int timeout) throws InterruptedException{
		System.setProperty("webdriver.chrome.driver","D:\\software\\chromedriver.exe");
		 driver=new ChromeDriver();
		driver.get(URL);
		driver.manage().window().maximize();
		Thread.sleep(timeout);
		System.out.println();
		
		}

public static void main(String[] args) throws InterruptedException

	{
	BasicSelenium base=new BasicSelenium("https://demoqa.com/elements");
	BasicSelenium base1=new BasicSelenium("https://www.facebook.com/login/",5000);//overloaded constructor
	System.out.println("hi");

}


	

}
