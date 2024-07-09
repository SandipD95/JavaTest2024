package FrameWork;

import org.openqa.selenium.By;
import org.testng.annotations.*;

public class TestClass extends BaseClass {
	PageClass pg=new PageClass();
	@Test()
	public void fill_data() throws InterruptedException
	{
		Thread.sleep(5000);
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys("abcfdsf@gmail.com");
		System.out.println("Mail id Enterd");
		//pg.emailreturn();
	}
	@Test()
	public void clickLodin()
	{
		driver.findElement(By.linkText("Create new account")).click();
		System.out.println("Link click");
	}
	
}
