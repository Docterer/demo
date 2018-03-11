package com.jojo.zzz;

import java.io.File;

import com.jojo.util.FileUtil;

/**
 * 开干
 * 
 * @author jojo
 *
 */
public class Work {

	public static void main(String[] args) throws Exception {
		
		FileUtil.fileRename(new File("D:\\Workspace\\test\\1"), "漢化組\\.cc");
	}
}
