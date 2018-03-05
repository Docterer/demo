package com.jojo.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class RegexUtil {
	
	private static Logger logger = LoggerFactory.getLogger(RegexUtil.class);
	
	/**
	 * 文件重命名，去除符合正则表达式的内容
	 * 
	 * @author jojo
	 * @param fileDirectory
	 *            文件目录
	 * @param regexArray
	 *            正则表达式数组
	 */
	public static void fileRename(File fileDirectory, String[] regexArray) {
		if(ArrayUtils.isEmpty(regexArray) || fileDirectory == null) {
			System.err.println("参数不得为空");
			return ;
		}
		
		// 先改名，然后递归
		String path = fileDirectory.getAbsolutePath();
		for (String regex : regexArray) {
			path = path.replaceAll(regex, "");
		}
		
		fileDirectory.renameTo(new File(path));

		if (fileDirectory.isDirectory()) {
			for (File file : fileDirectory.listFiles()) {
				fileRename(file, regexArray);
			}
		}
	}
	
	
	
	
	
	
	/**
	 * 根据正则表达式，递归删除符合条件的文件
	 * 
	 * @param filePath
	 * @param regexArr
	 */
	public static void fileDelete(String filePath, String[] regexArr) {
		if(StringUtils.isBlank(filePath) || StringUtils.isAnyBlank(regexArr)) {
			System.err.println("参数不得为空");
			return ;
		}
		
		File file = new File(filePath);
		if(file.isDirectory()) {
			for(File tempFile : file.listFiles()) {
				fileDelete(tempFile.getAbsolutePath(), regexArr);
			}
		}
		
		for(String str : regexArr) {
			if(file.getAbsolutePath().matches(str)) {
				file.delete();
				break;
			}
		}
	}
	
	
	
	
	
	
	/**
	 * 解压文件至目标文件夹
	 * 
	 * @param filePath
	 * @param destinationPath
	 */
	public static void fileDecompress(String filePath, String destinationPath) throws Exception {
		File file = new File(filePath);
		if (!file.exists()) {
			System.err.println("要解压的文件不存在");
			return;
		} else if (StringUtils.isBlank(destinationPath) || !new File(destinationPath).isDirectory()) {
			System.err.println("保存位置无效");
			return;
		}

		BufferedInputStream bufferedIn = null;
		BufferedOutputStream bufferedOut = null;
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(file);
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = enumeration.nextElement();
				
				// 生成特定文件名
				String fileName = createFileName(destinationPath, zipFile, zipEntry);
				
				// 根据生成的文件名，创建文件或目录
				File targetFile = createDirectoryOrFileByName(fileName);
				if(targetFile.isDirectory()) continue;

				// 读取内容
				bufferedIn = new BufferedInputStream(zipFile.getInputStream(zipEntry));
				bufferedOut = new BufferedOutputStream(new FileOutputStream(targetFile));

				byte[] container = new byte[10 * 1024];
				int lengthOfReadByte = 0;
				while ((lengthOfReadByte = bufferedIn.read(container)) != -1) {
					bufferedOut.write(container, 0, lengthOfReadByte);
				}
				bufferedOut.flush();
			}
		} finally {
			if (bufferedIn != null) bufferedIn.close();
			if (bufferedOut != null) bufferedOut.close();
			if (zipFile != null) zipFile.close();
		}
	}
	/**
	 * 生成特定文件名
	 * @param destinationPath
	 * @param zipFile
	 * @param zipEntry
	 * @return
	 */
	private static String createFileName(String destinationPath, ZipFile zipFile, ZipEntry zipEntry) {
		StringBuffer result = new StringBuffer();
		result.append(destinationPath + File.separator);
		// 提取压缩文件名，并作为文件夹
		String zipFileName = zipFile.getName();
		int temp = zipFileName.lastIndexOf(File.separator);
		// 如果存在文件分隔符
		if (temp != -1) {
			result.append(zipFileName.substring(temp + 1).split("\\.")[0]);
		} else {
			result.append(zipFileName);
		}
		result.append(File.separator);
		result.append(zipEntry.getName());

		return result.toString();
	}
	/**
	 * 根据生成的文件名，创建文件或目录
	 * @param fileName
	 * @return 
	 * @throws IOException 
	 */
	private static File createDirectoryOrFileByName(String fileName) throws IOException {
		if(StringUtils.isBlank(fileName)) {
			return null ;
		}
		File file = new File(fileName);
		File parent = file.getParentFile();
		if(parent != null && !parent.exists()) {
			parent.mkdirs();
		}
		// 如果是目录，则不需要读取内容
		if (!fileName.matches(".+\\.\\w+$")) {
			if (!file.exists()) file.mkdirs();
		} else {
			file.createNewFile();
		}
		return file;
	}
	
	
	/**
	 * 把文件读成字符串
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static String getStringFromFile(String filePath) throws Exception {
		if (StringUtils.isBlank(filePath)) {
			System.err.println("必填项不能为空");
		}
		BufferedReader bufferedIn = null;
		StringBuilder fileString = new StringBuilder();
		try {
			bufferedIn = new BufferedReader(new FileReader(filePath));
			String temp = null;
			while ((temp = bufferedIn.readLine()) != null) {
				fileString.append(temp);
			}
		} finally {
			if (bufferedIn != null) bufferedIn.close();
		}
		return fileString.toString();
	}
	
	/**
	 * 取url最后一部分，但不包括参数，通常用来获取文件名
	 * 
	 * @param url
	 * @return
	 */
	public static String getLastPartOfUrl(String url) {
		if (StringUtils.isBlank(url)) {
			logger.error("URL为空");
			return null;
		}
		int questionMark = url.indexOf("?");
		if (questionMark != -1) {
			url = url.substring(0, url.indexOf("?"));
		}
		String[] arr = url.split("/");
		if (ArrayUtils.isEmpty(arr)) {
			logger.error("该URL不合法或非restful风格");
			return null;
		}
		return arr[arr.length - 1];
	}

	/**
	 * 取url中文件后缀名
	 * 
	 * @param url
	 * @return
	 */
	public static String getSuffixFromUrl(String url) {
		if (StringUtils.isBlank(url)) {
			logger.error("URL为空");
			return null;
		}
		int questionMark = url.indexOf("?");
		if (questionMark != -1) {
			url = url.substring(0, url.indexOf("?"));
		}
		String[] arr = url.split("\\.");
		if (ArrayUtils.isEmpty(arr)) {
			logger.error("该URL中不包含文件名");
			return null;
		}
		return arr[arr.length - 1];
	}
	
	/**
	 * 获取HTML字符串
	 * @param url
	 * @return
	 * @throws Exception 
	 */
	public static String getStringFromUrl(String url) throws Exception {
		if(StringUtils.isBlank(url)) {
			System.err.println("必填项不能为空");
		}
		BufferedReader bufferedIn = null;
		String result = null;
		try {
			StringBuilder htmlString = new StringBuilder();
			URL httpUrl = new URL(url);
			URLConnection connection = httpUrl.openConnection();
			bufferedIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String temp = null;
			while ((temp = bufferedIn.readLine()) != null) {
				htmlString.append(temp);
			}
			result = htmlString.toString();
		} finally {
			if(bufferedIn != null) bufferedIn.close();
		}
		return result;
	}
	
	/**
	 * 根据正则表达式过滤字符串
	 * @param rawData
	 * @param regex
	 * @return
	 */
	public static List<String> filterByRegex(String rawData, String... regex) {
		if (ArrayUtils.isEmpty(regex) || StringUtils.isBlank(rawData)) {
			System.err.println("必填项不能为空");
		}
		List<String> resultList = Lists.newArrayList();
		// 开始匹配
		for (String reg : regex) {
			Pattern p = Pattern.compile(reg);
			Matcher m = p.matcher(rawData);
			while (m.find()) {
				String str = m.group();
				System.out.println("根据正则表达式" + reg + "找到目标" + str);
				resultList.add(str);
				// 不懂为甚么有时候要在find一次
//				m.find();
			}
		}
		System.out.println("总计：" + resultList.size() + "个");
		return resultList;
	}
	
	/**
	 * 批量
	 * @param picUrlList
	 * @param savePoint
	 * @throws Exception
	 */
	public static void batchSavePicToLocal(List<String> picUrlList, String savePoint, int startPosition) throws Exception {
		if(CollectionUtils.isEmpty(picUrlList)) {
			return ;
		}
		int count = startPosition;
		for (String picUrl : picUrlList) {
			System.out.println("正在保存" + (++count) + "个文件");
			savePicToLocal(picUrl, savePoint + File.separator + count + ".jpg");
		}
	}
	
	/**
	 * 根据给定的url从服务器获取图片，并保存在指定位置。<br>
	 * 
	 * @param picUrl
	 * @param savePoint
	 * @throws Exception
	 */
	public static void savePicToLocal(String picUrl, String savePoint) throws Exception {
		BufferedOutputStream bufferedOut = null;
		BufferedInputStream bufferedIn = null;
		File file = null;
		// 这个 try 块只是为了关闭资源
		try {
			// IO
			file = new File(savePoint);
			File parent = file.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			
			URL url = new URL(picUrl);
			URLConnection connection = url.openConnection();
			connection.connect();
			
			bufferedIn = new BufferedInputStream(connection.getInputStream());
			bufferedOut = new BufferedOutputStream(new FileOutputStream(file));

			byte[] container = new byte[100 * 1024];
			int lengthOfReadByte = 0;
			while ((lengthOfReadByte = bufferedIn.read(container)) != -1) {
				bufferedOut.write(container, 0, lengthOfReadByte);
			}
			bufferedOut.flush();
			System.out.println(savePoint+" 保存成功");
		}catch(MalformedURLException e){
			System.err.println("此url无法访问："+picUrl+"\r\n文件"+savePoint+"已删除");
			file.delete();
		}
		finally {
			IOUtils.closeQuietly(bufferedIn);
			IOUtils.closeQuietly(bufferedOut);
		}
	}
	/**
	 * 
	 * @param filePath
	 *            "D:\\Workspace\\test\\1.txt"
	 * @param startPos
	 *            42
	 * @param savePoint
	 *            图片保存位置
	 * @param regex
	 *            "http://[\\.\\w/]+jpg"
	 * @throws Exception
	 */
	public static void savePicToLocalByUrlWhichGetFromFile(String filePath, int startPos, String savePoint,
			String... regex) throws Exception {
		String fileString = RegexUtil.getStringFromFile(filePath);
		List<String> urlList = RegexUtil.filterByRegex(fileString, regex);
		RegexUtil.batchSavePicToLocal(urlList, savePoint, startPos);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * @param clazz
	 */
	public static void showGettersOrSetters(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();

		List<Field> list = Arrays.asList(fields);
		Collections.sort(list, new Comparator<Field>() {
			@Override
			public int compare(Field o1, Field o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		for (Field field : fields) {
			StringBuffer sb = new StringBuffer();
			sb.append("temp.set");
			sb.append(field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
			sb.append("(").append(" ").append(");");
			System.out.println(sb.toString());
		}
	}

}
