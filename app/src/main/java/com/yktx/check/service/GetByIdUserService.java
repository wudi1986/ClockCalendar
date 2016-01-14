package com.yktx.check.service;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.yktx.check.bean.CreateUserBean;
import com.yktx.check.conn.ServiceListener;
import com.yktx.check.conn.UrlParams;
import com.yktx.check.util.Contanst;
import com.yktx.check.util.Tools;

public class GetByIdUserService extends Service{

	public GetByIdUserService(String requestType,
			Hashtable<String, String> params, String urlParams,
			ServiceListener serviceListener) {
		super(requestType, params, urlParams, serviceListener);
		// TODO Auto-generated constructor stub
		this.url=UrlParams.IP+Contanst.HTTP_GETBYIDUSER+urlParams;
		Tools.getLog(Tools.i, "bbb", "url ===== "+url);
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
			if (Contanst.HTTP_SUCCESS.equals(retcode)) {//成功获取数据
//				JSONObject ongoingTasks = result.getJSONObject("ongoingTasks");
//				JSONObject suspendTasks = result.getJSONObject("suspendTasks");
//				JSONArray jsonArray = result.getJSONArray("message");
//				for(int j = 0; j < jsonArray.length(); j++){
//					String str = jsonArray.getString(j);
//					Gson gson = new Gson();
//					MyTaskBean userTaskBean = gson.fromJson(str, MyTaskBean.class);
//					list.add(userTaskBean);
//				}
				String message = result.getString("message");
				Gson gson = new Gson();
				CreateUserBean createUserBean = gson.fromJson(message, CreateUserBean.class);
				serviceListener.getJOSNdataSuccess(createUserBean, retcode , Contanst.GETBYIDUSER);
			} else {
				String errmsg = (String) result.get("message");
				serviceListener.getJOSNdataFail(erroCodeParse(retcode), errmsg, Contanst.GETBYIDUSER);
			}
		} catch (JSONException e) {
			serviceListener.getJOSNdataFail("", "服务器异常", Contanst.GETBYIDUSER);
			e.printStackTrace();
		}
	}

	@Override
	void httpFail(String erro) {
		// TODO Auto-generated method stub
		serviceListener.getJOSNdataFail("", "网络异常", Contanst.GETBYIDUSER);
		Tools.getLog(Tools.i, "aaa", "httpFailhttpFail");
	}

	@Override
	void parse(String reponse) {
		// TODO Auto-generated method stub
		
	}

}
