package com.noknown.framework.sms.test.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noknown.framework.common.base.BaseController;

@RestController
public class SmsTestController extends BaseController {

    protected final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

	@RequestMapping(value = "/sms/test/send", method = {RequestMethod.POST,RequestMethod.GET})
	public ResponseEntity<?> analysis(@RequestParam String to, @RequestParam String text)
			throws Exception {
		System.out.println("发送短信给出[" + to + "]:");
		System.out.println("短信内容：" + text);
		return ResponseEntity.ok("0");
	}
	
	
	
}
