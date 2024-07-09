package Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class GmailTest{
	
	public static void main(String[] args) throws InterruptedException {
		
	
	System.setProperty("webdriver.chrome.driver","D:\\software\\chromedriver.exe");
    WebDriver driver=new ChromeDriver();
    driver.manage().window().maximize();
    Thread.sleep(50000);
    //Implicit wait
 //   driver.manage().timeouts().implicitlyWait(30,TimeUnit.SECONDS);
    driver.get("https://mail.google.com/mail/u/0/?tab=rm&pli=1#inbox");
    System.out.println(driver.getWindowHandles());
    driver.findElement(By.xpath("//input[@type='email']")).sendKeys("sandipdahatonde1234@gmail.com");
    driver.findElement(By.xpath("//*[@id=\"identifierNext\"]/div/button/span")).click();
    System.out.println(driver.getWindowHandles());
 
}}
