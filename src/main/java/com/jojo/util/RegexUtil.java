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
	 * �ݹ�Ŀ¼��ɾ���ļ����з���������ʽ�Ĳ���
	 * 
	 * @author jojo
	 * @param fileDirectory
	 *            �ļ�Ŀ¼
	 * @param regexArray
	 *            ������ʽ����
	 */
	public static void fileRename(File fileDirectory, String[] regexArray) {
		// �ȸ�����Ȼ��ݹ�
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
	
	/***************************** ��HTML��ȡ������ *********************************/
	/**
	 * ����ҳ��������ͼƬ
	 * 
	 * @param regex
	 *            ��ȡͼƬ��������ʽ
	 * @param htmlUrlArray
	 *            ��ҳ��Դ·��
	 * @param savePoint
	 *            ��ȡͼƬ��ı���Ŀ¼���������
	 * @param threadNumber
	 *            �����߳�����Ĭ��7��
	 */
	public static void getPicFromHTML(String regex, String[] htmlUrlArray, String savePoint, int threadNumber) {
		try {
			// ����ͼƬ��url
			List<String> picUrlList = new ArrayList<String>();
			for (String htmlUrl : htmlUrlArray) {
				picUrlList.addAll(getLinkByRegex(regex, htmlUrl));
			}

			// ���乤�������д��Ż���
			if (threadNumber == 0 || threadNumber == 1) {
				threadNumber = 7;
			}
			// ƽ��������
			int workload = picUrlList.size() / threadNumber;
			// ɨβ������
			int workload2 = picUrlList.size() % threadNumber;

			// ��ʼ��������ɨβ
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
	 * ����������ʽ��ȡ�ļ��е����ӣ���ȡ����ȡ�����ṩ��������ʽ
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
			// ����ҳ���ݶ����ڴ�
			StringBuilder htmlString = new StringBuilder();
			URL url = new URL(htmlUrl);
			URLConnection connection = url.openConnection();
			bufferedIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String temp = null;
			while ((temp = bufferedIn.readLine()) != null) {
				htmlString.append(temp);
			}

			// ��ʼƥ��
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(htmlString);
			while (m.find()) {
				String str = m.group();
				list.add(str);
				/*
				 * ������Һ�Ī���������������ҳ�����URL��Ȼ�ظ�һ�Σ���֪��Ϊʲô
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
 * �����߳�
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
				System.out.println("��ʼ�����" + startNumber + "��");
				String realSavePoint = savePoint + File.separator + startNumber + ".jpg";
				// list��������0��ʼ
				this.savePicToLocal(picUrlList.get(startNumber - 1), realSavePoint);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���ݸ�����url�ӷ�������ȡͼƬ����������ָ��λ�á�ע�⣬ָ��Ŀ¼������ڡ�<br>
	 * 
	 * @param picUrl
	 * @param savePoint
	 * @throws Exception
	 */
	public void savePicToLocal(String picUrl, String savePoint) throws Exception {
		BufferedOutputStream bufferedOut = null;
		BufferedInputStream bufferedIn = null;
		// ��� try ��ֻ��Ϊ�˹ر���Դ
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
			// ������int����ȻͼƬ��ʧ��
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
