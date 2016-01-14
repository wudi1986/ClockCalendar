package com.yktx.check.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class TopPointUserBean implements Serializable{
	private String point;
	private String avartarPath;
	private String name;
	private String userId;
	private int imageSource;
	
	/** 勋章的宽度 动态获取  */
	private int medalwidth = 0;
	/** //此用户所有勋章  */ 
	private ArrayList<MedalBean> badges = new ArrayList<MedalBean>();
	
	@Override
	public String toString() {
		return "TopPointUserBean [point=" + point + ", avartarPath="
				+ avartarPath + ", name=" + name + ", userId=" + userId
				+ ", imageSource=" + imageSource + ", medalwidth=" + medalwidth
				+ ", badges=" + badges + "]";
	}
	public TopPointUserBean() {
		super();
	}
	public TopPointUserBean(String point, String avartarPath, String name,
			String userId, int imageSource, int medalwidth,
			ArrayList<MedalBean> badges) {
		super();
		this.point = point;
		this.avartarPath = avartarPath;
		this.name = name;
		this.userId = userId;
		this.imageSource = imageSource;
		this.medalwidth = medalwidth;
		this.badges = badges;
	}
	public int getMedalwidth() {
		return medalwidth;
	}
	public void setMedalwidth(int medalwidth) {
		this.medalwidth = medalwidth;
	}
	public ArrayList<MedalBean> getBadges() {
		return badges;
	}
	public void setBadges(ArrayList<MedalBean> badges) {
		this.badges = badges;
	}
	public int getImageSource() {
		return imageSource;
	}
	public void setImageSource(int imageSource) {
		this.imageSource = imageSource;
	}
	public String getPoint() {
		return point;
	}
	public void setPoint(String point) {
		this.point = point;
	}
	public String getAvartarPath() {
		return avartarPath;
	}
	public void setAvartarPath(String avartarPath) {
		this.avartarPath = avartarPath;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
