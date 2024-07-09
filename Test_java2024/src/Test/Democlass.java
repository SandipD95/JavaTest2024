package Test;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Listeners(data.ListnnerImplementation.class)
public class Democlass {
	public static	WebDriver driver; 
	Democlass democlass;
	
	public  void clickElement(WebElement e)
	{
		JavascriptExecutor js=(JavascriptExecutor)driver;
		js.executeScript("arguments[0].scrollIntoView();",e);
		e.click();
	}



@BeforeMethod(enabled=true)
void baseMethod()
{
	System.setProperty("webdriver.chrome.driver","D:\\software\\chromedriver.exe");
	 driver=new ChromeDriver();
	driver.manage().window().maximize();
	driver.manage().timeouts().implicitlyWait(100,TimeUnit.SECONDS);
//	driver.get("https://money.rediff.com/indices/bse");
	//driver.get("https://www.facebook.com/login/");
	driver.get("https://demoqa.com/elements");
	democlass=new Democlass();
	

}
@AfterMethod(enabled=false)
public void tearDown()
{
	driver.close();
}


@Test(enabled=false)
void a()
{
	Democlass democlass=new Democlass();
	//democlass.pageDown(300);
	//pageDown(300);
	JavascriptExecutor js=(JavascriptExecutor)driver;
	//js.executeScript("Windows.scroll(0,300)","");
	try {
		js.executeScript("window.scrollBy(0,300)", "");
WebElement element=	driver.findElement(By.xpath("//h5[@xpath='1']"));
Actions act=new Actions(driver);
act.moveToElement(element).click().perform();
	//js.executeScript("arguments[0].scrollIntoView();", element);
	//element.click();
	}catch(Exception e)
	{
		e.printStackTrace();
		File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		String screenshotBase64 = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BASE64);
		System.out.println("catch excecuted");
	}
	finally {
		System.out.println("Finally excecuted");
		
	}
	
	}
@Test(enabled=false)
public void texbox()
{
	driver.findElement(By.xpath("//span[normalize-space()='Text Box']")).click();
	driver.findElement(By.id("userName")).sendKeys("Hari");
	driver.findElement(By.id("userEmail")).sendKeys("abc@gmail.com");
	driver.findElement(By.id("currentAddress")).sendKeys("india");
	driver.findElement(By.id("permanentAddress")).sendKeys("adsf");
	JavascriptExecutor js=(JavascriptExecutor)driver;
	
	js.executeScript("window.scrollBy(0,350)","");
	driver.findElement(By.id("submit")).click();
}
@Test(enabled=false)
public void checkbox()
{
	driver.findElement(By.xpath("//span[normalize-space()='Check Box']")).click();
	JavascriptExecutor js=(JavascriptExecutor)driver;
	js.executeScript("window.scrollBy(0,350)", "");
	driver.findElement(By.xpath("//span[@class='rct-checkbox']")).click();
}
@Test(enabled=false)
public void buttons() throws InterruptedException
{
	
	JavascriptExecutor js=(JavascriptExecutor)driver;
	js.executeScript("window.scrollBy(0,100)", "");
	//js.executeScript("window.scrollBy(0,document.body.scrollHeight)");
	driver.findElement(By.xpath("//span[normalize-space()='Buttons']")).click();
	//js.executeScript("window.scrollBy(0,350)", "");
	
	Actions act=new Actions(driver);
	
WebElement wl=	driver.findElement(By.id("doubleClickBtn"));
	act.doubleClick(wl).perform();
	Thread.sleep(9000);
	WebElement w=	driver.findElement(By.id("rightClickBtn"));
	act.contextClick(w).perform();
	
	
}

@Test(enabled=false)
public void buttonsClick()
{
	driver.findElement(By.xpath("//span[normalize-space()='Buttons']")).click();
	Democlass democlass=new Democlass();
//	democlass.pageDown(300);
	//JavascriptExecutor js=(JavascriptExecutor)driver;
	//js.executeScript("window.scrollBy(0,350)", "");
	driver.findElement(By.xpath("//span[@class='rct-checkbox']")).click();
}

@Test(enabled=false)
public void freamHnadle()
  {
	JavascriptExecutor js=(JavascriptExecutor)driver;
	js.executeScript("window.scrollBy(0,650)","");
	driver.findElement(By.xpath("//div[3]/span/div/div")).click();
	//driver.findElement(By.xpath("//span[@class='rct-checkbox']")).click();
	
	driver.findElement(By.xpath("/div[3]/div/ul/li[3]/span")).click();
	WebElement f=driver.findElement(By.id("frame1"));
	driver.switchTo().frame(f);
	
  }

@Test(enabled=false)
public void dynamic_properties() throws InterruptedException
  {
JavascriptExecutor js=(JavascriptExecutor)driver;
//	String cWindow=driver.getWindowHandle();
//democlass.pageDown(2,cWindow);
	js.executeScript("window.scrollBy(0,350)","");
	driver.findElement(By.xpath("//span[normalize-space()='Dynamic Properties']")).click();
	Thread.sleep(11000);
	//boolean b=driver.findElement(By.id("enableAfter")).isEnabled();
//	System.out.println("Element b is "+b);
	
	driver.findElement(By.id("enableAfter")).click();
		System.out.println("element click done");
	
  }
@Test(enabled=false)
public void webElwmnt() throws InterruptedException
  {
JavascriptExecutor js=(JavascriptExecutor)driver;
	js.executeScript("window.scrollBy(0,350)","");
	driver.findElement(By.xpath("//span[normalize-space()='Links']")).click();
	List<WebElement> wl=driver.findElements(By.tagName("a"));
	System.out.println(wl.size());
	int size=wl.size();
	for(int i=0;i<size;i++)
	{
		System.out.println(wl.get(i).getText()+"    "+wl.get(i).getAttribute("href"));
	}
	// Scrolling down the page till the element is found		
  
	
WebElement w=driver.findElement(By.linkText("Forbidden"));
	  js.executeScript("arguments[0].scrollIntoView();", w);
	  w.click();
  }
@Test(enabled=false)
public void windowHandle()
{
	
//WebElement m=driver.findElement(By.xpath("//div[3]/span/div/div"));
WebElement m=driver.findElement(By.xpath("//*[contains(Text(),'Alerts, Frame & Windows']"));
JavascriptExecutor js=(JavascriptExecutor)driver;
js.executeScript("arguments[0].scrollIntoView();",m);
m.click();
WebElement w=driver.findElement(By.xpath("//span[normalize-space()='Browser Windows']"));
    js.executeScript("arguments[0].scrollIntoView();",w);
	w.click();
	WebElement d= driver.findElement(By.id("tabButton"));
	  js.executeScript("arguments[0].scrollIntoView();",d);
		d.click();
	String parentWindow	=driver.getWindowHandle();
	Set<String> allWindow=driver.getWindowHandles();
	System.out.println(allWindow);
	System.out.println(parentWindow);
	Iterator<String> it=allWindow.iterator();
while(it.hasNext())
{
	
String childWindow=it.next();
	if(!parentWindow.equals(childWindow))
	{
		driver.switchTo().window(childWindow);
	String title=driver.getCurrentUrl();
		System.out.println(title);
		System.out.println("if executed");
	}
	else {
		System.out.println("first window");
	}
}
String pageSource=driver.getPageSource();
String text="This is a sample page";
boolean b=pageSource.contains(text);
System.out.println(b);


}
@Test(enabled=false)
public void explicitWaitTest() throws Exception
{
	WebDriverWait wait=new WebDriverWait(driver,60);
	//wait.until(ExpectedConditions.alertIsPresent());
	WebElement m=driver.findElement(By.xpath("//div[3]/span/div/div"));
	JavascriptExecutor js=(JavascriptExecutor)driver;
	js.executeScript("arguments[0].scrollIntoView();", m);
	//js.executeScript("arguments[0].scrollIntoView();", w);
	m.click();
	WebElement d=driver.findElement(By.xpath("//span[normalize-space()='Alerts']"));
	d.click();
	WebElement a=driver.findElement(By.id("alertButton"));
	js.executeScript("arguments[0].scrollIntoView();",a);
	a.click();
	Thread.sleep(5000);
	driver.switchTo().alert().accept();
	//js.executeScript("window.scrollBy(0,600)", "");
	driver.findElement(By.id("timerAlertButton")).click();
	Thread.sleep(6000);
	wait.until(ExpectedConditions.alertIsPresent());
	driver.switchTo().alert().accept();
	driver.findElement(By.id("timerAlertButton")).click();
	driver.findElement(By.id("confirmButton")).click();
	driver.switchTo().alert().dismiss();
	String s=driver.findElement(By.id("confirmResult")).getText();
	String t="Cancel";
     driver.findElement(By.id("promtButton")).click();
	driver.switchTo().alert().sendKeys("ABC");
	driver.switchTo().alert().accept();
	
	
}

@Test(enabled=false)
public void assertionTest()
{
	String a="ABC",b="EFG";
	//Assert.assertEquals(b, a);
	
	SoftAssert as=new SoftAssert();
	as.assertEquals(a, b);
	as.assertAll();
	as.assertEquals(driver.getTitle(), "");
	}

@Test(enabled=false)
public void tab()
{
	WebDriverWait wait=new WebDriverWait(driver,60);
	//wait.until(ExpectedConditions.alertIsPresent());
	WebElement m=driver.findElement(By.xpath("//div[4]/span/div/div"));
	JavascriptExecutor js=(JavascriptExecutor)driver;
	js.executeScript("arguments[0].scrollIntoView();", m);
	//js.executeScript("arguments[0].scrollIntoView();", w);
	m.click();
	WebElement d=driver.findElement(By.xpath("//div[4]/div/ul/li[6]/span"));
	d.click();
	
	//js.executeScript("arguments[0].scrollIntoView();",a);
}
@Test(enabled=false)
public void tooltips()
{
	WebDriverWait wait=new WebDriverWait(driver,60);
	//wait.until(ExpectedConditions.alertIsPresent());
	WebElement m=driver.findElement(By.xpath("//div[@id='app']/div/div/div/div/div/div/div[4]/span/div/div"));
	JavascriptExecutor js=(JavascriptExecutor)driver;
	js.executeScript("arguments[0].scrollIntoView();", m);
	//js.executeScript("arguments[0].scrollIntoView();", w);
	m.click();
	WebElement d=driver.findElement(By.xpath("//li[contains(.,'Tool Tips')]"));
//	d.click();
Actions act=new Actions(driver);
//div[@id='app']/div/div/div/div/div/div/div[4]/span/div/div


js.executeScript("arguments[0].scrollIntoView();", d);
d.click();
WebElement e=driver.findElement(By.id("toolTipButton"));
js.executeScript("arguments[0].scrollIntoView();", e);
act.moveToElement(e).build().perform();
}
@Test(enabled=false)
public void menu()
{
	WebDriverWait wait=new WebDriverWait(driver,60);
	//wait.until(ExpectedConditions.alertIsPresent());
	WebElement m=driver.findElement(By.xpath("//div[@id='app']/div/div/div/div/div/div/div[4]/span/div/div"));
	JavascriptExecutor js=(JavascriptExecutor)driver;
	js.executeScript("arguments[0].scrollIntoView();", m);
	//js.executeScript("arguments[0].scrollIntoView();", w);
	m.click();
	WebElement d=driver.findElement(By.xpath("//li[contains(.,'Menu')]"));
d.click();
WebElement a=driver.findElement(By.linkText("Main Item 2"));
js.executeScript("arguments[0].scrollIntoView();", a);
Actions act=new Actions(driver);
act.moveToElement(a).build().perform();
WebElement b=driver.findElement(By.linkText("SUB SUB LIST »"));
act.moveToElement(b).build().perform();
WebElement c=driver.findElement(By.linkText("Sub Sub Item 2"));
act.moveToElement(c).click().perform();
//act.click(c).build().perform();



}

@Test(enabled=false)
public void fileUpload()
{
	WebDriverWait wait=new WebDriverWait(driver,60);
	//wait.until(ExpectedConditions.alertIsPresent());
	WebElement m=driver.findElement(By.xpath("//span[contains(.,'Upload and Download')]"));
	JavascriptExecutor js=(JavascriptExecutor)driver;
	js.executeScript("arguments[0].scrollIntoView();", m);
	//js.executeScript("arguments[0].scrollIntoView();", w);
	m.click();
	WebElement d=driver.findElement(By.id("uploadFile"));
d.sendKeys("D:\\quri.txt");
WebElement f=driver.findElement(By.id("downloadButton"));
js.executeScript("arguments[0].scrollIntoView();", f);
f.click();
}



@Test(enabled=true)
public void dropdown()
{
	WebElement m=driver.findElement(By.xpath("//div[4]/span/div/div"));
	JavascriptExecutor js=(JavascriptExecutor)driver;
	js.executeScript("arguments[0].scrollIntoView();", m);
	//js.executeScript("arguments[0].scrollIntoView();", w);
	m.click();
	WebElement d=driver.findElement(By.xpath("//span[contains(.,'Select Menu')]"));
d.click();
WebElement a=driver.findElement(By.id("oldSelectMenu"));
js.executeScript("arguments[0].scrollIntoView();", a);
Select sc=new Select(a);
sc.selectByValue("Red1");
	
	
}

@Test(enabled=false)
public void hideElement()
{
	WebElement m=driver.findElement(By.xpath("//div[4]/span/div/div"));
	JavascriptExecutor js=(JavascriptExecutor)driver;
	js.executeScript("arguments[0].scrollIntoView();", m);//pagedown still element visibility
	js.executeScript("window.scrollBy(0,500)","");//
	js.executeScript("Document.getElementById('displayText').value='abc';");//to handlehidenelement
js.executeScript("document.getElementById('name').value='abc';"); //to enter data in text feild
		//document.getElementById("fieldId").value = "Value";
	//js.executeScript("arguments[0].scrollIntoView();", w);
}

@Test(enabled=false)
public void dataTable() throws InterruptedException
{
	
//	driver.findElement(By.id("dataTable")).click();	
	//WebElement a=driver.findElement(By.id("showMoreLess"));
	WebElement a=driver.findElement(By.xpath("//a[text()='Privacy Centre']"));
	//a.click();
	democlass.clickElement(a);	
	/*
	 * JavascriptExecutor js=(JavascriptExecutor)driver; for(int i=0;i<6;i++) {
	 * Thread.sleep(5000); js.executeScript("window.scrollBy(0,300)","");
	 * driver.findElement(By.xpath("//a[text()='MF Selector']")).click();
	 * //w.click(); //System.out.println(w.isDisplayed()+"  "+w.isEnabled());
	 * 
	 * }
	 */
}

@Test(groups={"smoke","sanity"},enabled=false,dependsOnMethods={"baseMethod"})
public void dependsMethodTest()
{
	WebElement a=driver.findElement(By.xpath("//a[text()='Privacy Centre']"));
	democlass.clickElement(a);
}


@Test(groups= {"smoke","Sanity"},priority=1,enabled=false)
public void iteratorTest() throws InterruptedException
{
	WebElement m=driver.findElement(By.xpath("//*[text()='Alerts, Frame & Windows']"));
	JavascriptExecutor js=(JavascriptExecutor)driver;
	js.executeScript("arguments[0].scrollIntoView();",m);
	m.click();
	WebElement w=driver.findElement(By.xpath("//*[text()='Browser Windows']"));
	    js.executeScript("arguments[0].scrollIntoView();",w);
		w.click();
		WebElement d= driver.findElement(By.id("tabButton"));
		  js.executeScript("arguments[0].scrollIntoView();",d);
		d.click();
		
		String perent=driver.getWindowHandle();
		Set<String> allWin=driver.getWindowHandles();
		Iterator<String> it=allWin.iterator();
		//Iterator<String> it=allWin.iterator();
		while(it.hasNext())
		{
		String child=(String) it.next();
			if(!perent.equals(child))
			{
				driver.switchTo().window(child);
				String title=driver.getTitle();
			String URL=	driver.getCurrentUrl();
				System.out.println("Title is "+title+" URL "+driver.getCurrentUrl());
			String s=driver.getPageSource();
			if(s.contains("This is a sample page"))
			
				System.out.println("page validation done");
			
			
				Thread.sleep(5000);
			driver.close();
			
			}
			
			driver.switchTo().window(perent);
			driver.findElement(By.id("windowButton")).click();
			String s2=driver.getPageSource();
			if(s2.contains("This is a sample page"))
			{
				System.out.println("page validation done");
			}
		
			
		}
		
	
}
@Test()
public void multipleWindowHandling()
{
	WebElement m=driver.findElement(By.xpath("//*[text()='Alerts, Frame & Windows']"));
	JavascriptExecutor js=(JavascriptExecutor)driver;
	js.executeScript("arguments[0].scrollIntoView();",m);
	m.click();
	WebElement w=driver.findElement(By.xpath("//*[text()='Browser Windows']"));
	    js.executeScript("arguments[0].scrollIntoView();",w);
		w.click();

js.executeScript("window.scrollBy(0,300)", "");

		driver.findElement(By.id("tabButton")).click();
		driver.findElement(By.id("windowButton")).click();	
		driver.findElement(By.id("messageWindowButton")).click();
		Set<String> allWindow=driver.getWindowHandles();
		for(int i=0;i<allWindow.size();i++)
		{
			System.out.println(allWindow);
		}
		for(String a:allWindow)
		{
			System.out.println(a);
		}
		
		
}
@Test(enabled=false)
public void Popup_Demo() throws InterruptedException, AWTException { 
 
driver.get("Webpage link"); 
driver.manage().window().maximize(); 
Thread.sleep(20000);
driver.findElement(By.id("PopUp")).click(); // Clicking on the popup button
Robot robot = new Robot();
//robot.mouseMove(4005); // Navigating through mouse hover. Note that the coordinates might differ, kindly check the coordinates of x and y axis and update it accordingly.
robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
Thread.sleep(2000);
robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
Thread.sleep(2000);
driver.quit();
}
}
