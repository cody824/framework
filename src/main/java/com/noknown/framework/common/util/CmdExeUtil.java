package com.noknown.framework.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * @author xingweiwei
 */
public class CmdExeUtil {

	private final static Logger logger = LoggerFactory.getLogger(CmdExeUtil.class);

	public static String execSh(String cmd) {
		try {
			String[] cmdA = {"/bin/sh", "-c", cmd};
			logger.debug("CMD:" + cmd);
			Process process = Runtime.getRuntime().exec(cmdA);
			LineNumberReader br = new LineNumberReader(new InputStreamReader(process.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			logger.debug("Output:" + sb.toString());
			return sb.toString();
		} catch (Exception e) {
			logger.error("执行脚本{}错误：{}", cmd, e.getLocalizedMessage());
		}
		return null;
	}

	public static String exec(String cmd) {
		try {
			String[] cmdA = {cmd};
			logger.debug("CMD:" + cmd);
			Process process = Runtime.getRuntime().exec(cmdA);
			LineNumberReader br = new LineNumberReader(new InputStreamReader(process.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			logger.debug("Output:" + sb.toString());
			return sb.toString();
		} catch (Exception e) {
			logger.error("执行命令{}错误：{}", cmd, e.getLocalizedMessage());
		}
		return null;
	}
}
