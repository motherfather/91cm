package com.nineone.nocm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class RouterController implements ErrorController {


    @RequestMapping(value = "/{path:[^\\.]*}")
    public String redirect(){
        return "forward:/";
    }

    @RequestMapping("/error")
    public String ridiectRoot() {
    	return "forward:/";
    }

	@Override
	public String getErrorPath() {
		return "/error";
	}
}