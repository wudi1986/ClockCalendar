package com.yktx.check.bean;

import java.io.Serializable;

public class ChatGroupJoinBean implements Serializable{
	/** 群聊标题 */
	private String groupName;
	/** 群聊ID */
	private String chatGroupId;
	/** 勋章来源, 1为123服务器, 2为七牛 */
	private int badgeSource;
	/** 勋章路径 */
	private String badgePath;
	/** buildingId */
	private String buildingId;
	
	/** 未读消息数 */
	private int lastNum;
	
	
	
	public int getLastNum() {
		return lastNum;
	}
	public void setLastNum(int lastNum) {
		this.lastNum = lastNum;
	}
	public ChatGroupJoinBean() {
		super();
	}
	public ChatGroupJoinBean(String groupName, String chatGroupId,
			int badgeSource, String badgePath, String buildingId) {
		super();
		this.groupName = groupName;
		this.chatGroupId = chatGroupId;
		this.badgeSource = badgeSource;
		this.badgePath = badgePath;
		this.buildingId = buildingId;
	}
	@Override
	public String toString() {
		return "ChatGroupJoinBean [groupName=" + groupName + ", chatGroupId="
				+ chatGroupId + ", badgeSource=" + badgeSource + ", badgePath="
				+ badgePath + ", buildingId=" + buildingId + "]";
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getChatGroupId() {
		return chatGroupId;
	}
	public void setChatGroupId(String chatGroupId) {
		this.chatGroupId = chatGroupId;
	}
	public int getBadgeSource() {
		return badgeSource;
	}
	public void setBadgeSource(int badgeSource) {
		this.badgeSource = badgeSource;
	}
	public String getBadgePath() {
		return badgePath;
	}
	public void setBadgePath(String badgePath) {
		this.badgePath = badgePath;
	}
	public String getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}
	
	

}
