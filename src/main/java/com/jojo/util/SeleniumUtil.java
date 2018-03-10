package com.jojo.util;

import static com.jojo.util.arg.HttpConstant.SHU_HUI_PREFIX_CARTOON_NUM;
import static com.jojo.util.arg.HttpConstant.SHU_HUI_PREFIX_CARTOON_NUM_DETAIL;
import static com.jojo.util.arg.HttpConstant.SHU_HUI_PREFIX_PIC;
import static com.jojo.util.arg.HttpConstant.TAG_a;
import static com.jojo.util.arg.HttpConstant.TAG_img;
import static com.jojo.util.arg.HttpConstant.TAG_title;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class SeleniumUtil {

	private static final String CHROME_DRIVER = "webdriver.chrome.driver";

	private static final String CHROME_DRIVER_LOCATION = "D:\\Workspace\\tools\\chromeDriver\\chromedriver.exe";

	private static Logger logger = LoggerFactory.getLogger(SeleniumUtil.class);

	static {
		System.getProperties().setProperty(CHROME_DRIVER, CHROME_DRIVER_LOCATION);
	}

	public static void main(String[] args) throws Exception {
		// 源头
		String url = "http://www.ishuhui.com/cartoon/book/11";

		WebDriver webDriver = new ChromeDriver();
		webDriver.get(url);

		/*
		 * step 1，取每一话的地址
		 */
		List<WebElement> aTagList = webDriver.findElements(By.tagName(TAG_a));
		logger.error("共获取到{}个a标签，下面开始过滤", aTagList.size());
		List<String> hrefList = Lists.transform(aTagList, new Function<WebElement, String>() {
			@Override
			public String apply(WebElement input) {
				String href = input.getAttribute("href");
				if (StringUtils.startsWith(href, SHU_HUI_PREFIX_CARTOON_NUM)) {
					logger.error("当前链接为：{} 符合条件", href);
					return href;
				} else {
					logger.error("当前链接为：{} 不符合条件，剔除", href);
					return null;
				}
			}
		});

		/*
		 * step 2，取每一话的真实地址
		 */
		List<String> realMangaUrlList = Lists.newArrayList();
		for (String href : hrefList) {
			// 由于偷懒，不高兴写for循环，直接用guava转list，所以可能存在null对象
			if (StringUtils.isBlank(href)) {
				continue;
			}
			logger.error("开始访问{}", href);
			webDriver.get(href);
			List<WebElement> tempList = webDriver.findElements(By.tagName(TAG_a));
			for (WebElement temp : tempList) {
				String realUrl = temp.getAttribute("href");
				if (StringUtils.startsWith(realUrl, SHU_HUI_PREFIX_CARTOON_NUM_DETAIL)) {
					logger.error("找到真正的漫画url：{}", realUrl);
					realMangaUrlList.add(realUrl);
				}
			}
		}
		logger.error("真实漫画Url的个数：{}", realMangaUrlList.size());

		/*
		 * step 3，获取所有图片url的地址
		 */
		Map<String, List<String>> allPicUrlMap = Maps.newHashMap();
		for (String realMangaUrl : realMangaUrlList) {
			logger.error("准备访问url：{}", url);
			webDriver.get(realMangaUrl);

			List<WebElement> imgElementList = webDriver.findElements(By.tagName(TAG_img));
			List<String> srcList = Lists.transform(imgElementList, new Function<WebElement, String>() {
				@Override
				public String apply(WebElement input) {
					String href = input.getAttribute("src");
					if (StringUtils.startsWith(href, SHU_HUI_PREFIX_PIC)) {
						logger.error("当前链接为：{} 符合条件", href);
						return href;
					} else {
						logger.error("当前链接为：{} 不符合条件，剔除", href);
						return null;
					}
				}
			});

			WebElement title = webDriver.findElement(By.tagName(TAG_title));
			allPicUrlMap.put(title.getText(), srcList);
		}

		/*
		 * step 4，保存图片
		 */
		for (Map.Entry<String, List<String>> picUrlEntry : allPicUrlMap.entrySet()) {
			String fileName = FileUtil.BASE_SAVE_DIRECTORY + File.separator + picUrlEntry.getKey() + File.separator;
			List<String> picUrlList = picUrlEntry.getValue();
			for (String picUrl : picUrlList) {
				// 同上，guava转的
				if (StringUtils.isBlank(picUrl)) {
					continue;
				}
				fileName += RegexUtil.getLastPartOfUrl(picUrl);
				FileUtil.createNewFileFromInternet(picUrl, fileName);
			}
		}

		// close
		webDriver.close();
	}
}
