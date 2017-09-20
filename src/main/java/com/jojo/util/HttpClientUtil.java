package com.jojo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void testLogger() {
		logger.error("fuck");
	}
	
	public static void main(String[] args) {
		
		HttpClientUtil clientUtil = new HttpClientUtil();
		clientUtil.testLogger();
	}
}
