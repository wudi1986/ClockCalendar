package com.yktx.check.service;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import com.yktx.check.conn.ServiceListener;
import com.yktx.check.conn.UrlParams;
import com.yktx.check.util.Contanst;
import com.yktx.check.util.Tools;



/**
 * @author Administrator 修改密码
 *
 */
public class AddErrorLogService extends Service{

	public AddErrorLogService(String requestType,
			Hashtable<String, String> params, String urlParams,
			ServiceListener serviceListener) {
		super(requestType, params, urlParams, serviceListener);
		this.url=UrlParams.IP+Contanst.HTTP_ADDERRORLOG
				+".html";
		Tools.getLog(Tools.i,  "aaa", "url ===== "+url);
	}

	@Override
	void httpFail(String erro) {
		serviceListener.getJOSNdataFail("", "网络异常", Contanst.ADDERRORLOG);
		Tools.getLog(Tools.i,  "aaa", "httpFailhttpFail");
//		LodingActivity.isJion = false;
	}
	@Override
	void httpSuccess(String reponse) {
//		LodingActivity.isJion = false;
		try {
			// ======================根据协议本地测试数据================================
			// reponse =
			// ========================联调接口时注释就可以============================
			JSONObject result = new JSONObject(reponse);
			String retcode = result.get("statusCode").toString();
			
//			HomeBestView.listCount = Integer.parseInt((String)result.get("tot_count")); 
			Tools.getLog(Tools.i,  "aaa", "retcode = "+retcode);
			if (Contanst.HTTP_SUCCESS.equals(retcode)) {//成功获取数据
				serviceListener.getJOSNdataSuccess("ok", retcode , Contanst.ADDERRORLOG);
			} else {
				String errmsg = (String) result.get("message");
				serviceListener.getJOSNdataFail(erroCodeParse(retcode), errmsg, Contanst.ADDERRORLOG);
			}
		} catch (JSONException e) {
			serviceListener.getJOSNdataFail("", "服务器异常", Contanst.ADDERRORLOG);
			e.printStackTrace();
		}
	}

	@Override
	void parse(String reponse) {
		// TODO Auto-generated method stub
		
	}

}
