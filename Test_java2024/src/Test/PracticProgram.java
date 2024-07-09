package Test;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

public class PracticProgram {

@Test(enabled=false)
	public void swapNumber()
	{
		int a=20,b=30;
		System.out.println("value of a before swap ="+a+"    value of b before swap ="+b);
		a=a+b;
		b=a-b;
		a=a-b;
		System.out.println("value of a After swap ="+a+"    value of b after swap ="+b);
		}

@Test(enabled=false)
public void stringTest()
{
	String s="Hari Om";
	for(int i=0;i<s.length();i++)
	{
		char c=s.charAt(i);
		System.out.println(c+"  ASCII value "+(int)c);
		
	}	
	// Print only upper case charactors and same for lower
	for(int i=0;i<s.length();i++)
	{
	  char c=s.charAt(i);
		if(Character.isUpperCase(c))
		{
			System.out.println(c+" is Upper case ");
		}
		
	}
	
	// count only upper case charactors and same for lower
	int counter = 0,counter2=0;
	for(int i=0;i<s.length();i++)
		{
		
		  char c=s.charAt(i);
			if(Character.isUpperCase(c))
			{
				counter++;
			}
			if(Character.isLowerCase(c))
			{
				counter2++;
			}
		}
		System.out.println("count of upper case charactors "+counter+"  lower case "+counter2);
	
	
		
}
@Test(enabled=false)
public void stringReverse()
{
	
}
@Test(enabled=false)
public void stringCharactorCount()
{
	String s="Sandip dahatonde";
	int counter=0;
	for(int i=0;i<s.length();i++)
	{
		char c=s.charAt(i);
		for(int j=0;j<s.length();j++)
		{
			char e=s.charAt(j);
			if(c==e)
			{
				counter++;
			}
		}
		System.out.println("Count of "+c+"="+counter);
		counter=0;
	}
	
}
@Test(enabled=false)
public void NewStringFromOld()
{
	//Create new String only with upper char
	//Scanner sc=new Scanner(System.in);
	//String s=sc.next();
//	System.out.println("Enter String ");
	String s="Hari Om";
	String newString="";
	for(int i=0;i<s.length();i++)
	{
		char d=s.charAt(i);
		if(Character.isUpperCase(d))
		{
			newString=newString+d;
		}
	}
	System.out.println(newString);
	
	}
@Test(enabled=false)
public void StringTesting()
{
	//create new string with digit first and then character
	String s="Hari12 Om5";
	String digit="";
	String letter="";
	String newWord="";
	for (int i=0;i<s.length();i++)
	{
		char c=s.charAt(i);
		if(Character.isDigit(c))
		{
			digit=digit+c;
		}
		/*
		 * if(Character.isLetter(c)) { letter=letter+c; }
		 */
		else
		{
			letter=letter+c;
		}
	}
	newWord=digit+letter;
	System.out.println(newWord);
	
}
@Test(enabled=false)
public void StringTesting2()
{
	//conver to upper case
	String s="Hari Om";
	
	String newString="";
	for (int i=0;i<s.length();i++)
	{
		char c=s.charAt(i);
		if(Character.isLowerCase(c))
		{
			c=Character.toUpperCase(c);
		}
		
		newString=newString+c;
	}
		System.out.println("Old String "+s);
		System.out.println("New String "+newString);
	
}
@Test(enabled=false)
public void reverseString()
{
	 //Reverse String
	String s="hariOm";
	String rev="";
	for(int i=s.length()-1;i>=0;i--)
	{
		char c=s.charAt(i);
		rev=rev+c;
		
	}
	System.out.println("Original String= "+s);
	System.out.println("reverse String="+ rev);
} 

@Test(enabled=false)
void StringPalindrom()
{
	String s="ABA";
	String rev="";
	for(int i=s.length()-1;i>=0;i--)
	{
		char c=s.charAt(i);
		rev=rev+c;
		
	}
	System.out.println(rev.length());
	if(rev.equals(s))
		System.out.println(s+" is Palindrom string "+rev);
	else
		System.out.println(s+" Not a Palaindrom "+rev);
}	

@Test(enabled=false)
void vouvelsTest()
{
	String s="IAOUESM";
	for(int i=0;i<s.length();i++)
	{
		char c=s.charAt(i);
		char u=Character.toUpperCase(c);
		if(u=='A'||u=='E'||u=='I'||u=='O'||u=='U')
		System.out.println(u+"  is Vouvel");
		else
		System.out.println(u+" Is not vouvel");
	}
	
}

@Test(enabled=false)
void printASII_value()
{
	String s="1ABCDEFG";
	for(int i=0;i<s.length();i++)
	{
		char c=s.charAt(i);
		System.out.println(c+"  ASCII value="+(int)c);
		c+=2;
		System.out.println(c+"  ASCII value="+(int)c);
	}
	
}
@Test(enabled=false)
void countWord()
{
	String s="Hari Om",w=""; 
	s+=" ";	
int counter=0;
for(int i=0;i<s.length();i++)
{
	char c=s.charAt(i);
	if(c!=' ')
	{
		w+=c;
	}
	else
	{
	System.out.println(w);	
	w="";
	}
}
}

@Test(enabled=false)
void PrintFirstTwoChatacterFromWord()
{
	String s="Ram Krishna Hari";
	s+=" ";
	String w="";
	for(int i=0;i<s.length();i++)
	{
		char c=s.charAt(i);
		
		if(c!=' ')
		{
			w+=c;
		}
		else
		{
			System.out.println(w.substring(0, 2));
			w="";
		}
				
	}
}
@Test(enabled=false)
public void TestLounch() throws InterruptedException
{
	System.setProperty("webdriver.chrome.driver","D:\\software\\chromedriver.exe");
	WebDriver driver=new ChromeDriver();
	driver.get("https://www.facebook.com/");
	driver.manage().window().maximize();
	//driver.findElement(By.xpath("//button[@data-testid='royal_login_button']")).click();
	Thread.sleep(300);
	driver.findElement(By.xpath("//a[@data-testid='open-registration-form-button']")).click();
	Thread.sleep(4000);
	//first way--but industary mostly used it.
	List<WebElement>monthlist=driver.findElements(By.xpath("//select[@id='month']/option"));
	//monthlist.get(5).click();
	//monthlist.size();
	//second way recommended
	WebElement bm=driver.findElement(By.xpath("//select[@id='month']"));
	Select month1=new Select(bm);//select class paremeteriesed contractor
	month1.selectByVisibleText("Mar");
	System.out.println(month1.getFirstSelectedOption().getText());
	month1.selectByValue("10");
}
@Test(enabled=false)
public void palidromString()
{
	
	    String s="ACA";
	    String w="";
	        {
	             for(int i=s.length()-1;i>=0;i--)
	                    {
	                  char c=s.charAt(i);
	                  w=w+c;
	                    } 
	            if(s.equals(w))    
	            {
	                System.out.println(s+" Is Palindrom");
	            }
	            else
	            System.out.println(s+" Is not Palindrom");
	        }  
	       
}
@Test(enabled=false)
public void testString()
{
	String s="Sandip",d="Sandip";
	
	int a=s.compareTo(d);
	{
		System.out.println(a+"");
	}
	
}

@Test (enabled=false)
void stringMethodsTest()
{
	String s ="Ram Krishna Hari";
	String b ="Ram Krishna Hari";
	
	int c=s.compareToIgnoreCase(b);
	int d=s.compareTo(b);
	
	if(d==0)
	System.out.println("Strings are equals");
	else
		System.out.println("Strings are not equals");
	
	//String concat 1. BY + oprator / concat
	String f=s+b;
	System.out.println(f);
	
	String g=s.concat(b);
System.out.println(g);
		int aa=10;
		String ab=aa+b;
		System.out.println(ab);
	
}
@Test(enabled=false)
void arrayTest()
{
	String[] a= {"Om","Sham","Ram"};
	for(int i=0;i<a.length;i++)
	{
	
	System.out.println(a[i]);
	}
	for(String i:a)
	{
		System.out.println(i);
	}
	int[] aa= {10,12,15,17};
	int j=0;
	for(int i:aa)
	{
	j=j+i;
	System.out.println(j);
	}
		
	int ab[][]= {{1,2,3},{4,5,6},{7,8,9}};
	for(int i=0;i<ab.length;i++)
	{
		for(int k=0;k<ab[i].length;k++)
		{
			System.out.print(ab[i][k]);
		}
		System.out.println();
	}
	
}		

@Test (enabled=false)
void findLinks() throws InterruptedException
{

System.setProperty("webdriver.chrome.driver","D:\\software\\chromedriver.exe");
WebDriver driver=new ChromeDriver();
driver.get("https://www.facebook.com/");
driver.manage().window().maximize();
//driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
Thread.sleep(5000);
List<WebElement> allLink=driver.findElements(By.tagName("a"));

System.out.println(allLink.size());
JavascriptExecutor js=(JavascriptExecutor)driver;
for(int i=1;i<47;i++)
	{
WebElement element=allLink.get(i);
	
try {
		System.out.println(allLink.get(i).getAttribute("href")+"   "+allLink.get(i).getText()+"   "+allLink.get(i).isEnabled());
		js.executeScript("arguments[0],scrollIntoView();",element);
	
allLink.get(i).click();
}catch(Exception e)
{
	e.printStackTrace();
}
//Thread.sleep(9000);
driver.get("https://www.facebook.com/");
//driver.navigate().back();
System.out.println( " Link "+i+" Click");
//driver.manage().deleteAllCookies();


}

}
@Test(enabled=false)
void handleWindow() throws InterruptedException
{
	System.setProperty("webdriver.chrome.driver","D:\\software\\chromedriver.exe");
	WebDriver driver=new ChromeDriver();
	driver.get("https://www.naukri.com");
	
	driver.manage().window().maximize();
	driver.manage().timeouts().implicitlyWait(60,TimeUnit.SECONDS);
	JavascriptExecutor js=(JavascriptExecutor)driver;
	Thread.sleep(9000);
//	driver.findElement(By.linkText("Got it")).click();
	WebElement element=driver.findElement(By.linkText("Careers"));
	js.executeScript("arguments[0].scrollIntoView();", element);
	element.click();
	
	String p=driver.getWindowHandle();
	Set<String> s=driver.getWindowHandles();
	Iterator<String> it=s.iterator();
	String m=it.next();
	String n=it.next();
	
	System.out.println(m);
	System.out.println(n);
	driver.switchTo().window(n);
}
@Test(enabled=false)
public void StringTest()
{
	String s1=new String();
	String s2=new String("HariRam");
	char[] s3= {'a','b','c'};
	String s4=new String(s3);
	
	System.out.println("s2 "+s2+" s3 "+s3+" s4 "+s4);//s2 HariRam s3 [C@742d4e15 s4 abc
	String s5="hari";
	String s6="om";
	s5.concat(s6);
	System.out.println(s5);//hari
String s7=s5.concat(s6);//HariOm
	s5=s5+s6;
	System.out.println(s5);//hariOm
	
	
}	
@Test(enabled=false)
public void StringFunctions()
{
	String s="Hari om shanti";
	
	String[] a=s.split(" ");
	System.out.println(a);//@638ef7ed
	for(String i:a)
	{
		System.out.print(i+" ");//out put hari Om Shanti
		
	}
	//Iterator it=a.iterator();only allow with java collection Objects such as ArrayList,set
	

	
}
@Test(enabled=false)
public void stringChar()
{
	String s="Hari Om",n="";
	
	for(int i=0;i<s.length();i++)
	{
		char c=s.charAt(i);
		if(Character.isLowerCase(c))
			n=n+(Character.toUpperCase(c));
		else if(Character.isUpperCase(c))
		n=n+(Character.toLowerCase(c));
		else
		n=n+c;
	}
		
  
	System.out.println(n);
}

@Test(enabled=false)
void StringTestSplit()
{
	String s="Om Shanti Shanti Om";
	int counter=1;
     for(int i=0;i<s.length();i++)
     {
    	 char c=s.charAt(i);
    	 if(Character.isWhitespace(c))
    	 {
    		 counter++;
    	 }
     }
     System.out.println("Number of words "+counter);
     String[] m=s.split(" ");
     System.out.println("Array Size"+m.length);
}
@Test(enabled=false)
void arrayDynamic()
{
	int[] mark=new int[3];
	String[] name=new String[3];
	Scanner sc=new Scanner(System.in);
	for(int i=0;i<mark.length;i++)
	{
	System.out.println("Enter Mark and Enter Name");
		mark[i]=sc.nextInt();
		name[i]=sc.next();
	}
	
	System.out.println(name);//@cd1e646
	for(String i:name)
	{
		System.out.println(i);
	}
	boolean[] b= {true,false,true};
	char c[]= {'a','b','c','e'};
	
}
@Test(enabled=false)
void arrayReverse()
{
	String[] s= {"ram","krishna","hari"};
	String[] a=new String[3];
	for(int i=s.length-1;i>=0;i--)
	{
	System.out.println(s[i]);
     }
}
@Test(enabled=false)
void arrayRoot()
{
	int[]a= {8,12,30,14};
	double[] b=new double[a.length];
	for(int i=0;i<a.length;i++)
	{
		b[i]=Math.sqrt(a[i]);
	}
	for(double d:b)
	{
		System.out.println(d);
	}
	
	
}
@Test(enabled=false)
void arraySeprat()
{
	double d[]= {1.2,3.6,78.77,44.22};
	int c[]=new int[d.length];
	int e[]=new int[d.length];
	for(int i=0;i<d.length;i++)
	{
		c[i]=(int)d[i];
		
		//e[i]=d[i]-c[i]);
		System.out.println(c[i]+"    "+e[i]);
		
	}
	
}
@Test(enabled=false)
void arraySort()
{
	String[] s= {"Om","sai","ram","am"};
	String find="sai";
	for(int i=0;i<s.length;i++)
	{
		if(find.equals(s[i]))
		{
			System.out.println("Match found at array index "+i);
		}
	}
	
	
}

 static void primeNumber(int num)
{
	int n=num,counter=0;
	for(int i=2;i<n;i++)
	{
		//System.out.println(n%i);
		if(n%i==0)
		{
		//	System.out.println(i+"    " +n%i);
			counter++;
			break;
		}
		
	}
	      if(counter>0) {
	     System.out.println(n+" is not Prime number");
          }
	      else
          {
	    	  System.out.println(n+" is  Prime number");
          }
}

 public static void palindrom(int m)
 {
	int n=m;
int temp=n;
	int rev=0,rem;
	while(temp>0)
	{
		rem=temp%10;//.out.println(rem);
		rev=(rev*10)+rem;//System.out.println(rev);
		temp=temp/10;//6System.out.println(temp);
		
		
	}
//System.out.println(temp);
if(rev==n)
{
	System.out.println(n+" is palindrom number");
}else {
	System.out.println(n+" is not a palindrom");
}

 }
@Test (enabled=true)
public void numberTest()
{
	int[] arrayTest= {121,433,435,2,2342,34};
	for(int i=0;i<arrayTest.length;i++)
	{
	
		PracticProgram.primeNumber(arrayTest[i]);
		PracticProgram.palindrom(arrayTest[i]);
	}
	
}
@Test
void infinitloop()
{
	int a=10;
	for(;;)
	{
		
		a++;
		System.out.println(a);
		if(a==1000)
		{
			break;
		}
	}


	
}


}

