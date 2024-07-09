package Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class SpiceJet {
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws InterruptedException 
	{
		System.setProperty("webdriver.chrome.driver", "D:\\software\\chromedriver.exe");
		WebDriver driver=new ChromeDriver();
	//	driver.manage().timeouts().implicitlyWait(30,TimeUnit.SECONDS);
	//	driver.manage().timeouts().implicitlyWait(30,TimeUnit.SECONDS);
		driver.get("https://www.spicejet.com/");
		Thread.sleep(5000);
		//driver.manage().window().maximize();
	//driver.navigate().refresh();
	Thread.sleep(5000);
	//	driver.navigate().forward();
		Thread.sleep(5000);
	//	driver.navigate().back();
		System.out.println(driver.getCurrentUrl());
		System.out.println(driver.getTitle());
		System.out.println(driver.getWindowHandle());
		System.out.println(driver.getWindowHandles());
		Thread.sleep(9000);
		//driver.switchTo().alert().dismiss();
	//driver.switchTo().alert().accept();
	//String s=driver.switchTo().alert().getText();
//	driver.findElement(By.xpath("//*[@id='main-container']/div/div[1]/div[3]/div[2]/div[3]/div/div[1]/div[1]/input");
//	System.out.println(s);
	//*[@id=":4"]/div[2]
	
		
	}

}
