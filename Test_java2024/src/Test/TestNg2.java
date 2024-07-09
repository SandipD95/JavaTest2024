package Test;

import org.testng.annotations.*;

public class TestNg2 {

@BeforeSuite
public void a()
{
	System.out.println("BeforeSuite");
}
@BeforeMethod
public void f()
{
	System.out.println("Before Method");
}
	@Test
public void b()
{
	System.out.println("Test1");
}
	
@Test
public void c()
{
	System.out.println("Test2");
}
	
@Test
public void d()
{
	System.out.println("Test3");
}

@AfterSuite
public void a1()
{
	System.out.println("AfterSuite");
}
	
	
	
}
