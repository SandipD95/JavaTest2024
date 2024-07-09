package FrameWork;



import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class BaseClass {

	WebDriver driver;
	
	@BeforeSuite()
	public void base()
	{
		System.setProperty("webdriver.chrome.driver", "D:\\software\\chromedriver.exe");
	 driver=new ChromeDriver();
	//driver.manage().timeouts().implicitlyWait(0, null);
	driver.get("https://classic.freecrm.com/index.html?e=1");
	driver.manage().window().maximize();	

		
		driver.findElement(By.name("username")).sendKeys("SandipBD");
		driver.findElement(By.name("password")).sendKeys("a12345678");
		driver.findElement(By.xpath("//input[@value='Login']")).click();
	}
			
	
	@Test
	public void newComplany() throws Exception
	{
	driver.switchTo().frame("mainpanel");
	System.out.println("Switch to frame successfull");
		Actions action=new Actions(driver);
	Thread.sleep(9000);
	//driver.findElement(By.xpath("//a[@title='Contacts']")).click();
	WebElement w=driver.findElement(By.xpath("//a[@title='Contacts']"));
	action.moveToElement(driver.findElement(By.xpath("//a[@title='Contacts']"))).build().perform();
action.moveToElement(driver.findElement(By.xpath("//a[text()='Full Search Form']"))).click();


//	driver.findElement(null)

		
	}
	@AfterSuite()
	public void teardown()
		{
		//driver.close();
		System.out.println("Brouser close");
	}
}
