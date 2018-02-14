package com.noknown.framework.common.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author guodong
 */
@Controller
public class ErrorController /*extends BasicErrorController*/ {


	@RequestMapping(produces = "text/html", value= "/error/{errorNum}")
	public ModelAndView errorHtml(@PathVariable Integer errorNum) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setStatus(HttpStatus.valueOf(errorNum));
		modelAndView.setViewName("error/" + errorNum);
		return  modelAndView;
	}

	@RequestMapping(value= "/error/{errorNum}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> error(@PathVariable Integer errorNum) {
		Map<String, Object> body = new HashMap<>(1);
		body.put("httpStatus", HttpStatus.valueOf(errorNum));
		return new ResponseEntity<>(body, HttpStatus.valueOf(errorNum));
	}
}
