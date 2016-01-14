package com.yktx.check.service;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.yktx.check.bean.ByTaskIdBean;
import com.yktx.check.bean.ChatGroupInfoBean;
import com.yktx.check.conn.ServiceListener;
import com.yktx.check.conn.UrlParams;
import com.yktx.check.util.Contanst;
import com.yktx.check.util.Tools;

public class ChatGetChatGroupService extends Service{

	public ChatGetChatGroupService(String requestType,
			Hashtable<String, String> params, String urlParams,
			ServiceListener serviceListener) {
		super(requestType, params, urlParams, serviceListener);
		// TODO Auto-generated constructor stub
		this.url = UrlParams.IP + Contanst.HTTP_CHAT_GETCHATGROUP+urlParams;
		Tools.getLog(Tools.i, "aaa", "url ===== " + url);
	}

	@Override
	void httpSuccess(String reponse) {
		// TODO Auto-generated method stub
		try {
			// ======================根据协议本地测试数据================================
			// reponse =
			// ========================联调接口时注释就可以============================
			Tools.getLog(Tools.i, "bbb", "reponse = "+reponse);
			JSONObject result = new JSONObject(reponse);
			String retcode =  result.getString("statusCode");

			Tools.getLog(Tools.i, "bbb", "retcode = "+retcode);
			Tools.getLog(Tools.i, "bbb", "getJOSNdataSuccessgetJOSNdataSuccess");
			String message = result.getString("message");
			if (Contanst.HTTP_SUCCESS.equals(retcode)&& message != null) {//成功获取数据
				Gson gson = new Gson();
				ChatGroupInfoBean chatGroupInfoBean =  gson.fromJson(message, ChatGroupInfoBean.class);
				serviceListener.getJOSNdataSuccess(chatGroupInfoBean, retcode , Contanst.CHAT_GETCHATGROUP);
			} else {
				String errmsg = null;
				if(message != null){
					errmsg = (String) result.get("message");
				}else{
					errmsg = "没有这个群！";
				}
				
				serviceListener.getJOSNdataFail(erroCodeParse(retcode), errmsg, Contanst.CHAT_GETCHATGROUP);
			}
		} catch (JSONException e) {
			serviceListener.getJOSNdataFail("", "服务器异常", Contanst.CHAT_GETCHATGROUP);
			e.printStackTrace();
		}
	}

	@Override
	void httpFail(String erro) {
		// TODO Auto-generated method stub
		serviceListener.getJOSNdataFail("", "网络异常", Contanst.CHAT_GETCHATGROUP);
		Tools.getLog(Tools.i, "aaa", "httpFailhttpFail");
	}

	@Override
	void parse(String reponse) {
		// TODO Auto-generated method stub
		
	}

}
