package com.noknown.framework.security.web.controller;

import com.noknown.framework.cache.service.CacheService;
import com.noknown.framework.common.base.BaseController;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.exception.WebException;
import com.noknown.framework.common.util.RegexValidateUtil;
import com.noknown.framework.security.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

@Controller("messageAuthCodeController")
@RequestMapping(value = "/security")
public class VerificationCodeController extends BaseController {

	@Autowired
	private VerificationCodeService verificationCodeService;

	@Autowired
	private CacheService cacheService;

	@RequestMapping(value = {"/authcode/{type}", "/verification-code/{type}"}, method = RequestMethod.POST)
	public @ResponseBody
	Object generatePhotoAuthcode(
			@PathVariable String type,
			@RequestParam(value = "to", required = false) String to,
			@RequestParam(required = false, defaultValue = "4") int len,
			@RequestParam(required = false, defaultValue = "10") int timeout) throws Exception {


		if ("phone".equals(type)) {

			if (!RegexValidateUtil.checkMobile(to)) {
				throw new WebException("请输入正确的手机号码");
			}
		} else if ("email".equals(type)) {
			if (!RegexValidateUtil.checkEmail(to)) {
				throw new WebException("请输入正确的邮箱");
			}
		} else {
			throw new WebException("不支持发送改类型的验证码");
		}
		Date now = new Date();
		Date date = (Date) cacheService.get("send_autocode_for_" + to);
		if (date == null) {
			cacheService.set("send_autocode_for_" + to, now, new Date(now.getTime() + 60 * 1000));
		} else {
			throw new WebException("对不起，验证码发送间隔为1分钟，请稍后再试");
		}
		verificationCodeService.send(type, to, len, timeout);
		return ResponseEntity.ok().body(new HashMap<>());
	
	}
	

	/**
	 * 验证码验证
	 * @param request servlet请求
	 * @param authcode 验证码
	 * @param clientId 客户id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = {"/authcode", "/verification-code"}, method = RequestMethod.POST)
	public @ResponseBody
	Object validateAuthcode(HttpServletRequest request,
			@RequestParam("authcode") String authcode,
			@RequestParam(required = false) String clientId) throws Exception {
		
		clientId = clientId == null ? request.getSession().getId() : clientId;
		boolean isOk = verificationCodeService.check(clientId, authcode);
		if (!isOk){
			throw new WebException("验证码错误");
		}
		return ResponseEntity.ok();
	}

	/**
	 * 获取验证码图片
	 * @param request 请求
	 * @param response 应答
	 * @param clientId 客户端id
	 * @throws Exception 
	 */
	@RequestMapping(value = {"/authcode/img", "/verification-code/img"}, method = RequestMethod.GET)
	public void generateImgAuthcode(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false, defaultValue = "4") int len,
			@RequestParam(required = false, defaultValue = "10") int timeout,
			@RequestParam(required = false) String clientId) throws Exception {
		clientId = clientId == null ? request.getSession().getId() : clientId;
		
		OutputStream os = null;// 写出数据
		OutputStream out = null;// 缓冲
		try {
			os = response.getOutputStream();
			out = new BufferedOutputStream(os);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// 设置页面不缓存
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Expires", "0");
		int width = 60, height = 20;
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		// 获取图形上下文
		Graphics g = image.getGraphics();
		// 生成随机类
		Random random = new Random();
		// 设定背景色
		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height);
		// 设定字体
		g.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		// 随机产生155条干扰线，使图像中的认证码不易被其它程序探测到
		g.setColor(getRandColor(160, 200));
		for (int i = 0; i < 155; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(width);
			int x1 = random.nextInt(12);
			int y1 = random.nextInt(12);
			g.drawLine(x, y, x + x1, y + y1);
		}
		// 取随机产生的认证码(4位数字)
		String aRand = "";
		try {
			aRand = verificationCodeService.generate(clientId, len, timeout);
		} catch (ServiceException e1) {
			e1.printStackTrace();
			for (int i = 0; i < len; i++) {
				String rand = String.valueOf(random.nextInt(10));
				aRand += rand;
			}
		}
		for (int i = 0; i < aRand.length(); i++) {
			String rand = aRand.substring(i, i + 1);
			// 将认证码显示到图像中
			g.setColor(new Color(20 + random.nextInt(110), 20 + random
					.nextInt(110), 20 + random.nextInt(110)));
			// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
			g.drawString(rand, 13 * i + 6, 16);
		}

		// 图像生效
		g.dispose();
		// 输出图像到叶面
		try {
			ImageIO.write(image, "JPEG", os);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private  Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255) {
			fc = 255;
		}
		if (fc > 255) {
			bc = 255;
		}
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}
}
