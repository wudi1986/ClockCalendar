package com.yktx.check.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import com.yktx.check.R;

public class TaskVisityDialog extends Dialog{
	public Activity mActivity;
	public Context mContext;
	public TextView mResult,mCancel;
	public TaskVisityDialog(Activity activity) {
		super(activity, R.style.dialog);
		// TODO Auto-generated constructor stub
		mContext = activity;
		mActivity = activity;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setOnShowListener(tener);
		setContentView(R.layout.taskvisity_dialog);
		mResult = (TextView) findViewById(R.id.taskvisity_dialog_result);
		mCancel = (TextView) findViewById(R.id.taskvisity_dialog_cancel);
	}
	OnShowListener tener = new OnShowListener() {
		@Override
		public void onShow(DialogInterface arg0) {
			// TODO Auto-generated method stub
			WindowManager windowManager = mActivity.getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.width = (int) (display.getWidth()); // 设置宽度
			getWindow().setAttributes(lp);
		}

	};

}
