package com.noknown.framework.common.util;

import com.noknown.framework.common.exception.UtilException;
import com.noknown.framework.common.pojo.DownloadInfo;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * @author guodong
 * @date 2020/10/13
 */
public class LogUtil {


	private final static Logger logger = LoggerFactory.getLogger(LogUtil.class);

	public static DownloadInfo exportLogs(String logPath) throws UtilException {
		File dir = new File(logPath);
		if (!dir.exists()) {
			return null;
		}
		String timeStr = DateUtil.getCurrentTime(DateUtil.TIME_NOFUll_FORMAT);
		File zipFile = new File(System.getProperty("java.io.tmpdir"), "log-" + timeStr + ".zip");
		try {
			FileUtil.zip(zipFile.getAbsolutePath(), dir);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new UtilException("生成压缩包失败");
		}
		DownloadInfo downloadInfo = new DownloadInfo();
		downloadInfo.setBuildDate(new Date())
				.setFilePath(zipFile.getAbsolutePath())
				.setSize(zipFile.length())
				.setTaskNum(dir.listFiles().length);
		return downloadInfo;
	}

	public static String getLogContent(String logPath, String name) throws IOException {
		File dir = new File(logPath);
		if (!dir.exists()) {
			return null;
		}
		File file = new File(logPath, name);
		if (file.exists()) {
			return FileUtils.readFileToString(file);
		}
		return null;
	}
}
