package com.jojo.zzz;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * 开干
 * 
 * @author jojo
 *
 */
public class Work {

	public static void main(String[] args) {
		System.getProperties().setProperty("webdriver.chrome.driver", "D:\\Workspace\\tools\\chromeDriver\\chromedriver.exe");
		
		WebDriver webDriver = new ChromeDriver();  
		webDriver.get("http://hanhuazu.cc/cartoon/post?id=2292");  
		String responseBody = webDriver.getPageSource();  
		System.out.println(responseBody);  
		webDriver.close();  
	}
}
