package com.jojo.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
	
	/**
	 * 递归目录，删除文件名中符合正则表达式的部分
	 * 
	 * @author jojo
	 * @param fileDirectory
	 *            文件目录
	 * @param regexArray
	 *            正则表达式数组
	 */
	public static void fileRename(File fileDirectory, String[] regexArray) {
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
	
	/***************************** 从HTML获取磁力链 *********************************/
	/**
	 * 从网页批量保存图片
	 * 
	 * @param regex
	 *            提取图片的正则表达式
	 * @param htmlUrlArray
	 *            网页的源路径
	 * @param savePoint
	 *            提取图片后的保存目录，必须存在
	 * @param threadNumber
	 *            工作线程数，默认7条
	 */
	public static void getPicFromHTML(String regex, String[] htmlUrlArray, String savePoint, int threadNumber) {
		try {
			// 所有图片的url
			List<String> picUrlList = new ArrayList<String>();
			for (String htmlUrl : htmlUrlArray) {
				picUrlList.addAll(getLinkByRegex(regex, htmlUrl));
			}

			// 分配工作量（有待优化）
			if (threadNumber == 0 || threadNumber == 1) {
				threadNumber = 7;
			}
			// 平均工作量
			int workload = picUrlList.size() / threadNumber;
			// 扫尾工作量
			int workload2 = picUrlList.size() % threadNumber;

			// 开始工作，先扫尾
			Worker worker = new Worker(picUrlList.size() - workload2, picUrlList.size(), picUrlList, savePoint);
			Thread thread = new Thread(worker);
			thread.start();

			int temp = 1;
			for (int i = 0; i < threadNumber - 1; i++) {
				worker = new Worker(temp, workload - 1, picUrlList, savePoint);
				thread = new Thread(worker);
				thread.start();
				temp += workload;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据正则表达式获取文件中的链接，获取内容取决于提供的正则表达式
	 * 
	 * @param regex
	 * @param htmlUrl
	 * @return
	 * @throws Exception
	 */
	public static List<String> getLinkByRegex(String regex, String htmlUrl) throws Exception {
		List<String> list = new ArrayList<String>();
		BufferedReader bufferedIn = null;
		try {
			// 将网页内容读进内存
			StringBuilder htmlString = new StringBuilder();
			URL url = new URL(htmlUrl);
			URLConnection connection = url.openConnection();
			bufferedIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String temp = null;
			while ((temp = bufferedIn.readLine()) != null) {
				htmlString.append(temp);
			}

			// 开始匹配
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(htmlString);
			while (m.find()) {
				String str = m.group();
				list.add(str);
				/*
				 * 加这个我很莫名奇妙，但不加最后找出来的URL必然重复一次，不知道为什么
				 */
				m.find();
			}
		} finally {
			if (bufferedIn != null) { bufferedIn.close(); }
		}
		return list;
	}
}

/**
 * 工作线程
 */
class Worker implements Runnable {

	private int startNumber;
	private int endNumber;
	private List<String> picUrlList;
	private String savePoint;

	public Worker(int startNumber, int endNumber, List<String> picUrlList, String savePoint) {
		super();
		this.startNumber = startNumber;
		this.endNumber = endNumber;
		this.picUrlList = picUrlList;
		this.savePoint = savePoint;
	}

	@Override
	public void run() {
		try {
			for (; startNumber < endNumber + 1; startNumber++) {
				System.out.println("开始处理第" + startNumber + "张");
				String realSavePoint = savePoint + File.separator + startNumber + ".jpg";
				// list的索引从0开始
				this.savePicToLocal(picUrlList.get(startNumber - 1), realSavePoint);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据给定的url从服务器获取图片，并保存在指定位置。注意，指定目录必须存在。<br>
	 * 
	 * @param picUrl
	 * @param savePoint
	 * @throws Exception
	 */
	public void savePicToLocal(String picUrl, String savePoint) throws Exception {
		BufferedOutputStream bufferedOut = null;
		BufferedInputStream bufferedIn = null;
		// 这个 try 块只是为了关闭资源
		try {
			URL url = new URL(picUrl);
			URLConnection connection = url.openConnection();
			connection.connect();

			// IO
			File file = new File(savePoint);
			if (!file.exists()) {
				file.createNewFile();
			}
			bufferedIn = new BufferedInputStream(connection.getInputStream());
			bufferedOut = new BufferedOutputStream(new FileOutputStream(file));
			// 必须用int，不然图片会失真
			int temp = 0;
			while ((temp = bufferedIn.read()) != -1) {
				bufferedOut.write(temp);
			}
		} finally {
			if (bufferedIn != null) { bufferedIn.close(); }
			if (bufferedOut != null) { bufferedOut.close(); }
		}
	}
}
