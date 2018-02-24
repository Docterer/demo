package com.jojo.util.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

/**
 * 增删改查生成
 * 
 * @author chenyue
 */
public class JavaCodeCRUDGenerator {

	private Charset charsetUTF8 = Charset.forName("UTF-8");

	private String targetPackageService = "com.jiatu.wms.service";

	private String targetPackageController = "com.jiatu.wms.web.controller";

	private String targetProjectDir = "D:\\workspace\\JiaTu\\wms";

	private Class<?> targetEntityClass;

	public void generate() {
		String classSimpleName = targetEntityClass.getSimpleName();
		String className = targetEntityClass.getName();
		List<String> fieldNames = getFiledName(targetEntityClass);
		Map<String, Object> map = Maps.newHashMap();
		map.put("classSimpleName", classSimpleName);
		map.put("targetPackageController", targetPackageController);
		map.put("targetPackageService", targetPackageService);
		map.put("className", className);
		map.put("fieldNames", fieldNames);
		map.put("ctx", " ${ctx}");
		map.put("classSimpleNameUncapitalize", StringUtils.uncapitalize(classSimpleName));
		System.out.println(JSON.toJSONString(map));

		// Create your Configuration instance, and specify if up to what
		// FreeMarker
		// version (here 2.3.25) do you want to apply the fixes that are not
		// 100%
		// backward-compatible. See the Configuration JavaDoc for details.
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);

		// Specify the source where the template files come from. Here I set a
		// plain directory for it, but non-file-system sources are possible too:
		String basePath = this.getClass().getClassLoader().getResource("").getPath();
		try {
			cfg.setDirectoryForTemplateLoading(new File(basePath + "/generator"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Set the preferred charset template files are stored in. UTF-8 is
		// a good choice in most applications:
		cfg.setDefaultEncoding("UTF-8");

		// Sets how errors will appear.
		// During web page *development*
		// TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		// Don't log exceptions inside FreeMarker that it will thrown at you
		// anyway:
		cfg.setLogTemplateExceptions(false);
		try {
			// 生成service
			Template temp = cfg.getTemplate("service.ftlh");
			File serviceFile = new File(targetProjectDir + "\\src\\main\\java\\"
					+ targetPackageService.replace(".", "\\") + "\\" + classSimpleName + "Service.java");
			if (!serviceFile.exists()) {
				serviceFile.createNewFile();
			}
			BufferedWriter servicelWrite = Files.newWriter(serviceFile, charsetUTF8);
			temp.process(map, servicelWrite);
			System.out.println(serviceFile);

			temp = cfg.getTemplate("serviceImpl.ftlh");
			File serviceImplFile = new File(targetProjectDir + "\\src\\main\\java\\"
					+ targetPackageService.replace(".", "\\") + "\\" + classSimpleName + "ServiceImpl.java");
			if (!serviceImplFile.exists()) {
				serviceImplFile.createNewFile();
			}
			BufferedWriter serviceImplWrite = Files.newWriter(serviceImplFile, charsetUTF8);
			temp.process(map, serviceImplWrite);
			System.out.println(serviceImplFile);
			// 生成controllerApi
			temp = cfg.getTemplate("controllerApi.ftlh");
			File apiFile = new File(targetProjectDir + "\\src\\main\\java\\"
					+ targetPackageController.replace(".", "\\") + "\\api\\" + classSimpleName + "ApiController.java");
			if (!apiFile.exists()) {
				apiFile.createNewFile();
			}
			BufferedWriter apiWrite = Files.newWriter(apiFile, charsetUTF8);
			temp.process(map, apiWrite);
			System.out.println(apiFile);
			// 生成JSP
			temp = cfg.getTemplate("jsp.ftlh");
			File jspFile = new File(targetProjectDir + "\\src\\main\\webapp\\WEB-INF\\views\\system\\"
					+ StringUtils.uncapitalize(classSimpleName) + ".jsp");
			if (!jspFile.exists()) {
				jspFile.createNewFile();
			}
			BufferedWriter jspWrite = Files.newWriter(jspFile, charsetUTF8);
			temp.process(map, jspWrite);
			System.out.println(jspFile);
			System.out.println("done");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<String> getFiledName(Class<?> clazz) {
		List<String> fieldNames = Lists.newArrayList();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers())) {
				fieldNames.add(field.getName());
			}
		}
		return fieldNames;
	}

	public static void main(String[] args) {
		JavaCodeCRUDGenerator generator = new JavaCodeCRUDGenerator();
		generator.generate();
	}

}
