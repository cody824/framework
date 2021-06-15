package com.noknown.framework.email.processor.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.noknown.framework.common.util.StringUtil;
import com.noknown.framework.email.model.MailMessage;
import com.noknown.framework.email.processor.MailProcessor;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author 未知
 */
@Component
public class MailProcessorImpl implements MailProcessor {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final JavaMailSender sender;

	private final FreeMarkerConfigurer freeMarkerConfigurer;

	@Value("${mail.support:false}")
	private boolean mailSupport;

	@Value("${mail.fromUser:noknown@163.com}")
	private String fromUser;

	@Value("${mail.sendTreadNum:1}")
	private int sendTreadNum;

	@Value("${mail.waitSecond:0}")
	private int waitSecond;

	private BlockingQueue<MimeMessage> msgQueue = new LinkedBlockingDeque<>(1000);

	@Autowired
	public MailProcessorImpl(JavaMailSender sender, FreeMarkerConfigurer freeMarkerConfigurer) {
		this.sender = sender;
		this.freeMarkerConfigurer = freeMarkerConfigurer;
	}

	@PostConstruct
	void runEmailThread() {

		if (!mailSupport) {
			return;
		}

		ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
				.setNameFormat("email-sender-%d").build();

		//Common Thread Pool
		ExecutorService pool = new ThreadPoolExecutor(sendTreadNum, 200,
				0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

		for (int i = 0; i < sendTreadNum; i++) {
			pool.execute(new EmailSendThread());
		}

	}

	@Override
	public boolean sendMail(String from, String to, String subject, String content, boolean async) {
		MimeMessage msg = sender.createMimeMessage();
		try {
			msg.addRecipients(MimeMessage.RecipientType.CC, InternetAddress.parse(fromUser));
			MimeMessageHelper helper = new MimeMessageHelper(msg, false, "UTF-8");
			helper.setSubject(subject);

			if (from != null) {
				helper.setFrom(from);
			} else {
				helper.setFrom(fromUser);
			}
			helper.setTo(to);
			helper.setText(content, true);
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
		if (async) {
			try {
				msgQueue.put(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			sender.send(msg);
		}
		return true;
	}

	@Override
	public boolean sendMail(String from, String to, String subject, String tpl, Object tplData,
	                        boolean async) {
		String content = this.getMailContentFromTpl(tpl, tplData);
		return this.sendMail(from, to, subject, content, async);
	}

	@Override
	public boolean sendMail(MailMessage mailMessage, boolean async) {
		String from = mailMessage.getFrom();
		String to = mailMessage.getTo();
		String content = mailMessage.getContent();
		String subject = mailMessage.getSubject();
		String tpl = mailMessage.getTpl();
		Map<String, String> tplData = mailMessage.getTplData();

		if (StringUtil.isBlank(content)) {
			return this.sendMail(from, to, subject, tpl, tplData, async);
		} else {
			return this.sendMail(from, to, subject, content, async);
		}
	}

	private Template getMailTpl(String tpl) {
		Template mailTpl;
		try {
			mailTpl = freeMarkerConfigurer.getConfiguration().getTemplate(tpl);
		} catch (IOException e) {
			logger.error(String.format("无法读取模板文件，tpl:%s", tpl));
			return null;
		}
		return mailTpl;
	}

	private String getMailContentFromTpl(String tpl, Object tplData) {
		Template maiTemplate = this.getMailTpl(tpl);
		String html = null;
		if (maiTemplate == null) {
			return null;
		}
		try {
			html = FreeMarkerTemplateUtils.processTemplateIntoString(maiTemplate, tplData);
		} catch (IOException e) {
			logger.error(String.format("无法读取模板文件，tpl:%s", tpl));
		} catch (TemplateException e) {
			logger.error(String.format("解析模板文件出错，tpl:%s", tpl));
		}
		return html;
	}

	public class EmailSendThread implements Runnable {

		@Override
		public void run() {
			logger.info("启动发送邮件线程！");
			while (true) {
				try {
					MimeMessage message = msgQueue.take();
					sender.send(message);
					if (waitSecond > 0) {
						Thread.sleep(1000 * waitSecond);
					}
				} catch (InterruptedException e) {
					break;
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getLocalizedMessage());
				}
			}
		}
	}
}
