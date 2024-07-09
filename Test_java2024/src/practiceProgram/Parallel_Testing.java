package practiceProgram;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;
public class Parallel_Testing {

	
@Test
void T1()
{
	System.setProperty("webdriver.chrome.driver","D:\\software\\chromedriver.exe");
WebDriver driver=new ChromeDriver();
driver.get("https://www.google.com/");
System.out.println("T1");
}
@Test
void T2()
{
	System.setProperty("webdriver.chrome.driver","D:\\software\\chromedriver.exe");
	WebDriver driver=new ChromeDriver();
	driver.get("https://www.google.com/");
		System.out.println("T2");
}
//for parrallel testing we need to configer in TestNg file

/*<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite thread-count="2" parallel="methods" name="parrallalTesting">
  <test thread-count="2" parallel="methods" name="parrallel">
    <classes>
      <class name="practiceProgram.Parallel_Testing"/>
    </classes>
  </test> <!-- parrallel -->
</suite> <!-- parrallalTesting -->
*/
}
