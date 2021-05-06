package com.noknown.framework.common.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @param <T> 数据类型
 * @author guodong
 */
public class PageData<T> implements Serializable {

	private static final long serialVersionUID = 6154488472992859862L;
	/**
	 * 对象总数
	 */
	private long total;
	/**
	 * 分页数据
	 */
	private Collection<?> data;
	/**
	 * 第几页
	 */
	private int pageNum;
	/**
	 * 总页数
	 */
	private int totalPage;
	/**
	 * 第几个
	 */
	private int start;

	/**
	 * 分页模式
	 *      false: 更具数量start等信息计算页数
	 *      true: 手动设置
	 */
	private boolean pageModel = false;

	/**
	 * 每页多少条
	 */
	private int limit;
	private List<Integer> pages;
	private int liststep = 10;


	public List<Integer> getPages() {
		pages = new ArrayList<Integer>();
		//从第几页开始显示分页信息
		int listbegin = (this.getPageNum() - (int) Math.ceil((double) liststep / 2));
		if (listbegin < 1) {
			listbegin = 1;
		}
		//分页信息显示到第几页
		int listend = this.getPageNum() + liststep / 2;
		if (listend > getTotalPage()) {
			listend = getTotalPage() + 1;
		}
		for (; listbegin < listend; listbegin++) {
			pages.add(listbegin);
		}
		return pages;
	}

	public List<Integer> pager(int pageNO, int liststep, int pageCount) {
		List<Integer> itemsIndex = new ArrayList<>();
		//从第几页开始显示分页信息
		int listbegin = (pageNO - (int) Math.ceil((double) liststep / 2));
		if (listbegin < 1) {
			listbegin = 1;
		}
		//分页信息显示到第几页
		int listend = pageNO + liststep / 2;
		if (listend > pageCount) {
			listend = pageCount + 1;
		}
		for (; listbegin < listend; listbegin++) {
			itemsIndex.add(listbegin);
		}
		return itemsIndex;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageNum() {
		if (!pageModel && limit != 0) {
			pageNum = (start+limit)/limit;
		}
		return pageNum;
	}

	public int getTotalPage() {
		if (limit != 0) {
			totalPage = (int) (total/limit);
		}
		int num = totalPage*limit;
		if(num < total){
			totalPage += 1;
		}
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public Collection<?> getData() {
		return data;
	}

	public void setData(Collection<?> collection) {
		this.data = collection;
	}

	public boolean isPageModel() {
		return pageModel;
	}

	public PageData<T> setPageModel(boolean pageModel) {
		this.pageModel = pageModel;
		return this;
	}
}
