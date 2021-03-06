package com.yktx.check.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class ByTaskIdBean implements Serializable{
	private int currentPage;
	private int totalCount;
	private int totalPage;
	private int pageLimit;
	private ArrayList<TaskItemBean> listData;
	private String nextPage;
	private String prevPage;
	
	public ByTaskIdBean() {
		super();
	}
	@Override
	public String toString() {
		return "ByTaskIdBean [currentPage=" + currentPage + ", totalCount="
				+ totalCount + ", totalPage=" + totalPage + ", pageLimit="
				+ pageLimit + ", listData=" + listData + ", nextPage="
				+ nextPage + ", prevPage=" + prevPage + "]";
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public ByTaskIdBean(int currentPage, int totalCount, int totalPage,
			int pageLimit, ArrayList<TaskItemBean> listData,
			String nextPage, String prevPage) {
		super();
		this.currentPage = currentPage;
		this.totalCount = totalCount;
		this.totalPage = totalPage;
		this.pageLimit = pageLimit;
		this.listData = listData;
		this.nextPage = nextPage;
		this.prevPage = prevPage;
	}
	public int getPageLimit() {
		return pageLimit;
	}
	public void setPageLimit(int pageLimit) {
		this.pageLimit = pageLimit;
	}
	public ArrayList<TaskItemBean> getListData() {
		return listData;
	}
	public void setListData(ArrayList<TaskItemBean> listData) {
		this.listData = listData;
	}
	public String getNextPage() {
		return nextPage;
	}
	public void setNextPage(String nextPage) {
		this.nextPage = nextPage;
	}
	public String getPrevPage() {
		return prevPage;
	}
	public void setPrevPage(String prevPage) {
		this.prevPage = prevPage;
	}
	
	

}
