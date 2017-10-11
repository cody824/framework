package com.noknown.framework.common.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ErrorMsg implements Serializable {
	private static final long serialVersionUID = 8853943991332469005L;
	
	/**
	 * 错误编码
	 * 	1 ： 参数错误
	 * 	65535 : 未知错误
	 * 
	 */
	private int errorNum = 1;
	
	/**
	 * 错误信息的参数列表
	 */
	private List<String> errorArgs;
	
	/**
	 * 错误信息
	 */
	private String errorMsg;
	
	/**
	 * 系统内部错误信息
	 */
	private String systemError;

	/**
	 * 错误异常对象
	 */
	private Object detail;
	
	public ErrorMsg(){
	}
	
	/**
	 * 构造错误信息
	 * @param errorMsg 错误信息
 	 */
	public ErrorMsg(String errorMsg, boolean isSystem){
		if (isSystem) {
			this.systemError = errorMsg;
		} else {
			this.errorMsg = errorMsg;
		}
	}
	
	/**
	 * 构造错误信息 
     * @param errorNum 错误码
     * @param errorMsg 错误信息
	 */
	public ErrorMsg(int errorNum, String errorMsg) {
		this.errorNum = errorNum;
		this.errorMsg = errorMsg;
	}
	
	/**
	 * 构造错误信息
	 * @param errorNum	错误编码
	 * @param errorMsg	错误信息
	 * @param errorArgs	错误参数表
	 */
	public ErrorMsg(int errorNum, String errorMsg, List<String> errorArgs){
		this.errorMsg = errorMsg;
		this.errorNum = errorNum;
		this.errorArgs = errorArgs;
	}

	/**
	 * @return 错误编码
	 */
	public int getErrorNum() {
		return errorNum;
	}

	/**
	 * 设置错误编码
	 * @param errorNum 错误编码
	 */
	public void setErrorNum(int errorNum) {
		this.errorNum = errorNum;
	}

	/**
	 * 获取错误参数
	 * @return 错误参数
	 */
	public List<String> getErrorArgs() {
		if (errorArgs == null)
			errorArgs = new ArrayList<String>();
		return errorArgs;
	}

	/**
	 * 设置错误参数
	 * @param errorArgs 错误参数
	 */
	public void setErrorArgs(List<String> errorArgs) {
		this.errorArgs = errorArgs;
	}

	/**
	 * 获取错误信息
	 * @return	错误信息
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * 设置错误消息
	 * @param errorMsg 错误消息
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String toString() {
		return "ErrorMsg [errorNum=" + errorNum + ", errorArgs=" + errorArgs
				+ ", errorMsg=" + errorMsg + "]";
	}

	/**
	 * @return the detail
	 */
	public Object getDetail() {
		return detail;
	}

	/**
	 * @param detail the detail to set
	 */
	public void setDetail(Object detail) {
		this.detail = detail;
	}

	/**
	 * @return the systemError
	 */
	public String getSystemError() {
		return systemError;
	}

	/**
	 * @param systemError the systemError to set
	 */
	public void setSystemError(String systemError) {
		this.systemError = systemError;
	}
}
