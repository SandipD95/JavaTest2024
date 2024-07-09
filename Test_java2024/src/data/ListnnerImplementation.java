package data;
import java.io.File;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import Test.Democlass;

public class ListnnerImplementation extends Democlass implements ITestListener {

	public void onFinish(ITestContext contextFinish) {
		System.out.println("onFinish method finished");

		}

		@Override
		public void onStart(ITestContext contextStart) {
		System.out.println("onStart method started");
		}

		@Override
		public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		System.out.println("Method failed with certain success percentage"+ result.getName());

		}

		@Override
		public void onTestFailure(ITestResult result) {
		System.out.println("Method failed"+ result.getName());
		File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		String screenshotBase64 = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BASE64);

		}

		@Override
		public void onTestSkipped(ITestResult result) {
		System.out.println("Method skipped"+ result.getName());

		}

		@Override
		public void onTestStart(ITestResult result) {
		System.out.println("Method started"+ result.getName());

		}

		@Override
		public void onTestSuccess(ITestResult result) {
		System.out.println("Method passed"+ result.getName());

		}

		
}
