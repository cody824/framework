package com.noknown.framework.sms.pojo;

import java.util.List;
import java.util.Map;

public class SMS {
	
    public static final String  TYPE_TXT="txt";
    public static final String  TYPE_TEMPLATE="template";
	
	
	/**
	 * 服务提供商 玄武：xuanwu
	 */
	private String provider;
	
	/**
	 * 发送的手机号
	 */
	private List<String> phones;
		
	/**
	 * 消息类型  txt：自定义文本   template：模板
	 */
	private String type;
	
	/**
	 * 文本模式下，发送的内容
	 */
    private String content;
	
    /**
     * 模板模式下，短信使用的模板编号
     */
    private String tempCode;
    
    /**
     * 模板模式下，模板变量参数，键值对
     */
    private Map<String,String> vars;
    
    /**
     * 文本模式构造器
     * @param provider
     * @param phones
     * @param type
     * @param content
     */
	public SMS(String provider, List<String> phones, String content) {
		super();
		this.provider = provider;
		this.phones = phones;
		this.type = SMS.TYPE_TXT;
		this.content = content;
	}
	
	
	/**
	 * 模板模式构造器
	 * @param provider
	 * @param phones
	 * @param type
	 * @param tempCode
	 * @param vars
	 */
	public SMS(String provider, List<String> phones, 
			String tempCode, Map<String, String> vars) {
		super();
		this.provider = provider;
		this.phones = phones;
		this.type = SMS.TYPE_TEMPLATE;
		this.tempCode = tempCode;
		this.vars = vars;
	}




	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public List<String> getPhones() {
		return phones;
	}

	public void setPhones(List<String> phones) {
		this.phones = phones;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTempCode() {
		return tempCode;
	}

	public void setTempCode(String tempCode) {
		this.tempCode = tempCode;
	}

	public Map<String, String> getVars() {
		return vars;
	}

	public void setVars(Map<String, String> vars) {
		this.vars = vars;
	}

}
