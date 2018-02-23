package com.jojo.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 开干
 * 
 * @author jojo
 *
 */
public class Work {
	public static void main(String[] args) {
		// ExecutorService executor = Executors.newFixedThreadPool(15);

		@SuppressWarnings("resource")
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:config/applicationContext-bean.xml");
		ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) applicationContext.getBean("threadPool");
		for (int i = 0; i < 100; i++) {
			executor.execute(new SimpleEvent(i + "事件"));
		}
	}
}

class SimpleEvent implements Runnable {
	private String name;

	public SimpleEvent(String name) {
		super();
		this.name = name;
	}

	@Override
	public void run() {
		System.out.println(name + "被执行");
	}
}
