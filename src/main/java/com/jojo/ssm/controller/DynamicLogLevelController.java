package com.jojo.ssm.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.jojo.ssm.pojo.LogVO;
import com.jojo.ssm.pojo.Response;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

/**
 * 动态查看log的等级
 */
@Controller
@RequestMapping("/api/log")
public class DynamicLogLevelController {

	/**
	 * 获取日志列表
	 * 
	 * @param logName
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ResponseBody
	public Response list(@RequestParam String logName) {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		List<ch.qos.logback.classic.Logger> loggerList = loggerContext.getLoggerList();
		List<LogVO> allLoggers = Lists.newArrayList();
		for (ch.qos.logback.classic.Logger log : loggerList) {
			if (log.getLevel() != null) {
				if (StringUtils.isBlank(logName)) {
					LogVO LogVO = new LogVO();
					String level = log.getEffectiveLevel().levelStr;
					LogVO.setLevel(level);
					LogVO.setName(log.getName());
					allLoggers.add(LogVO);
				} else {
					if (StringUtils.containsIgnoreCase(log.getName(), logName)) {
						LogVO LogVO = new LogVO();
						String level = log.getEffectiveLevel().levelStr;
						LogVO.setLevel(level);
						LogVO.setName(log.getName());
						allLoggers.add(LogVO);
					}
				}
			}
		}
		return new Response(Response.SCUCCESS, "");
	}

	/**
	 * 等级更新
	 * 
	 * @param logVO
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public Response update(LogVO logVO) {
		Response response = new Response();
		ch.qos.logback.classic.Logger julLogger = getLogger(logVO.getName());
		julLogger.setLevel(Level.toLevel(logVO.getLevel()));
		response.setSuccessMessage("更新成功！");
		return response;
	}

	/**
	 * 删除日志
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "/detele", method = RequestMethod.POST)
	@ResponseBody
	public Response detele(@RequestParam String name) {
		Response response = new Response();
		ch.qos.logback.classic.Logger julLogger = getLogger(name);
		julLogger.setLevel(null);
		response.setSuccessMessage("删除成功！");
		return response;
	}

	/**
	 * 通过日志名称获取指定数据
	 * 
	 * @param loggerName
	 * @return
	 */
	private ch.qos.logback.classic.Logger getLogger(String loggerName) {
		Logger julLogger = LoggerFactory.getLogger(loggerName);
		if (julLogger instanceof ch.qos.logback.classic.Logger) {
			return (ch.qos.logback.classic.Logger) julLogger;
		}
		return null;
	}
}
