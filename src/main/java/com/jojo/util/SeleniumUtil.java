package com.jojo.util;

import static com.jojo.util.arg.HttpConstant.SHU_HUI_PREFIX_CARTOON_NUM;
import static com.jojo.util.arg.HttpConstant.SHU_HUI_PREFIX_CARTOON_NUM_DETAIL;
import static com.jojo.util.arg.HttpConstant.SHU_HUI_PREFIX_PIC;
import static com.jojo.util.arg.HttpConstant.TAG_a;
import static com.jojo.util.arg.HttpConstant.TAG_img;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Lists;

public class SeleniumUtil {

	private static final String CHROME_DRIVER = "webdriver.chrome.driver";

	private static final String CHROME_DRIVER_LOCATION = "D:\\Workspace\\tools\\chromeDriver\\chromedriver.exe";

	private static Logger logger = LoggerFactory.getLogger(SeleniumUtil.class);

	static {
		System.getProperties().setProperty(CHROME_DRIVER, CHROME_DRIVER_LOCATION);
	}

	/**
	 * 取一部漫画中，每一话的地址
	 * 
	 * @param url
	 * @return
	 */
	public static List<String> getMangaUrlFromShuHui(String url) {
		WebDriver webDriver = new ChromeDriver();
		webDriver.get(url);

		List<String> hrefList = Lists.newArrayList();
		List<WebElement> aTagList = webDriver.findElements(By.tagName(TAG_a));
		logger.error("共获取到{}个a标签，下面开始过滤", aTagList.size());
		for (WebElement a : aTagList) {
			String href = a.getAttribute("href");
			if (StringUtils.startsWith(href, SHU_HUI_PREFIX_CARTOON_NUM)) {
				logger.error("当前链接为：{} 符合条件", href);
				hrefList.add(href);
			} else {
				logger.error("当前链接为：{} 不符合条件，剔除", href);
			}
		}
		logger.error("过滤后的数量为{}", hrefList.size());

		webDriver.close();
		return hrefList;
	}

	/**
	 * 取某一话的真实地址
	 * 
	 * @param hrefList
	 * @return
	 */
	public static List<String> getRealMangaUrlFromShuHui(List<String> hrefList) {
		WebDriver webDriver = new ChromeDriver();
		List<String> realMangaUrlList = Lists.newArrayList();
		for (String href : hrefList) {
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

		webDriver.close();
		return realMangaUrlList;
	}

	/**
	 * 取所有图片的url，同时返回某一话的标题，方便生成目录
	 * 
	 * @param realMangaUrlList
	 * @return
	 */
	public static Map<String, List<String>> getAllPicUrl(List<String> realMangaUrlList) {
		WebDriver webDriver = new ChromeDriver();

		Map<String, List<String>> allPicUrlMap = Maps.newHashMap();
		for (String realMangaUrl : realMangaUrlList) {
			logger.error("准备访问url：{}", realMangaUrl);
			webDriver.get(realMangaUrl);

			List<WebElement> imgElementList = webDriver.findElements(By.tagName(TAG_img));
			List<String> srcList = Lists.newArrayList();
			for (WebElement img : imgElementList) {
				String src = img.getAttribute("src");
				// vue.js的懒加载，某些图片会放置在这个属性里，但这个属性是不会有http://前缀的
				String dataSrc = img.getAttribute("data-src");
				if (StringUtils.startsWith(src, SHU_HUI_PREFIX_PIC)) {
					logger.error("当前链接为：{} 符合条件", src);
					srcList.add(src);
				} else if (StringUtils.contains(dataSrc, "pic")) {
					dataSrc = "http:" + dataSrc;
					srcList.add(dataSrc);
					logger.error("当前链接为：{} 符合条件", dataSrc);
				} else {
					logger.error("此对象没有找到合适的图片地址：{}", img.toString());
				}
			}

			Document document = Jsoup.parse(webDriver.getPageSource());
			allPicUrlMap.put(document.title(), srcList);
		}
		
		webDriver.close();
		return allPicUrlMap;
	}

	/**
	 * 保存至本地
	 * @param allPicUrlMap
	 * @throws Exception
	 */
	public static void saveToLocal(Map<String, List<String>> allPicUrlMap) throws Exception {
		for (Map.Entry<String, List<String>> entry : allPicUrlMap.entrySet()) {
			String directory = FileUtil.BASE_SAVE_DIRECTORY + FileUtil.filterInvalidCharacter(entry.getKey()) + File.separator;
			for (String picUrl : entry.getValue()) {
				String fileName = directory + RegexUtil.getLastPartOfUrl(picUrl);
				FileUtil.createNewFileFromInternet(picUrl, fileName);
			}
		}
	}

	/**
	 * fire
	 * @param url
	 * @throws Exception
	 */
	public static void down(String url) throws Exception {
		List<String> hrefList = getMangaUrlFromShuHui(url);
		List<String> realMangaUrlList  =getRealMangaUrlFromShuHui(hrefList);
		Map<String, List<String>> allPicUrlMap = getAllPicUrl(realMangaUrlList);
		saveToLocal(allPicUrlMap);
	}
	
}
