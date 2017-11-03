package com.jojo.util;

import java.io.File;

/**
 * 开干
 * 
 * @author jojo
 *
 */
public class Work {
	public static void main(String[] args) {
		
		String fileDirectory = "D:\\BaiduYunDownload";
		
		String[] regexArray = {"\\[www\\.java1234\\.com\\]"};
		
		RegexUtil.fileRename(new File(fileDirectory), regexArray);
	}
}
