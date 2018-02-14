package com.noknown.framework.email.processor;

import com.noknown.framework.email.model.MailMessage;

import java.util.Map;

/**
 * @author 未知
 */
public interface MailProcessor {
	
	/**
	 * 发送邮件
	 * @param from	发件人地址
	 * @param to	收件人地址
	 * @param subject 主题
	 * @param content 正文
	 * @param async 异步发送
	 * @return 是否成功
	 */
	boolean sendMail(String from, String to, String subject, String content, boolean async);
	
	/**
	 * 通过模板发送邮件
	 * @param from	收件人地址
	 * @param to	发件人地址
	 * @param subject 主题
	 * @param tpl	模板名
	 * @param tplData	模板内容
	 * @param async 异步发送
	 * @return 是否成功
	 */
	boolean sendMail(String from, String to, String subject, String tpl, Map<String, String> tplData, boolean async);
	
	/**
	 * 发送邮件
	 * @param mailMessage   邮件信息
	 * @param async 异步发送
	 * @return 是否成功
	 */
	boolean sendMail(MailMessage mailMessage, boolean async);


}
