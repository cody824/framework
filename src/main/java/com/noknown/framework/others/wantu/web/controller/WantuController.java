package com.noknown.framework.others.wantu.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.media.MediaDir;
import com.alibaba.media.MediaException;
import com.alibaba.media.MediaFile;
import com.alibaba.media.Result;
import com.alibaba.media.client.MediaClient;
import com.alibaba.media.common.PagedList;
import com.alibaba.media.upload.UploadPolicy;
import com.alibaba.media.upload.UploadTokenClient;
import com.noknown.framework.common.base.BaseController;
import com.noknown.framework.common.exception.WebException;
import com.noknown.framework.common.util.StringUtil;
import com.noknown.framework.others.wantu.utils.WantuRepo;

import libs.fastjson.com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping(value="/wantuapi")
public class WantuController extends BaseController {
	
	static final Logger logger = LoggerFactory.getLogger(WantuController.class); 
	
	@Autowired
	@Qualifier("publicUploadTokenClient")
	UploadTokenClient tokenClient;

    @Autowired
    WantuRepo upRepo;
	
	@RequestMapping(value = "/token", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody
	Object getDefaultToken(
			HttpSession session,
			HttpServletRequest request) throws Exception{
		JSONObject jsonObject = new JSONObject();
	    try {
	        UploadPolicy uploadPolicy = new UploadPolicy();
	        uploadPolicy.setInsertOnly(UploadPolicy.INSERT_ONLY_NONE); //INSERT_ONLY_NONE=0表示可覆盖上传，INSERT_ONLY=1表示不可覆盖
	        uploadPolicy.setExpiration(System.currentTimeMillis() + 3600 * 1000); //token过期时间，单位毫秒。-1表示永不过期。
	 
	        String token = tokenClient.getUploadToken(uploadPolicy);
	 
	        jsonObject.put( "state", "success" );
	        jsonObject.put( "token", token );
	        return outActionReturn(jsonObject, HttpStatus.OK);
	 
	    } catch (MediaException e) {
	    	throw new WebException(e);
	    }
	}
	
	@RequestMapping(value = "/token/{app}/{type}", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody
	Object getTokenByType(
			HttpSession session,
			HttpServletRequest request,
			@PathVariable String app,
			@PathVariable String type) throws Exception{
		JSONObject jsonObject = new JSONObject();
	    try {
	    	UploadTokenClient client = upRepo.getUTClient(app, type);
	        UploadPolicy uploadPolicy = upRepo.getUP(app, type);
	        uploadPolicy.setExpiration(System.currentTimeMillis() + 3600 * 1000); //token过期时间，单位毫秒。-1表示永不过期。
	        String token = client.getUploadToken(uploadPolicy);
	        jsonObject.put( "state", "success" );
	        jsonObject.put( "token", token );
	        return outActionReturn(jsonObject, HttpStatus.OK);
	 
	    } catch (MediaException e) {
	    	throw new WebException(e);
	    }
	}
	
	/**
	 * 
	 * @param session
	 * @param request
	 * @param taobaoApi
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/manage/open/{api}", method = {RequestMethod.GET, RequestMethod.POST}, headers = "Accept=application/json")
	public @ResponseBody
	Object doPublicApi(
			HttpSession session,
			HttpServletRequest request,
			@RequestParam String app,
			@RequestParam String type,
			@PathVariable String api) throws Exception{
		
		MediaClient mediaClient = upRepo.getMediaClient(app, type);
		Result<?> resoult = null;
		
		if ("existsFile".equals(api)) {
			String dir = request.getParameter("dir");
			String name = request.getParameter("name");
			if (StringUtil.isBlank(dir) || StringUtil.isBlank(name))
				throw new WebException("api参数不正确", HttpStatus.BAD_REQUEST);
			resoult = mediaClient.existsFile(dir, name);
		} else if ("existsDir".equals(api)) {
			String dir = request.getParameter("dir");
			if (StringUtil.isBlank(dir))
				throw new WebException("api参数不正确", HttpStatus.BAD_REQUEST);
			resoult = mediaClient.existsDir(dir);
		} else if ("getFile".equals(api)) {
			String dir = request.getParameter("dir");
			String name = request.getParameter("name");
			if (StringUtil.isBlank(dir) || StringUtil.isBlank(name))
				throw new WebException("api参数不正确", HttpStatus.BAD_REQUEST);
			resoult = mediaClient.getFile(dir, name);
		} else if ("listDirs".equals(api)) {
			String dir = request.getParameter("dir");
			String pageStr = request.getParameter("page");
			String pageSizeStr = request.getParameter("pageSize");
			if (StringUtil.isBlank(dir))
				throw new WebException("api参数不正确", HttpStatus.BAD_REQUEST);
			int page = 1, pageSize = 100;
			try {
				if (StringUtil.isNotBlank(pageStr) )
					page = Integer.parseInt(pageStr);
			} catch (Exception e) {}
			try {
				if (StringUtil.isNotBlank(pageSizeStr) )
					pageSize = Integer.parseInt(pageSizeStr);
			} catch (Exception e) {}
			resoult = mediaClient.listDirs(dir, page, pageSize);
		} else if ("listFiles".equals(api)) {
			String dir = request.getParameter("dir");
			String pageStr = request.getParameter("page");
			String pageSizeStr = request.getParameter("pageSize");
			if (StringUtil.isBlank(dir))
				throw new WebException("api参数不正确", HttpStatus.BAD_REQUEST);
			int page = 1, pageSize = 100;
			try {
				if (StringUtil.isNotBlank(pageStr) )
					page = Integer.parseInt(pageStr);
			} catch (Exception e) {}
			try {
				if (StringUtil.isNotBlank(pageSizeStr) )
					pageSize = Integer.parseInt(pageSizeStr);
			} catch (Exception e) {}
			resoult = mediaClient.listFiles(dir, page, pageSize);
		}  else {
			throw new WebException("api未实现", HttpStatus.NOT_IMPLEMENTED);
		}
		return outActionReturn(resoult, HttpStatus.OK);
	}
	
	/**
	 * 
	 * @param session
	 * @param request
	 * @param taobaoApi
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/manage/security/{api}", method = {RequestMethod.GET, RequestMethod.POST}, headers = "Accept=application/json")
	public @ResponseBody
	Object doSecurityApi(
			HttpSession session,
			HttpServletRequest request,
			@RequestParam String app,
			@RequestParam String type,
			@PathVariable String api) throws Exception{
		
		MediaClient mediaClient = upRepo.getMediaClient(app, type);
		Result<?> resoult = null;
		
		if ("existsFile".equals(api)) {
			String dir = request.getParameter("dir");
			String name = request.getParameter("name");
			if (StringUtil.isBlank(dir) || StringUtil.isBlank(name))
				throw new WebException("api参数不正确", HttpStatus.BAD_REQUEST);
			resoult = mediaClient.existsFile(dir, name);
		} else if ("existsDir".equals(api)) {
			String dir = request.getParameter("dir");
			if (StringUtil.isBlank(dir))
				throw new WebException("api参数不正确", HttpStatus.BAD_REQUEST);
			resoult = mediaClient.existsDir(dir);
		} else if ("getFile".equals(api)) {
			String dir = request.getParameter("dir");
			String name = request.getParameter("name");
			if (StringUtil.isBlank(dir) || StringUtil.isBlank(name))
				throw new WebException("api参数不正确", HttpStatus.BAD_REQUEST);
			resoult = mediaClient.getFile(dir, name);
		} else if ("listDirs".equals(api)) {
			String dir = request.getParameter("dir");
			String pageStr = request.getParameter("page");
			String pageSizeStr = request.getParameter("pageSize");
			if (StringUtil.isBlank(dir))
				throw new WebException("api参数不正确", HttpStatus.BAD_REQUEST);
			int page = 1, pageSize = 100;
			try {
				if (StringUtil.isNotBlank(pageStr) )
					page = Integer.parseInt(pageStr);
			} catch (Exception e) {}
			try {
				if (StringUtil.isNotBlank(pageSizeStr) )
					pageSize = Integer.parseInt(pageSizeStr);
			} catch (Exception e) {}
			resoult = mediaClient.listDirs(dir, page, pageSize);
		} else if ("listFiles".equals(api)) {
			String dir = request.getParameter("dir");
			String pageStr = request.getParameter("page");
			String pageSizeStr = request.getParameter("pageSize");
			if (StringUtil.isBlank(dir))
				throw new WebException("api参数不正确", HttpStatus.BAD_REQUEST);
			int page = 1, pageSize = 100;
			try {
				if (StringUtil.isNotBlank(pageStr) )
					page = Integer.parseInt(pageStr);
			} catch (Exception e) {}
			try {
				if (StringUtil.isNotBlank(pageSizeStr) )
					pageSize = Integer.parseInt(pageSizeStr);
			} catch (Exception e) {}
			resoult = mediaClient.listFiles(dir, page, pageSize);
		} else if ("deleteDir".equals(api)) {
			String dir = request.getParameter("dir");
			String force = request.getParameter("force");
			if (StringUtil.isBlank(force) || "false".equalsIgnoreCase(force))
				resoult = mediaClient.deleteDir(dir);
			else {
				resoult = deleteDir(dir, mediaClient);
			}
		} else {
			throw new WebException("api未实现", HttpStatus.NOT_IMPLEMENTED);
		}
		return outActionReturn(resoult, HttpStatus.OK);
	}
	
	private Result<?> deleteDir (String dir,MediaClient mediaClient) throws WebException {
		int start = 1, limit = 2;
		do {
			Result<PagedList<MediaFile>> mediaFilesResult  = mediaClient.listFiles(dir, start, limit);
			if (!mediaFilesResult.isSuccess()){
				throw new WebException(mediaFilesResult.getT());
			} 
			if (mediaFilesResult.getData().size() > 0) {
				for (MediaFile mf : mediaFilesResult.getData()){
					logger.debug("删除文件:" + dir + "/" + mf.getName());
					mediaClient.deleteFile(dir,  mf.getName());
				}
			} else {
				break;
			}
		} while(true);
		do {
			Result<PagedList<MediaDir>> mediaDirsResult  = mediaClient.listDirs(dir, start, limit);
			if (!mediaDirsResult.isSuccess()){
				throw new WebException(mediaDirsResult.getT());
			} 
			if (mediaDirsResult.getData().size() > 0) {
				for (MediaDir df : mediaDirsResult.getData()){
					deleteDir(df.getDir(), mediaClient);
				}
			} else {
				break;
			}
		} while(true);
		logger.debug("删除目录:" + dir);
		return mediaClient.deleteDir(dir);
	}

}
