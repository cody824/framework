package com.noknown.framework.email.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class MailMessage implements Serializable {

	private static final long serialVersionUID = 1316546546413132L;

	/**
	 * 发件人
	 */
	private String from;
	
	/**
	 * 收件人
	 */
	private String to;
	
	/**
	 * 正文
	 */
	private String content;
	
	/**
	 * 主题
	 */
	private String subject;
	
	/**
	 * 邮件模板
	 */
	private String tpl;
	
	/**
	 * 模板数据
	 */
	private Map<String, String> tplData;
	
	/**
	 * 发送时间
	 */
	private Date ctime;
	
	/**
	 * 优先级
	 */
	private Integer priority;
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getTpl() {
		return tpl;
	}
	public void setTpl(String tpl) {
		this.tpl = tpl;
	}
	public Map<String, String> getTplData() {
		return tplData;
	}
	public void setTplData(Map<String, String> tplData) {
		this.tplData = tplData;
	}
	public Date getCtime() {
		return ctime;
	}
	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
}
