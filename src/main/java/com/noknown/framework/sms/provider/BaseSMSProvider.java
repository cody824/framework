package com.noknown.framework.sms.provider;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.noknown.framework.common.util.RegexValidateUtil;
import com.noknown.framework.common.util.StringUtil;
import com.noknown.framework.common.util.http.HttpRequest;
import com.noknown.framework.sms.pojo.SMS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public abstract class BaseSMSProvider implements SMSProvider {

    protected final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

    //发送成功
    public static final String SEND_SUCCESS = "0";

    //账户余额不足
    public static final String NOT_ENOUGH_MONEY = "1";

    //发送号码数理大于最大发送数量
    public static final String OUT_OF_MAX_SEND_NUM = "2";

    //传递接口参数不正确
    public static final String ERROR_PARA = "3";

    //用户名不存在
    public static final String USER_NOT_EXIST = "4";

    //用户名密码不正确
    public static final String ERROR_PASSWARD = "5";

    //提交过快（提交速度超过流速限制）
    public static final String ERROR_SUBMIT_TOO_FAST = "6";

    //系统忙（因平台侧原因，暂时无法处理提交的短信）
    public static final String ERROR_SYSTEM_BUSY = "7";

    //敏感词
    public static final String ERROR_LIMIT_WORDS = "8";

    //消息长度错
    public static final String ERROR_MESSAGE_TOO_LONG = "9";

    //未知错误
    public static final String UNKNOWN_ERROR = "99";

	private static final HashMap<String, String> ERROR_CODE_NAME_MAP = new HashMap<>();

    public static BlockingQueue<SMS> msgQueue = new LinkedBlockingDeque<>(1000);

    public String name;

    public int maxNum = 100;

    public String split = ",";

	@Value("${sms.support:false}")
	private boolean smsSupport;

    protected abstract void initProvider();

    protected abstract String getSMSUrl(SMS sms);

    protected abstract void checkResult(String code,String phones);

	public static String getErrorNameByCode(String errorCode) {
		String errorMsg = ERROR_CODE_NAME_MAP.get(errorCode);
		if (errorMsg == null) {
			return errorCode;
        }
		return ERROR_CODE_NAME_MAP.get(errorCode);
    }
    

    public void sendError(String errorCode,String phone) {
    	String message =String.format("短信发送失败--短信服务商【%s】--发送号码【%s】  --错误原因【%s】", name, phone, getErrorNameByCode(errorCode));
        logger.error(message);
    }

    public void sendSuccess() {
        logger.debug("短信发送成功");
    }

	@PostConstruct
	void runSMSSendThread() {
		if (!smsSupport) {
			return;
		}

		//获取当前系统的CPU 数目
		int cpuNums = Runtime.getRuntime().availableProcessors();

		ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
				.setNameFormat("sms-sender-%d").build();

		//Common Thread Pool
		ExecutorService pool = new ThreadPoolExecutor(cpuNums, 200,
				0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

		for (int i = 0; i < cpuNums; i++) {
			pool.execute(new SMSSendThread(this));
        }

		logger.debug("创建短信发送队列");

		if (ERROR_CODE_NAME_MAP.isEmpty()) {
			ERROR_CODE_NAME_MAP.put(BaseSMSProvider.NOT_ENOUGH_MONEY, "账户余额不足");
			ERROR_CODE_NAME_MAP.put(BaseSMSProvider.OUT_OF_MAX_SEND_NUM, "发送号码数理大于最大发送数量");
			ERROR_CODE_NAME_MAP.put(BaseSMSProvider.ERROR_PARA, "传递接口参数不正确");
			ERROR_CODE_NAME_MAP.put(BaseSMSProvider.USER_NOT_EXIST, "用户名不存在");
			ERROR_CODE_NAME_MAP.put(BaseSMSProvider.ERROR_PASSWARD, "用户名密码不正确");
			ERROR_CODE_NAME_MAP.put(BaseSMSProvider.ERROR_SUBMIT_TOO_FAST, "提交过快（提交速度超过流速限制）");
			ERROR_CODE_NAME_MAP.put(BaseSMSProvider.ERROR_SYSTEM_BUSY, "系统忙（因平台侧原因，暂时无法处理提交的短信）");
			ERROR_CODE_NAME_MAP.put(BaseSMSProvider.ERROR_LIMIT_WORDS, "包含敏感词");
			ERROR_CODE_NAME_MAP.put(BaseSMSProvider.UNKNOWN_ERROR, "未知错误");
		}
		initProvider();
    }

    public String transitionPhones(List<String> phones) {
        if (phones.size() == 1) {
            return phones.get(0);
        } else {
            String phoneStr = "";
            for (String phone : phones) {
                if (RegexValidateUtil.checkMobile(phone)) {
                    if (StringUtil.isBlank(phoneStr)) {
                        phoneStr = phone;
                    } else {
                        phoneStr += this.split + phone;
                    }
                }
            }
            return phoneStr;
        }
    }

	/**
	 * 将手机号码按照maxNum分拆成多组 （包含单组）
	 */
	public List<List<String>> transitionPhoneGroup(List<String> mobileNos) {
		List<List<String>> lis = new ArrayList<>();
		int totalNum = mobileNos.size();
		int totalGroupNum = (int) Math.ceil(Float.parseFloat(totalNum + "") / maxNum);

		for (int i = 0; i < totalGroupNum; i++) {
			int start, end;
			start = maxNum * i;
			end = maxNum * (i + 1);
			if (end > totalNum) {
				end = totalNum;
			}
			lis.add(mobileNos.subList(start, end));
		}
		return lis;
    }

	@Override
	public Boolean send(String mobileNo, String msg) throws Exception {

		List<String> phones = new ArrayList<String>();
		phones.add(mobileNo);
		return send(phones, msg);
	}

	@Override
	public Boolean send(List<String> mobileNos, String msg) throws Exception {

		List<List<String>> list = transitionPhoneGroup(mobileNos);

		for (List<String> list2 : list) {
			SMS sms = new SMS(this.name, list2, msg);
			msgQueue.offer(sms);
		}
		return true;

	}

	@Override
	public Boolean send(String mobileNo, String templateCode,
	                    Map<String, String> vars) throws Exception {
		List<String> phones = new ArrayList<String>();
		phones.add(mobileNo);
		return send(phones, templateCode, vars);
	}

	@Override
	public Boolean send(List<String> mobileNos, String templateCode,
	                    Map<String, String> vars) throws Exception {
		List<List<String>> list = transitionPhoneGroup(mobileNos);

		for (List<String> list2 : list) {
			SMS sms = new SMS(this.name, list2, templateCode, vars);
			msgQueue.offer(sms);
		}
		return true;
	}

	@Override
	public void doProcessSMSByTxt(SMS sms) {
		try {

			String toUrl = this.getSMSUrl(sms);

			HttpRequest request = HttpRequest.get(toUrl);
			BufferedReader in =request.bufferedReader();
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
			checkResult(buffer.toString(),sms.getPhones().toString());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(String.format("发送短消息失败，手机号码:%s", transitionPhones(sms.getPhones())));

		}
	}

	@Override
	public void doProcessSMSByTemplate(SMS sms) throws Exception {
		try {

			String toUrl = this.getSMSUrl(sms);

			HttpRequest request = HttpRequest.get(toUrl);
			BufferedReader in =request.bufferedReader();
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
			checkResult(buffer.toString(),sms.getPhones().toString());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(String.format("发送短消息失败，手机号码:%s", transitionPhones(sms.getPhones())));

		}
	}

    public class SMSSendThread implements Runnable {
    	
    	SMSProvider processpr;
    	
    	public SMSSendThread(SMSProvider processpr) {
    		super();
    		this.processpr = processpr;
		}
    	
        @Override
        public void run() {
            logger.info("启动发送短信线程！");
            try {
                do {
                    SMS sms = msgQueue.take();

	                if (sms.getType().equals(SMS.TYPE_TXT)) {
		                processpr.doProcessSMSByTxt(sms);
	                }

	                if (sms.getType().equals(SMS.TYPE_TEMPLATE)) {
		                processpr.doProcessSMSByTemplate(sms);
	                }
                } while (true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
