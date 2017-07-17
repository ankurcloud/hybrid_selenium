package com.hybridFramework.testBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;



public class TestBase {
   public WebDriver driver;
    public Properties OR;
    public File f1;
    public FileInputStream file;
    
    public static ExtentReports extent;
    public static ExtentTest test;
    public ITestResult result;
    
    static{
    	Calendar calendar= Calendar.getInstance();
    	SimpleDateFormat formator=new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
    	extent=new ExtentReports(System.getProperty("user.dir")+"/src/main/java/com/hybridFramework/reports/test"+formator.format(calendar.getTime())+".html", false);    }
    
    //3.0.1
    //FF: 47.0.2
	public void getBrowser(String browser){
		if(System.getProperty("os.name").contains("Window")){
			if(browser.equalsIgnoreCase("firefox")){
				System.out.println(System.getProperty("user.dir"));
				System.setProperty("webdriver.gecko.driver",System.getProperty("user.dir")+"/Drivers/geckodriver.exe");
			    driver=new FirefoxDriver();
			}
			else if(browser.equalsIgnoreCase("Chrome")){
			   System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir")+"/Drivers/chromedriver.exe");
			   driver=new ChromeDriver();
		}
	}
}
	public void loadpropertiesFile() throws Throwable{
		OR=new Properties();
		f1=new File(System.getProperty("user.dir")+"/src/main/java/com/hybridFramework/config/config.properties");
	    file=new FileInputStream(f1);
	    OR.load(file);
	    
	    
		f1=new File(System.getProperty("user.dir")+"/src/main/java/com/hybridFramework/config/or.properties");
	    file=new FileInputStream(f1);
	    OR.load(file);
	}
	
	public String getScreenshot(String imageName) throws IOException{
		File image = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		String imagelocation=System.getProperty("user.dir")+"/src/main/java/com/hybridFramework/screenshot/";
		Calendar calender = Calendar.getInstance();
		SimpleDateFormat formater=new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
		String actualImageName=imagelocation+imageName+"_"+formater.format(calender.getTime())+".png";
		File destFile=new File(actualImageName);
		FileUtils.copyFile(image, destFile);
		return actualImageName;
	}
	public WebElement waitForelement(WebDriver driver,long time, WebElement element){
		WebDriverWait wait = new WebDriverWait(driver,time);
		return wait.until(ExpectedConditions.elementToBeClickable(element));
	}
	
	public WebElement waitForelementWithPollingInterval(WebDriver driver,long time, WebElement element){
		WebDriverWait wait = new WebDriverWait(driver,time);
		wait.pollingEvery(5, TimeUnit.SECONDS);
		wait.ignoring(NoSuchElementException.class);
		return wait.until(ExpectedConditions.elementToBeClickable(element));
		
	}
	
	public void getresult(ITestResult result) throws IOException{
		if(result.getStatus()==ITestResult.SUCCESS){
			test.log(LogStatus.PASS, result.getName()+"test is pass");
			
		}
		else if(result.getStatus()==ITestResult.SKIP){
			test.log(LogStatus.SKIP, result.getName()+"test is skip");
		}
		else if(result.getStatus()==ITestResult.FAILURE){
			test.log(LogStatus.FAIL, result.getName()+"test is Fail"+result.getThrowable());
			String screen=getScreenshot("");
			test.log(LogStatus.FAIL, test.addScreenCapture(screen));
			
		}
		else if(result.getStatus()==ITestResult.STARTED){
			test.log(LogStatus.INFO, result.getName()+"Test is started");
		}	
	}
	
	@AfterMethod()
	public void afterMethod(ITestResult result) throws IOException{
		getresult(result);
	}
	
	@BeforeMethod()
		public void beforeMethod(Method result){
			test=extent.startTest(result.getName());
			test.log(LogStatus.INFO, result.getName()+"test started");
		}
	
	@AfterClass(alwaysRun=true)
	public void endTest(){
		driver.quit();
		extent.endTest(test);
		extent.flush();
	}
	
	public static void main(String[] args) throws Throwable {
		TestBase test=new TestBase();
		//test.getBrowser("chrome");
		test.loadpropertiesFile();
		System.out.println(test.OR.getProperty("url"));
		System.out.println(test.OR.get("testname"));
	}
}
  