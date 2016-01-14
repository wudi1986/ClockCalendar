package com.yktx.check.bean;

import java.io.Serializable;

public class ChatGroupInfoBean implements Serializable{
	/** //环信群聊Id */
	String chatGroupId;
	/** buildingId */
	String buildingId;
	/** 群名 */
	String groupName;
	/** 创建时间 */
	String cTime;
	public ChatGroupInfoBean(String chatGroupId, String buildingId,
			String groupName, String cTime) {
		super();
		this.chatGroupId = chatGroupId;
		this.buildingId = buildingId;
		this.groupName = groupName;
		this.cTime = cTime;
	}
	public ChatGroupInfoBean() {
		super();
	}
	public String getChatGroupId() {
		return chatGroupId;
	}
	public void setChatGroupId(String chatGroupId) {
		this.chatGroupId = chatGroupId;
	}
	public String getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getcTime() {
		return cTime;
	}
	public void setcTime(String cTime) {
		this.cTime = cTime;
	}
	

}
