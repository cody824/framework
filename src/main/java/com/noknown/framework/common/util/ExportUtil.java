package com.noknown.framework.common.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.noknown.framework.common.pojo.DownloadInfo;
import com.noknown.framework.common.util.excel.ExcelHandle;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author guodong
 * @date 2018/2/13
 */
public class ExportUtil {


	private static ExecutorService eService = new ThreadPoolExecutor(1, 1, 0L,
			TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<>(1024),
			new ThreadFactoryBuilder().setNameFormat("ExportCleanThread-%d").build()
	);

	public static DownloadInfo exportList(Collection<?> objList, String exportPath, String title, String excelTpl, MessageSource messageSource, List<String> messageAttr) throws Exception {
		String taskKey = BaseUtil.getTimeCode(new Date());
		File dir = new File(exportPath, taskKey);
		if (dir.exists()) {
			FileUtil.delFile(dir.getAbsolutePath());
		}
		if (!dir.mkdirs()) {
			throw new Exception("系统错误，生成目录失败");
		}
		return exportList(objList, exportPath, taskKey, title, excelTpl, messageSource, messageAttr);
	}

	public static DownloadInfo exportList(Collection<?> objList, String exportPath, String taskKey, String title, String excelTpl, MessageSource messageSource, List<String> messageAttr) throws Exception {
		File dir = new File(exportPath, taskKey);
		File file;
		if (!dir.exists() && !dir.mkdirs()) {
			throw new Exception("系统错误，生成目录失败");
		}
		if (excelTpl == null) {
			throw new Exception("模板不存在");
		}

		List<String> dataAttr = new ArrayList<>();

		Object obj = objList.iterator().next();
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (Date.class.equals(field.getType())) {
				dataAttr.add(field.getName());
			}
		}

		ExcelHandle handle = new ExcelHandle();
		String str = JsonUtil.toJson(objList);
		if (str == null) {
			throw new Exception("生成信息失败");
		}
		JSONParser parser = new JSONParser();
		file = new File(dir, title + ".xlsx");
		try (OutputStream os = new FileOutputStream(file)) {
			Object objs = parser.parse(new StringReader(str));
			JSONArray json = (JSONArray) objs;

			Locale locale = LocaleContextHolder.getLocale();

			for (Object object : json) {
				JSONObject jsonObject = (JSONObject) object;

				for (String attr : dataAttr) {
					if (jsonObject.get(attr) != null) {
						DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss");
						LocalDateTime applyTime = LocalDateTime.ofEpochSecond(Long.parseLong(jsonObject.get(attr).toString()) / 1000, 0, ZoneOffset.ofHours(8));
						jsonObject.put(attr, df.format(applyTime));
					}
				}
				if (messageAttr != null) {
					for (String key : messageAttr) {
						if (jsonObject.get(key) != null) {
							jsonObject.put(key, messageSource.getMessage(jsonObject.get(key).toString(), null, locale));
						}
					}
				}
			}

			JSONObject dataMap = new JSONObject();
			dataMap.put("objs", json);
			handle.writeData(excelTpl, dataMap);
			Workbook wb = handle.getTempWorkbook(excelTpl);
			handle.readClose(excelTpl);
			wb.write(os);
			os.flush();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

		freeSpaceIfNeeded(exportPath);
		DownloadInfo downloadInfo = new DownloadInfo();
		downloadInfo.setBuildDate(new Date())
				.setFilePath(file.getAbsolutePath())
				.setSize(file.length())
				.setTaskNum(objList.size());
		return downloadInfo;
	}

	private static synchronized void freeSpaceIfNeeded(String filePath) {
		Runnable runnable = new CacheClean(filePath);
		eService.execute(runnable);
	}

	static class CacheClean implements Runnable {

		private String filePath;

		CacheClean(String filePath) {
			this.filePath = filePath;
		}

		@Override
		public void run() {
			File file = new File(filePath);
			if (file.exists() && file.isDirectory()) {
				File[] fs = file.listFiles((dir, name) -> name.endsWith(".zip"));
				if (fs == null) {
					return;
				}
				long now = System.currentTimeMillis();
				long keepSec = 1000 * 60 * 60;
				for (File f : fs) {
					if (now - f.lastModified() > keepSec) {
						FileUtils.deleteQuietly(f);
					}
				}
			}
		}
	}

}
