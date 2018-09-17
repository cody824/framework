package com.noknown.framework.common.pojo;

import java.util.Date;

public class DownloadInfo {

	private int taskNum;

	private long size;

	private String url;

	private String filePath;

	private Date buildDate;

	public int getTaskNum() {
		return taskNum;
	}

	public DownloadInfo setTaskNum(int taskNum) {
		this.taskNum = taskNum;
		return this;
	}

	public long getSize() {
		return size;
	}

	public DownloadInfo setSize(long size) {
		this.size = size;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public DownloadInfo setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getFilePath() {
		return filePath;
	}

	public DownloadInfo setFilePath(String filePath) {
		this.filePath = filePath;
		return this;
	}

	public Date getBuildDate() {
		return buildDate;
	}

	public DownloadInfo setBuildDate(Date buildDate) {
		this.buildDate = buildDate;
		return this;
	}
}
