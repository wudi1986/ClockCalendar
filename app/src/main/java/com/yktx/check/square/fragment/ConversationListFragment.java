package com.yktx.check.square.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.ui.ChatActivity;
import com.easemob.chatuidemo.ui.EasemobConstant;
import com.easemob.exceptions.EaseMobException;
import com.yktx.check.R;
import com.yktx.check.TaskInfoActivity;
import com.yktx.check.adapter.EaseConversationAdapater;
import com.yktx.check.bean.ChatGroupInfoBean;
import com.yktx.check.bean.ChatGroupJoinBean;
import com.yktx.check.conn.ServiceListener;
import com.yktx.check.conn.UrlParams;
import com.yktx.check.service.Service;
import com.yktx.check.util.Contanst;
import com.yktx.check.util.Tools;

public class ConversationListFragment extends BaseFragment implements
		ServiceListener {

    private TextView errorText;
	private Activity mContext;
	String userID;
	EaseConversationAdapater adapter;
	ListView listView;
	Map<String, EMConversation> conversations;
	
	private List<ChatGroupJoinBean> conversationList;
	ChatGroupInfoBean chatGroupInfoBean;
	boolean isClickGroup;
	RelativeLayout loadingView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mContext = ConversationListFragment.this.getActivity();
		settings = mContext
				.getSharedPreferences("clock", mContext.MODE_PRIVATE);
		userID = settings.getString("userid", null);
		View view = inflater.inflate(R.layout.ease_fragment_contact_list,
				container, false);
		// 获得所有会话
		conversations = EMChatManager.getInstance().getAllConversations();
		listView = (ListView) view.findViewById(R.id.contact_list);
		loadingView = (RelativeLayout) view.findViewById(R.id.loadingView);
		adapter = new EaseConversationAdapater(mContext);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub

				// TODO Auto-generated method stub
				if(isClickGroup){
					return;
				}
				isClickGroup = true;
				ChatGroupJoinBean conversation = conversationList.get(position);
				String username = conversation.getChatGroupId();
				// if (username == null) {
				getChatGroupConn(conversation.getBuildingId());
				// } else {
				// 进入聊天页面
//				Intent intent = new Intent(getActivity(), ChatActivity.class);
//				intent.putExtra(EasemobConstant.EXTRA_CHAT_TYPE,
//						EasemobConstant.CHATTYPE_GROUP);
//				// it's single chat
//				intent.putExtra(EasemobConstant.EXTRA_USER_ID, username);
//				startActivity(intent);

				// }
			}
		});
		conn();
		return view;
	}

	public void getChatGroupConn(String buildingId) {
		StringBuffer sb = new StringBuffer();
		sb.append("?buildingId=");
		sb.append(buildingId);
		// sb.append("145956070676759040");

		Service.getService(Contanst.HTTP_CHAT_GETCHATGROUP, null,
				sb.toString(), ConversationListFragment.this).addList(null)
				.request(UrlParams.GET);
	}

	/**
	 * 进群联网通知app服务器
	 */
	public void addUserToGroupConn(String chatGroupId) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			params.add(new BasicNameValuePair("chatGroupId", chatGroupId));
			params.add(new BasicNameValuePair("userId", userID));
		} catch (Exception e) {
			e.printStackTrace();
		}

		Service
				.getService(Contanst.HTTP_CHAT_ADDUSERTOCHATGROUP, null, null,
						ConversationListFragment.this).addList(params)
				.request(UrlParams.POST);
	}

	@Override
	public void getJOSNdataSuccess(Object bean, String sccmsg, int connType) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.what = Contanst.BEST_INFO_OK;
		msg.obj = bean;
		msg.arg1 = connType;
		mHandler.sendMessage(msg);
	}

	@Override
	public void getJOSNdataFail(String errcode, String errmsg, int connType) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.what = Contanst.BEST_INFO_FAIL;
		msg.obj = errmsg;
		msg.arg1 = connType;
		mHandler.sendMessage(msg);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Contanst.BEST_INFO_OK:
				switch (msg.arg1) {
				case Contanst.USER_GETALLCHATGROUP:
					conversationList = (List<ChatGroupJoinBean>) msg.obj;
					for (int i = 0; i < conversationList.size(); i++) {
						EMConversation eMConversation = conversations
								.get(conversationList.get(i).getChatGroupId());
						if (eMConversation != null) {
							conversationList.get(i).setLastNum(
									eMConversation.getUnreadMsgCount());
						}
					}
					if(loadingView.getVisibility() == View.VISIBLE){
						loadingView.setVisibility(View.GONE);
					}
					adapter.setList(conversationList);
					adapter.notifyDataSetChanged();
					break;
				case Contanst.CHAT_GETCHATGROUP:
					// 进群接口
					chatGroupInfoBean = (ChatGroupInfoBean) msg.obj;

					addUserToGroupConn(chatGroupInfoBean.getChatGroupId());
					break;
				case Contanst.CHAT_ADDUSERTOCHATGROUP:
					final String chatGroupId = chatGroupInfoBean
							.getChatGroupId();
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								EMGroupManager.getInstance().joinGroup(
										chatGroupId);
								ConversationListFragment.this.getActivity()
										.runOnUiThread(new Runnable() {
											public void run() {
												Intent in = new Intent(
														ConversationListFragment.this
																.getActivity(),
														ChatActivity.class);
												in.putExtra(
														"chatType",
														EasemobConstant.CHATTYPE_GROUP);
												in.putExtra("userId",
														chatGroupId);
												startActivityForResult(in, 0);
											}
										});

							} catch (EaseMobException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}// 需异步处理
						}
					}).start();

					break;
				}
				break;
			case Contanst.BEST_INFO_FAIL:
				String message = (String) msg.obj;
				switch (msg.arg1) {
				case Contanst.USER_GETALLCHATGROUP:
					// conn(1);
					// adapter.setList(fansItemBeans);
					// adapter.notifyDataSetChanged();
					break;
				}
				break;
			}
		}
	};

	public SharedPreferences settings;

	private void conn() {
		if(loadingView.getVisibility() == View.GONE){
			loadingView.setVisibility(View.VISIBLE);
		}
		settings = getActivity().getSharedPreferences("clock",
				getActivity().MODE_PRIVATE);
		StringBuffer sb = new StringBuffer();
		sb.append("?userId=");
		sb.append(settings.getString("userid", null));
		Service.getService(Contanst.HTTP_USER_GETALLCHATGROUP, null,
				sb.toString(), ConversationListFragment.this).addList(null)
				.request(UrlParams.GET);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
		if(requestCode == 0){
			isClickGroup = false;
		}
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Tools.getLog(Tools.d, "ccc", "ConversationListFragment  onResume");
		if(isConversationListFragmentVisibleToUser){
			conn();
		}
	}
	boolean isConversationListFragmentVisibleToUser;
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		isConversationListFragmentVisibleToUser = isVisibleToUser;
		Tools.getLog(Tools.d, "ccc", "ConversationListFragment  setUserVisibleHint");
	}
	// @Override
	// public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo
	// menuInfo) {
	// getActivity().getMenuInflater().inflate(R.menu.em_delete_message, menu);
	// }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        boolean handled = false;
//        boolean deleteMessage = false;
//        /*if (item.getItemId() == R.id.delete_message) {
//            deleteMessage = true;
//            handled = true;
//        } else*/ if (item.getItemId() == R.id.delete_conversation) {
//            deleteMessage = true;
//            handled = true;
//        }
//        EMConversation tobeDeleteCons = conversationListView.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
//        // 删除此会话
//        EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup(), deleteMessage);
//        InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
//        inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
//        refresh();
//
////        // 更新消息未读数
////        ((MainActivity) getActivity()).updateUnreadLabel();
//        
//        return handled ? true : super.onContextItemSelected(item);
//    }

}
