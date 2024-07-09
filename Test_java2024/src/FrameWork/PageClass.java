package FrameWork;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class PageClass extends BaseClass {

	
	public WebElement emailreturn(WebElement email) {
	email=driver.findElement(By.id("email"));
	return email;
	}
	
}
