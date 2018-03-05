package com.jojo.util;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeleniumUtil {

	private static final String CHROME_DRIVER = "webdriver.chrome.driver";

	private static final String CHROME_DRIVER_LOCATION = "D:\\Workspace\\tools\\chromeDriver\\chromedriver.exe";
	
	private static final String URL = "http://www.sis001.com/forum/viewthread.php?tid=9742034&extra=page%3D3%26amp%3Bfilter%3Ddigest";

	private static Logger logger = LoggerFactory.getLogger(SeleniumUtil.class);

	static {
		System.getProperties().setProperty(CHROME_DRIVER, CHROME_DRIVER_LOCATION);
	}

	public static void main(String[] args) {
		WebDriver webDriver = new ChromeDriver();
		webDriver.get(URL);
		List<WebElement> webElementList = webDriver.findElements(By.tagName("img"));

		if (CollectionUtils.isEmpty(webElementList)) {
			logger.error("标签为空");
			return;
		}
		for (WebElement element : webElementList) {
			String picUrl = element.getAttribute("src");
			if (StringUtils.contains(picUrl, "load")) {
				picUrl = "http:" + element.getAttribute("data-src");
			}
			System.out.println(picUrl);
		}
		webDriver.close();
	}
}
