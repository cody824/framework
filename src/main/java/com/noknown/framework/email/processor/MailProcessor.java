/**
 * @Title: MailService.java
 * @Package com.soulinfo.api.mail.service
 * @Description: 邮件服务层
 * CopyRright (c) 2014-2015 SOUL
 * Company:无锡众志和达数据计算股份有限公司
 * 
 * @author xingweiwei
 * @date 2015年5月28日 下午12:19:56
 * @version V1.0
 */
package com.noknown.framework.email.processor;

import com.noknown.framework.email.model.MailMessage;

import java.util.Map;


public interface MailProcessor {
	
	/**
	 * 发送邮件
	 * @param from	发件人地址
	 * @param to	收件人地址
	 * @param subject 主题
	 * @param content 正文
	 * @return true 成功
	 */
	boolean sendMail(String from, String to, String subject, String content, boolean async);
	
	/**
	 * 通过模板发送邮件
	 * @param from	收件人地址
	 * @param to	发件人地址
	 * @param subject 主题
	 * @param tpl	模板名
	 * @param tplData	模板内容
	 * @return true 成功
	 */
	boolean sendMail(String from, String to, String subject, String tpl, Map<String, String> tplData, boolean async);
	
	/**
	 * 发送邮件
	 * @param mailMessage
	 * @return
	 */
	boolean sendMail(MailMessage mailMessage, boolean async);


}
