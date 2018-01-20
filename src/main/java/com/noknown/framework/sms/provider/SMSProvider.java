
package com.noknown.framework.sms.provider;

import java.util.List;
import java.util.Map;

import com.noknown.framework.sms.pojo.SMS;

public interface SMSProvider {

    /**
     * 发送短消息
     *
     * @param mobileNo 手机号码
     * @param msg      短消息
     * @return
     * @throws Exception
     */
    Boolean send(String mobileNo, String msg) throws Exception;

    /**
     * 批量发送短消息
     *
     * @param mobileNos 手机号码列表
     * @param msg       短消息
     * @return
     * @throws Exception
     */
    Boolean send(List<String> mobileNos, String msg) throws Exception;

    /**
     * 通过模板发送短信
     *
     * @param mobileNo
     * @param templateCode 模板编号
     * @param vars         模板变量参数，键值对
     * @return
     * @throws Exception
     */
    Boolean send(String mobileNo, String templateCode, Map<String, String> vars) throws Exception;

    /**
     * 通过模板 批量发送短信
     *
     * @param mobileNos
     * @param templateCode 模板编号
     * @param vars         模板变量参数，键值对
     * @return
     * @throws Exception
     */
    Boolean send(List<String> mobileNos, String templateCode, Map<String, String> vars) throws Exception;

    /**
     * 文本模式：实际处理短信接口
     *
     * @param sms
     */
    void doProcessSMSByTxt(SMS sms) throws Exception;

    /**
     * 模板模式：实际处理短信接口
     *
     * @param sms
     */
    void doProcessSMSByTemplate(SMS sms) throws Exception;
}