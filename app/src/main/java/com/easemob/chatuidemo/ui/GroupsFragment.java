/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.chatuidemo.ui;

import java.util.List;

import org.apache.harmony.javax.security.auth.Refreshable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.easemob.EMValueCallBack;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.yktx.check.R;

public class GroupsFragment extends Fragment {
	public static final String TAG = "GroupsActivity";
	private ListView groupListView;
	protected List<EMGroup> grouplist;
	private GroupAdapter groupAdapter;
	private View progressBar;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				refresh();
				break;
			case 1:
				Toast.makeText(GroupsFragment.this.getActivity(),
						R.string.Failed_to_get_group_chat_information,
						Toast.LENGTH_LONG).show();
				break;

			default:
				break;
			}
		};
	};

	LinearLayout layout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layout = (LinearLayout) inflater.inflate(R.layout.em_fragment_groups,
				container, false);

		grouplist = EMGroupManager.getInstance().getAllGroups();
		groupListView = (ListView) layout.findViewById(R.id.list);
		// show group list
		groupAdapter = new GroupAdapter(this.getActivity(), 1, grouplist);
		groupListView.setAdapter(groupAdapter);

		// 下拉刷新

		groupListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					// 进入群聊
					Intent intent = new Intent(GroupsFragment.this.getActivity(),
							ChatActivity.class);
					// it is group chat
					intent.putExtra("chatType", EasemobConstant.CHATTYPE_GROUP);
					intent.putExtra("userId", groupAdapter
							.getItem(position).getGroupId());
					startActivityForResult(intent, 0);
			}
			
		});
		
		return layout;
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}

	private void refresh() {
		grouplist = EMGroupManager.getInstance().getAllGroups();
		groupAdapter = new GroupAdapter(this.getActivity(), 1, grouplist);
		groupListView.setAdapter(groupAdapter);
		groupAdapter.notifyDataSetChanged();
	}


}
