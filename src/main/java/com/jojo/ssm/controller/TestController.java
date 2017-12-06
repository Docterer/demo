package com.jojo.ssm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin")
public class TestController {

	@RequestMapping(value = "/visit", method = RequestMethod.GET)
	public String visit(Model model) {
		model.addAttribute("message", "Hello World！");
		return "index";
	}
}
