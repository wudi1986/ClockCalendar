package com.yktx.check.dialog;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.yktx.check.BaseActivity;
import com.yktx.check.R;
import com.yktx.check.bean.TaskGetImageBean;
import com.yktx.check.conn.ServiceListener;
import com.yktx.check.conn.UrlParams;
import com.yktx.check.service.Service;
import com.yktx.check.util.Contanst;
import com.yktx.check.util.FileURl;
import com.yktx.check.util.Tools;
import com.yktx.check.view.RoundProgressBar;

public class PlayPhotoDialog extends Dialog implements ServiceListener {
	public TextView dailycam_success_dialog_totalDateCount,
			dailycam_success_dialog_photoNum, dailycam_success_dialog_day,
			dailycam_success_dialog_taskName;

	public ImageView dailycam_success_dialog_close,
			dailycam_success_dialog_imageMain1,
			dailycam_success_dialog_imageMain2, dailycam_success_dialog_play,
			dailycam_success_dialog_shareWeiXin,
			dailycam_success_dialog_topImage;

	private LinearLayout dailycam_success_dialog_blank;
	private RelativeLayout dailycam_success_dialog_imageMainLayout;

	RoundProgressBar roundProgressBar;

	private Context mContext;
	private Activity mActivity;
	String ImageUrl, taskName;
	int checkDateCount;
	String userId, taskId,mCity,mTakeDate,mUserName;

	Thread thread;
	long sleepTime = 1000;
	private int currentImg = 0;
	private int currentDay = 0;
	private int imageNum = 0;

	boolean isLoadingComplete, isSleepOver;
	boolean isPause = true;
	boolean isDownLoad;
	ArrayList<TaskGetImageBean> curList = new ArrayList<TaskGetImageBean>(10);

	public DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.signin_local_gallry)
			.showImageForEmptyUri(R.drawable.signin_local_gallry)
			.showImageOnFail(R.drawable.signin_local_gallry)
			.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
			.cacheInMemory(false)
			// 启用内存缓存
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

	private Bitmap mSharedBitmap;
	boolean isLoadingOver;

	public PlayPhotoDialog(Activity context, String url, String name,
			int checkDateCount, String userId, String taskId, String userName,
			String takeDate, String city,int imageNum) {
		super(context, R.style.dialogsuccess);
		// TODO Auto-generated constructor stub
		mActivity = context;
		mContext = context;
		ImageUrl = url;
		taskName = name;
		this.checkDateCount = checkDateCount;
		this.userId = userId;
		this.taskId = taskId;
		mCity = city;
		mTakeDate = takeDate;
		mUserName = userName;
		this.imageNum = imageNum;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dailycam_success_dialog1);
		setOnShowListener(tener);
		Conn();
		dailycam_success_dialog_totalDateCount = (TextView) findViewById(R.id.dailycam_success_dialog_totalDateCount);
		dailycam_success_dialog_photoNum = (TextView) findViewById(R.id.dailycam_success_dialog_photoNum);
		dailycam_success_dialog_day = (TextView) findViewById(R.id.dailycam_success_dialog_day);
		dailycam_success_dialog_taskName = (TextView) findViewById(R.id.dailycam_success_dialog_taskName);

		dailycam_success_dialog_close = (ImageView) findViewById(R.id.dailycam_success_dialog_close);
		dailycam_success_dialog_imageMain1 = (ImageView) findViewById(R.id.dailycam_success_dialog_imageMain1);
		dailycam_success_dialog_imageMain2 = (ImageView) findViewById(R.id.dailycam_success_dialog_imageMain2);

		dailycam_success_dialog_play = (ImageView) findViewById(R.id.dailycam_success_dialog_play);
		dailycam_success_dialog_shareWeiXin = (ImageView) findViewById(R.id.dailycam_success_dialog_shareWeiXin);
		dailycam_success_dialog_topImage = (ImageView) findViewById(R.id.dailycam_success_dialog_topImage);
		dailycam_success_dialog_blank = (LinearLayout) findViewById(R.id.dailycam_success_dialog_blank);
		dailycam_success_dialog_imageMainLayout = (RelativeLayout) findViewById(R.id.dailycam_success_dialog_imageMainLayout);
		roundProgressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);
		// dailycam_success_dialog_totalDateCount.setText(checkDateCount + "");
		dailycam_success_dialog_day.setText("Day" + checkDateCount + "");
		dailycam_success_dialog_taskName.setText(taskName);// name
		
		StringBuffer sb = new StringBuffer();
		sb.append(mUserName);
		
		if(mTakeDate != null && mTakeDate.length() != 0){
			sb.append(" | "+mTakeDate.replace("-", "."));
		}
		
		if(mCity != null && mCity.length() != 0&&!mCity.equals("0")){
			sb.append(" | "+mCity);
		}
		
		dailycam_success_dialog_photoNum.setText(sb.toString());
		dailycam_success_dialog_topImage.setVisibility(View.GONE);
		if (ImageUrl.indexOf("http") == -1) {
			
			ImageLoader.getInstance().displayImage(
					FileURl.LOAD_FILE + ImageUrl,
					dailycam_success_dialog_imageMain1, options,
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							// TODO Auto-generated method stub
							//加载失败的时候执行  
							if(imageNum == 0&&checkDateCount == 0){
								dailycam_success_dialog_imageMain1.setImageResource(R.drawable.zw_wutu);
							}else{
								dailycam_success_dialog_imageMain1.setImageResource(R.drawable.zw_wenzidaka);
							}
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							// TODO Auto-generated method stub
							mSharedBitmap = loadedImage;
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
							// TODO Auto-generated method stub

						}
					});
		} else {

//			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//					LayoutParams.WRAP_CONTENT, 1);
//			params.setMargins(0, (int) (50 * BaseActivity.DENSITY), 0, 0);
//			dailycam_success_dialog_topImage.setLayoutParams(params);
//			dailycam_success_dialog_topImage.setVisibility(View.INVISIBLE);
			ImageLoader.getInstance().displayImage(ImageUrl,
					dailycam_success_dialog_imageMain1, options,
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {
							// TODO Auto-generated method stub
							//开始加载的时候执行  
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							// TODO Auto-generated method stub
							//加载失败的时候执行  
							if(imageNum == 0&&checkDateCount == 0){
								dailycam_success_dialog_imageMain1.setImageResource(R.drawable.zw_wutu);
							}else{
								dailycam_success_dialog_imageMain1.setImageResource(R.drawable.zw_wenzidaka);
							}
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							// TODO Auto-generated method stub
							//加载成功的时候执行  
							mSharedBitmap = loadedImage;
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
							// TODO Auto-generated method stub
							//加载取消的时候执行  
							
						}
					});
		}
		dailycam_success_dialog_close
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dismiss();
					}
				});

		dailycam_success_dialog_blank
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dismiss();
					}
				});
		dailycam_success_dialog_shareWeiXin
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						mSharedClockPhoto.shared(taskName, mSharedBitmap);
					}
				});
		dailycam_success_dialog_imageMainLayout
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						if (!isLoadingOver) {
							if (isDownLoad)
								return;
							if (curList.size() == 0) {
								Toast.makeText(mContext, "暂无照片，可通过拍照打卡进行添加",
										Toast.LENGTH_SHORT).show();
								return;
							}
							isDownLoad = true;
							roundProgressBar.setVisibility(View.VISIBLE);
							dailycam_success_dialog_play
									.setVisibility(View.GONE);
							loadAllImage(0);
							return;
						}

						if (isPause) {
							if (curList.size() == 0) {
								Toast.makeText(mContext, "暂无照片，可通过拍照打卡进行添加",
										Toast.LENGTH_SHORT).show();
								return;
							}
							start();
							dispalyNextImage();
							Toast.makeText(mContext, "第一次播放需要加载图片请您耐心等待。",
									Toast.LENGTH_SHORT).show();
							dailycam_success_dialog_play
									.setVisibility(View.GONE);
							start();
						} else {
							Thread temp = thread;
							thread = null;
							if (temp != null)
								temp.interrupt();
							dailycam_success_dialog_play
									.setVisibility(View.VISIBLE);
						}
						isPause = !isPause;
					}
				});

	}

	private void loadAllImage(final int index) {
		String Url = Tools.getImagePath(curList.get(index).getImageSource())
				+ curList.get(index).getPath();
		ImageLoader.getInstance().loadImage(Url, new ImageSize(600, 800),
				options, new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub
						if (index + 1 >= curList.size()) {
							isLoadingOver = true;
							return;
						}
						loadAllImage(index + 1);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
						roundProgressBar.setProgress((index + 1) * 100
								/ curList.size());
						if (index + 1 >= curList.size()) {
							isLoadingOver = true;
							roundProgressBar.setVisibility(View.GONE);
							dispalyNextImage();
							dailycam_success_dialog_play
									.setVisibility(View.GONE);
							start();
							return;
						}
						loadAllImage(index + 1);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						// TODO Auto-generated method stub

					}
				}, new ImageLoadingProgressListener() {

					@Override
					public void onProgressUpdate(String imageUri, View view,
							int current, int total) {
						// TODO Auto-generated method stub

						roundProgressBar.setProgress(current * 100 / total
								/ curList.size() + index * 100 / curList.size());
					}
				});
	}

	public void Conn() {
		StringBuffer sb = new StringBuffer();
		sb.append("?userId=");
		sb.append(userId);
		sb.append("&taskId=");
		sb.append(taskId);
		Service.getService(Contanst.HTTP_TASK_GETIMAGE, null, sb.toString(),
				PlayPhotoDialog.this).addList(null)
				.request(UrlParams.GET);
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

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {

			case 0x110: // 播放下一张卡
				if (isLoadingComplete && isSleepOver) {
					isSleepOver = false;
					currentImg++;
					if (isRun)
						dispalyNextImage();
				}
				break;

			case Contanst.BEST_INFO_OK:

				switch (msg.arg1) {

				case Contanst.TASK_GETIMAGE:
					curList = (ArrayList<TaskGetImageBean>) msg.obj;
					break;
				}

				break;
			case Contanst.BEST_INFO_FAIL:
				String message = (String) msg.obj;
				Tools.getLog(Tools.d, "aaa", message);
				switch (msg.arg1) {

				case Contanst.TASK_GETIMAGE:
					break;
				}
				break;
			}
		}
	};

	private void start() {
		if (thread == null) {
			thread = new Thread() {
				@Override
				public void run() {
					Thread curThread = Thread.currentThread();
					while (thread != null && thread == curThread) {
						try {
							Thread.sleep(sleepTime);
							isSleepOver = true;
							Message msg = new Message();
							msg.what = 0x110;
							mHandler.sendMessage(msg);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			thread.start();

		}
	}

	String lastUrl;
	String lastDate;

	// 展示下一张图片
	private void dispalyNextImage() {

		// 如果发生数组越界
		if (currentImg >= curList.size()) {
			currentImg = 0;
			currentDay = 0;
		}
		isLoadingComplete = false;
		TaskGetImageBean bean = curList.get(currentImg);
		if (!bean.getCheckDate().equals(lastDate) ) {
			currentDay++;
			lastDate = bean.getCheckDate();
			dailycam_success_dialog_day.setText("Day"+currentDay);
		}
		
		String city = bean.getCity();
		String checkDate = bean.getCheckDate();
//		if(!bean.getCity().equals("0")){
//			city = bean.getCity();
//			dailycam_success_dialog_photoNum.setText(imageCount+"张·" + city);
//		} else{
//			dailycam_success_dialog_photoNum.setText(imageCount+"张");
//		}
		StringBuffer sb = new StringBuffer();
		sb.append(mUserName+"");
		if(checkDate != null && checkDate.length() != 0){
			sb.append(" | "+checkDate.replace("-", "."));
		}
		
		if(city != null &&  city.length() != 0&&!city.equals("0")){
			sb.append(" | "+city);
		}
		Tools.getLog(Tools.d, "aaa", "播放的数据："+sb.toString());
		Tools.getLog(Tools.d, "aaa", "smUserName："+mUserName);
		dailycam_success_dialog_photoNum.setText(sb.toString());
		
		
		bean.getcTime();

		if (lastUrl != null && lastUrl.length() > 0 && sleepTime > 800) {

			if (dailycam_success_dialog_imageMain1.getVisibility() != View.VISIBLE) {
				dailycam_success_dialog_imageMain1.setVisibility(View.VISIBLE);
			}
			// 初始化
			Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
			// 设置动画时间
			alphaAnimation.setDuration(500);
			alphaAnimation.setFillAfter(true);
			alphaAnimation.setAnimationListener(new AnimationListener() {

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
					ImageLoader.getInstance().displayImage(lastUrl,
							dailycam_success_dialog_imageMain1, options,
							new ImageLoadingListener() {

								@Override
								public void onLoadingStarted(String imageUri,
										View view) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onLoadingFailed(String imageUri,
										View view, FailReason failReason) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onLoadingComplete(String imageUri,
										View view, Bitmap loadedImage) {
									// TODO Auto-generated method stub
									Animation alphaAnimation = new AlphaAnimation(
											0.0f, 1.0f);
									// 设置动画时间
									alphaAnimation.setDuration(10);
									dailycam_success_dialog_imageMain1
											.startAnimation(alphaAnimation);
								}

								@Override
								public void onLoadingCancelled(String imageUri,
										View view) {
									// TODO Auto-generated method stub

								}
							});
				}
			});
			dailycam_success_dialog_imageMain1.startAnimation(alphaAnimation);

		} else {
			if (dailycam_success_dialog_imageMain1.getVisibility() != View.INVISIBLE) {
				dailycam_success_dialog_imageMain1
						.setVisibility(View.INVISIBLE);
			}
		}

		lastUrl = Tools.getImagePath(bean.getImageSource()) + bean.getPath();
		ImageLoader.getInstance().displayImage(lastUrl,
				dailycam_success_dialog_imageMain2, options,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
						isLoadingComplete = true;
						Message msg = new Message();
						msg.what = 0x110;
						mHandler.sendMessage(msg);

					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						// TODO Auto-generated method stub

					}
				});

	}

	SharedClockPhoto mSharedClockPhoto;

	public void setSharedClockPhoto(SharedClockPhoto mPhoto) {
		mSharedClockPhoto = mPhoto;
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

	boolean isRun = true;

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		isRun = false;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		isRun = true;
	}

	public interface SharedClockPhoto {
		public void shared(String taskName, Bitmap bitmap);
	}

}
