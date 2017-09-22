package com.jojo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class DateUtil {

	/**
	 * ʱ���ת����
	 * 
	 * @param timestamp
	 * @param format
	 *            ���Դ�null��Ĭ�ϸ�ʽΪyyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String timestamp2Date(String timestamp, String format) {
		long temp = NumberUtils.toLong(timestamp) * 1000L;
		Date date = new Date(temp);
		if (StringUtils.isBlank(format)) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

}
