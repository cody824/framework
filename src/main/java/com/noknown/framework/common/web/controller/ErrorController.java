package com.noknown.framework.common.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ErrorController /*extends BasicErrorController*/ {
	
	
	@RequestMapping(produces = "text/html", value= "/error/{errorNum}")
	public ModelAndView errorHtml(HttpServletRequest request,
			HttpServletResponse response, @PathVariable Integer errorNum) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setStatus(HttpStatus.valueOf(errorNum));
		modelAndView.setViewName("error/" + errorNum);
		return  modelAndView;
	}

	@RequestMapping(value= "/error/{errorNum}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request, @PathVariable Integer errorNum) {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("httpStatus", HttpStatus.valueOf(errorNum));
		return new ResponseEntity<Map<String, Object>>(body, HttpStatus.valueOf(errorNum));
	}
}
