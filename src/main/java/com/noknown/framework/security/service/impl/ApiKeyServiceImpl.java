package com.noknown.framework.security.service.impl;

import com.noknown.framework.common.base.BaseServiceImpl;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.util.BaseUtil;
import com.noknown.framework.common.util.StringUtil;
import com.noknown.framework.common.util.algo.MD5Util;
import com.noknown.framework.security.Constants;
import com.noknown.framework.security.authentication.SureApiAuthToken;
import com.noknown.framework.security.dao.ApiKeyDao;
import com.noknown.framework.security.dao.UserDao;
import com.noknown.framework.security.model.ApiKey;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.service.ApiKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * @author guodong
 * @date 2018/4/2
 */
@Service
public class ApiKeyServiceImpl extends BaseServiceImpl<ApiKey, String> implements ApiKeyService {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final ApiKeyDao apiKeyDao;

	private final UserDao userDao;

	@Autowired
	public ApiKeyServiceImpl(ApiKeyDao apiKeyDao, UserDao userDao) {
		this.apiKeyDao = apiKeyDao;
		this.userDao = userDao;
	}

	private String signTopRequest(Map<String, String> params, String secret, String signMethod) throws ServiceException, IOException, InvalidKeyException, NoSuchAlgorithmException {
		// 第一步：检查参数是否已经排序
		String[] keys = params.keySet().toArray(new String[0]);
		Arrays.sort(keys);

		// 第二步：把所有参数名和参数值串在一起
		StringBuilder query = new StringBuilder();
		if (Constants.SIGN_METHOD_MD5.equalsIgnoreCase(signMethod)) {
			query.append(secret);
		} else if (Constants.SIGN_METHOD_HMAC.equalsIgnoreCase(signMethod)) {
			//DO nothing
		} else {
			throw new ServiceException("不支持的算法" + signMethod, -4);
		}
		for (String key : keys) {
			String value = params.get(key);
			if (StringUtil.isNotBlank(key) && StringUtil.isNotBlank(value)) {
				query.append(key).append(value);
			}
		}

		// 第三步：使用MD5/HMAC加密
		byte[] bytes;
		if (Constants.SIGN_METHOD_HMAC.equalsIgnoreCase(signMethod)) {
			bytes = encryptHMAC(query.toString(), secret);
			return byte2hex(bytes);
		} else {
			query.append(secret);
			return MD5Util.getSignature(query.toString().getBytes(Constants.CHARSET_UTF8)).toUpperCase();
		}
	}

	private byte[] encryptHMAC(String data, String secret) throws IOException {
		byte[] bytes;
		try {
			SecretKey secretKey = new SecretKeySpec(secret.getBytes(Constants.CHARSET_UTF8), "HmacMD5");
			Mac mac = Mac.getInstance(secretKey.getAlgorithm());
			mac.init(secretKey);
			bytes = mac.doFinal(data.getBytes(Constants.CHARSET_UTF8));
		} catch (GeneralSecurityException gse) {
			throw new IOException(gse.toString());
		}
		return bytes;
	}

	private String byte2hex(byte[] bytes) {
		StringBuilder sign = new StringBuilder();
		for (byte aByte : bytes) {
			String hex = Integer.toHexString(aByte & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex.toUpperCase());
		}
		return sign.toString();
	}

	@Override
	public JpaRepository<ApiKey, String> getRepository() {
		return apiKeyDao;
	}

	@Override
	public JpaSpecificationExecutor<ApiKey> getSpecificationExecutor() {
		return apiKeyDao;
	}

	@Override
	public ApiKey create(Integer userId) {

		String accessKey = BaseUtil.getIdentifyCode();

		ApiKey apiKey = null;
		try {
			apiKey = new ApiKey();
			apiKey.setAccessKey(accessKey);
			apiKey.setSecurityKey(BaseUtil.getUUID());
			apiKey.setUserId(userId);
			apiKey.setEnable(true);
			apiKey.setCreateTime(new Date());
			apiKeyDao.save(apiKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiKey;
	}

	@Override
	public void enable(String accessKey) throws ServiceException {
		ApiKey apiKey = apiKeyDao.findById(accessKey).orElse(null);
		if (apiKey == null) {
			throw new ServiceException("key不存在", -3);
		}
		apiKey.setEnable(true);
		apiKeyDao.save(apiKey);
	}

	@Override
	public void disable(String accessKey) throws ServiceException {
		ApiKey apiKey = apiKeyDao.findById(accessKey).orElse(null);
		if (apiKey == null) {
			throw new ServiceException("key不存在", -3);
		}
		apiKey.setEnable(false);
		apiKeyDao.save(apiKey);

	}

	@Override
	public User check(SureApiAuthToken token) throws ServiceException {
		ApiKey apiKey = apiKeyDao.findById(token.getAccessKey()).orElse(null);
		if (apiKey == null) {
			throw new ServiceException("key不存在", -3);
		}

		String cipher;
		try {
			cipher = signTopRequest(token.getParams(), apiKey.getSecurityKey(), token.getSignMethod());
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error("加密失败：" + e.getMessage(), e);
			throw new ServiceException("加密失败，请联系管理员", -99);
		}
		User user;
		if (token.getSign().equalsIgnoreCase(cipher)) {
			user = userDao.findById(apiKey.getUserId()).orElse(null);
		} else {
			logger.debug("正确签名：" + cipher);
			throw new ServiceException("签名不匹配", -5);
		}
		return user;
	}

	@Override
	public ApiKey findByUserId(Integer userId) {
		return apiKeyDao.findByUserId(userId);
	}
}
