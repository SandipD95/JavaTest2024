package Test;

import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class TestJava {

	public static void main(String[] args) throws InterruptedException {
		System.setProperty("webdriver.chrome.driver","D:\\software\\chromedriver.exe");
		WebDriver driver=new ChromeDriver();
		driver.get("https://www.naukri.com/mnjuser/homepage");
		Thread.sleep(9000);
	//	driver.findElement(By.id("usernameField")).sendKeys("abc@gamail.com");
		WebElement e=driver.findElement(By.id("usernameField"));
		e.sendKeys("Om");
		e.clear();
		
		
		driver.findElement(By.id("passwordField")).sendKeys("wrongPass");
//driver.findElement(By.xpath("//*[@id='loginForm']/div[2]/div[3]/div/button[1]")).click();
//driver.findElement(By.xpath("//*[@id='passwordField']")).sendKeys("abc");
	//driver.findElement(null)

//driver.findElement(By.cssSelector("#loginForm > div:nth-child(2) > div:nth-child(2) > div > div.forgot-password-wrapper > a > small")).click();
		
		System.out.println("Hari");
		//driver.close();
		// TODO Auto-generated method stub
driver.findElement(By.linkText("Sign in with Google")).click();
System.out.println("Sign in with Google link hit");
Thread.sleep(19000);
//main window handle
String mainWindow=driver.getWindowHandle();
System.out.println(mainWindow);
//all window handle
Set<String> allWindowHandles=driver.getWindowHandles();
String s;

for(String i:allWindowHandles) {
	 s=i;
	 if(s!=mainWindow)
	 {
		 driver.switchTo().window(s); 
	 }
	}

Thread.sleep(19000);
//driver.switchTo().window(s);
System.out.println("window switch");
driver.findElement(By.id("identifierId")).sendKeys("Email@gmail.com");
System.out.println("Email entered");
driver.close();
System.out.println("child window close");
driver.switchTo().window(mainWindow);
System.out.println("user on main window");
driver.findElement(By.linkText("Register for Free")).click();
	}

}
