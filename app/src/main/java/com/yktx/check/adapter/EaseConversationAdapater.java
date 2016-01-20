package com.yktx.check.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.easeui.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yktx.check.bean.ChatGroupJoinBean;
import com.yktx.check.util.Tools;

/**
 * 会话列表adapter
 * 
 */
public class EaseConversationAdapater extends BaseAdapter {

	private List<ChatGroupJoinBean> conversationList = new ArrayList<ChatGroupJoinBean>();
	protected int primaryColor;
	protected int secondaryColor;
	protected int timeColor;
	protected int primarySize;
	protected int secondarySize;
	protected float timeSize;
	Context mContext;

	public EaseConversationAdapater(Context context) {
		this.mContext = context;
	}

	public void setList(List<ChatGroupJoinBean> conversationList) {
		this.conversationList = conversationList;
	}

	@Override
	public int getCount() {
		return conversationList.size();
	}

	@Override
	public ChatGroupJoinBean getItem(int arg0) {
		if (arg0 < conversationList.size()) {
			return conversationList.get(arg0);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.ease_row_chat_history, parent, false);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.unreadLabel = (TextView) convertView
					.findViewById(R.id.unread_msg_number);
//			holder.message = (TextView) convertView.findViewById(R.id.message);
//			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
//			holder.msgState = convertView.findViewById(R.id.msg_state);
			holder.list_itease_layout = (RelativeLayout) convertView
					.findViewById(R.id.list_itease_layout);
			convertView.setTag(holder);
		}

		// 获取与此用户/群组的会话
		ChatGroupJoinBean conversation = getItem(position);
		// 获取用户username或者群组groupid
		String username = conversation.getGroupName();

		// 群聊消息，显示群聊头像
		ImageLoader.getInstance().displayImage(
				Tools.getImagePath(conversation.getBadgeSource())
						+ conversation.getBadgePath(), holder.avatar);
		holder.name.setText(username);

		if (conversation.getLastNum() > 0) {
			// 显示与此用户的消息未读数
			holder.unreadLabel
					.setText(String.valueOf(conversation.getLastNum()));
			holder.unreadLabel.setVisibility(View.VISIBLE);
		} else {
			holder.unreadLabel.setVisibility(View.INVISIBLE);
		}


		return convertView;
	}

	private static class ViewHolder {
		/** 和谁的聊天记录 */
		TextView name;
		/** 消息未读数 */
		TextView unreadLabel;
//		/** 最后一条消息的内容 */
//		TextView message;
		/** 最后一条消息的时间 */
//		TextView time;
//		/** 用户头像 */
		ImageView avatar;
//		/** 最后一条消息的发送状态 */
//		View msgState;
		/** 整个list中每一行总布局 */
		RelativeLayout list_itease_layout;

	}
}
