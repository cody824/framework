package com.noknown.framework.security.pojo;

public class AuthcodeParam {

	/**
	 * 邮件发送地址
	 */
	private String adminEmailAddres;
	
	/**
	 * 验证码邮件主题
	 */
	private String emailSubject;

	/**
	 * 邮件中网站地址
	 */
	private String baseUrl;
	
	/**
	 * 邮件模板
	 */
	private String emailTemplate;
	
	/**
	 * 短信模板
	 */
	private String smsTemplate;

	/**
	 * @return the adminEmailAddres
	 */
	public String getAdminEmailAddres() {
		return adminEmailAddres;
	}

	/**
	 * @param adminEmailAddres the adminEmailAddres to set
	 */
	public void setAdminEmailAddres(String adminEmailAddres) {
		this.adminEmailAddres = adminEmailAddres;
	}

	/**
	 * @return the baseUrl
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @param baseUrl the baseUrl to set
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * @return the emailTemplate
	 */
	public String getEmailTemplate() {
		return emailTemplate;
	}

	/**
	 * @param emailTemplate the emailTemplate to set
	 */
	public void setEmailTemplate(String emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	/**
	 * @return the smsTemplate
	 */
	public String getSmsTemplate() {
		return smsTemplate;
	}

	/**
	 * @param smsTemplate the smsTemplate to set
	 */
	public void setSmsTemplate(String smsTemplate) {
		this.smsTemplate = smsTemplate;
	}

	/**
	 * @return the emailSubject
	 */
	public String getEmailSubject() {
		return emailSubject;
	}

	/**
	 * @param emailSubject the emailSubject to set
	 */
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}
	
}
