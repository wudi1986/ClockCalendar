package com.yktx.check;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.content.Intent;
import android.os.Handler;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.yktx.check.util.Tools;
import com.yktx.first.viewpager.FirstViewPagerActivity;

public class MainActivity extends BaseActivity {
	private ImageView mMainImage,circleBg;
	private String userName;
	private RelativeLayout main_Layout;
	/** 是否第一次打开应用，显示新首页 */
	boolean isFirstOpen;

	@Override
	protected void findViews() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_main);
		mMainImage = (ImageView) findViewById(R.id.main_Image);
		circleBg = (ImageView) findViewById(R.id.circleBg);
		
		userName = settings.getString("username", null);
		main_Layout = (RelativeLayout) findViewById(R.id.main_Layout);
		/** 渠道号 */
		AnalyticsConfig.setChannel("yyb");
		/** 友盟自动更新 */
//		MobclickAgent.updateOnlineConfig(mContext);
		/** 百度自动更新 */
//		BDAutoUpdateSDK.uiUpdateAction(this, new MyUICheckUpdateCallback());
		/**360自动更新 */
//		UpdateManager.checkUpdate(this); 
		
		/** 设置是否对日志信息进行加密, 默认false(不加密). */
		AnalyticsConfig.enableEncrypt(false);
		isFirstOpen = settings.getBoolean("isFirstOpen", true);

	}
//	private class MyUICheckUpdateCallback implements UICheckUpdateCallback {
//
//		@Override
//		public void onCheckComplete() {
////			dialog.dismiss();
//			Tools.getLog(Tools.d, "aaa", "百度自动gengxing!!!!");
//		}
//
//	}
	
	boolean isFirst = true;

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		// Conn();
		if (getIntent().getBooleanExtra("isalarm", false)) {
			MobclickAgent.onEvent(this, "clickalarmnotification");
		}
		
		ViewTreeObserver vto = main_Layout.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				Tools.getLog(Tools.i, "bbb",
						"MainActivity.allHeight =========== " );
				if (isFirst) {
					isFirst = false;
					int allHeight = main_Layout.getHeight();
					mEditor.putInt("allHeight", allHeight);
					mEditor.commit();
					Tools.getLog(Tools.i, "bbb",
							"MainActivity.allHeight =========== " + allHeight);
					init2();
					
					// 输出友盟 页面统计 的Log
					// String str = getDeviceInfo(mContext);
					// Tools.getLog(Tools.d,"aaa", "====="+str);
				}
			}
		});
		
//		ShapeDrawable shape = (ShapeDrawable) getResources().getDrawable(R.drawable.shape_default);
//		circleBg.setBackgroundResource(R.drawable.shape_default);
	
		


	}
	
	private void init2(){
		Animation anim=AnimationUtils.loadAnimation(this,R.anim.qidong_small_big); 
		anim.setDuration(1000L);
		anim.setFillAfter(true);
		
		anim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				if (isLogin) {
					HashSet<String> set = new HashSet<String>();
					long currentTime = System.currentTimeMillis();
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyyMMdd");
					Date date = new Date(currentTime);
					Tools.getLog(Tools.d, "aaa",
							"时间：" + formatter.format(date));
					set.add(formatter.format(date));
					JPushInterface.setAliasAndTags(mContext, userID, set,
							new TagAliasCallback() {

								@Override
								public void gotResult(int arg0,
										String arg1, Set<String> arg2) {
									// TODO Auto-generated method stub
									Tools.getLog(Tools.d, "aaa", "arg0:"
											+ arg0);
									Tools.getLog(Tools.d, "aaa", "arg1:"
											+ arg1);
									Tools.getLog(Tools.d, "aaa", "arg2:"
											+ arg2.toString());
								}
							});
				}

				new Handler().postDelayed(new Runnable() {
					//
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Intent in = null;
						// if(userName != null ){
						// in = new Intent(mContext,
						// ClockMainActivity.class);
						// }else{
						// in = new Intent(mContext, LoginActivity.class);
						// }
						if (isFirstOpen) {
							in = new Intent(mContext, FirstViewPagerActivity.class);
						} else {

							if (isLogin) {
								EMChatManager.getInstance().login(userID, "111111" , new EMCallBack() {
						            
						            @Override
						            public void onSuccess() {
						            	Intent in = new Intent(mContext, ClockMainActivity.class);
						            	startActivity(in);
										finish();
										overridePendingTransition(R.anim.slide_alpha_in_right, R.anim.slide_alpha_out_left);
						            }
						            
						            @Override
						            public void onProgress(int progress, String status) {

						            }
						            @Override
						            public void onError(int code, String error) {
						            	Intent in = new Intent(mContext, ClockMainActivity.class);
						            	startActivity(in);
										finish();
										overridePendingTransition(R.anim.slide_alpha_in_right, R.anim.slide_alpha_out_left);
						            }
						        });
								return;
								
							} else {
								in = new Intent(mContext, LoginActivity.class);
							}
						}
						startActivity(in);
						finish();
						overridePendingTransition(R.anim.slide_alpha_in_right, R.anim.slide_alpha_out_left);
					}
				}, 1000);
			}
		});
		circleBg.setAnimation(anim);
		
	}

	@Override
	protected void setListeners() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("SplashScreen"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
													// onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("SplashScreen"); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this);
	}
}
