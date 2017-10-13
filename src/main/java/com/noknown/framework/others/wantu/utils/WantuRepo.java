package com.noknown.framework.others.wantu.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.media.client.MediaClient;
import com.alibaba.media.upload.UploadPolicy;
import com.alibaba.media.upload.UploadTokenClient;

public class WantuRepo {

	private Map<String, UploadPolicy> upMap = new HashMap<>();
	
	private Map<String, UploadTokenClient> utClientMap = new HashMap<>();
	
	private Map<String, MediaClient> mediaClientMap = new HashMap<>();
	
	public UploadPolicy getUP(String app, String type) {
		return upMap.get(app + "_" + type);
	}
	
	public UploadTokenClient getUTClient(String app, String type) {
		return utClientMap.get(app + "_" + type);
	}
	
	public MediaClient getMediaClient(String app, String type) {
		return mediaClientMap.get(app + "_" + type);
	}

	/**
	 * @return the upMap
	 */
	public Map<String, UploadPolicy> getUpMap() {
		return upMap;
	}

	/**
	 * @param upMap the upMap to set
	 */
	public void setUpMap(Map<String, UploadPolicy> upMap) {
		this.upMap = upMap;
	}

	/**
	 * @return the utClientMap
	 */
	public Map<String, UploadTokenClient> getUtClientMap() {
		return utClientMap;
	}

	/**
	 * @param utClientMap the utClientMap to set
	 */
	public void setUtClientMap(Map<String, UploadTokenClient> utClientMap) {
		this.utClientMap = utClientMap;
	}

	/**
	 * @return the mediaClientMap
	 */
	public Map<String, MediaClient> getMediaClientMap() {
		return mediaClientMap;
	}

	/**
	 * @param mediaClientMap the mediaClientMap to set
	 */
	public void setMediaClientMap(Map<String, MediaClient> mediaClientMap) {
		this.mediaClientMap = mediaClientMap;
	}
	

	
}
