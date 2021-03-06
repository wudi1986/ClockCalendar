/**
The MIT License (MIT)

Copyright (c) 2014 singwhatiwanna
https://github.com/singwhatiwanna
http://blog.csdn.net/singwhatiwanna

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.yktx.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

import com.yktx.check.util.Tools;

import java.util.NoSuchElementException;

public class StickyLayout extends LinearLayout {
	private static final String TAG = "StickyLayout";
	private static final boolean DEBUG = false;

	public interface OnGiveUpTouchEventListener {
		public boolean giveUpTouchEvent(MotionEvent event);
	}

	public interface OnMoveOver {
		public void onMoveOver(int status);
		public void onMoving(int height, boolean isTouch);
	}


	private View mHeader;
	private View mContent;
	private OnGiveUpTouchEventListener mGiveUpTouchEventListener;
	private OnMoveOver onMoveOver;
	

	// header的高度 单位：px
	private int mOriginalHeaderHeight;
	private int mHeaderHeight;

	private int mStatus = STATUS_EXPANDED;
	public static final int STATUS_EXPANDED = 1;
	public static final int STATUS_COLLAPSED = 2;

	private int mTouchSlop;

	// 分别记录上次滑动的坐标
	private int mLastX = 0;
	private int mLastY = 0;

	// 分别记录上次滑动的坐标(onInterceptTouchEvent)
	private int mLastXIntercept = 0;
	private int mLastYIntercept = 0;

	// 用来控制滑动角度，仅当角度a满足如下条件才进行滑动：tan a = deltaX / deltaY > 2
	private static final int TAN = 2;

	private boolean mIsSticky = true;
	private boolean mInitDataSucceed = false;
	private boolean mDisallowInterceptTouchEventOnHeader = true;

	public StickyLayout(Context context) {
		super(context);
	}

	public StickyLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public StickyLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		if (hasWindowFocus && (mHeader == null || mContent == null)) {
			initData();
		}
	}

	private void initData() {
		int headerId = getResources().getIdentifier("sticky_header", "id",
				getContext().getPackageName());
		int contentId = getResources().getIdentifier("sticky_content", "id",
				getContext().getPackageName());
		if (headerId != 0 && contentId != 0) {
			mHeader = findViewById(headerId);
			mContent = findViewById(contentId);
			mOriginalHeaderHeight = mHeader.getMeasuredHeight();
			mHeaderHeight = mOriginalHeaderHeight;
			mTouchSlop = ViewConfiguration.get(getContext())
					.getScaledTouchSlop();
			if (mHeaderHeight > 0) {
				mInitDataSucceed = true;
			}
			if (DEBUG) {
				Tools.getLog(Tools.i, TAG, "mTouchSlop = " + mTouchSlop + "mHeaderHeight = "
						+ mHeaderHeight);
			}
		} else {
			throw new NoSuchElementException(
					"Did your view with id \"sticky_header\" or \"sticky_content\" exists?");
		}
	}

	public void setOnGiveUpTouchEventListener(OnGiveUpTouchEventListener l) {
		mGiveUpTouchEventListener = l;
	}

	public void setOnMoveOverListener(OnMoveOver onMoveOver) {
		this.onMoveOver = onMoveOver;
	}


	int startY;
	int curY;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		int intercepted = 0;
		int x = (int) event.getX();
		curY = (int) event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			mLastXIntercept = x;
			mLastYIntercept = curY;
			mLastX = x;
			mLastY = curY;
			intercepted = 0;
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			int deltaX = x - mLastXIntercept;
			int deltaY = curY - mLastYIntercept;
			if (mDisallowInterceptTouchEventOnHeader
					&& curY <= getHeaderHeight()) {
				intercepted = 0;
			} else if (Math.abs(deltaY) <= Math.abs(deltaX)) {
				intercepted = 0;
			} else if (mStatus == STATUS_EXPANDED && deltaY <= -mTouchSlop) {
				intercepted = 1;
			} else if (mGiveUpTouchEventListener != null) {
				if (mGiveUpTouchEventListener.giveUpTouchEvent(event)
						&& deltaY >= mTouchSlop) {
					intercepted = 1;
				}
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			intercepted = 0;
			mLastXIntercept = mLastYIntercept = 0;
			break;
		}
		default:
			break;
		}

		if (DEBUG) {
			Tools.getLog(Tools.i, TAG, "intercepted=" + intercepted);
		}
		return intercepted != 0 && mIsSticky;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mIsSticky) {
			return true;
		}
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {

			break;
		}
		case MotionEvent.ACTION_MOVE: {
			int deltaX = x - mLastX;
			int deltaY = y - mLastY;
			if (DEBUG) {
				Tools.getLog(Tools.i, TAG, "mHeaderHeight=" + mHeaderHeight + "  deltaY="
						+ deltaY + "  mlastY=" + mLastY);
			}
//			if (mStatus != STATUS_EXPANDED) {
//				Message msg = new Message();
//				msg.arg1 = STATUS_EXPANDED;
//				h.sendMessage(msg);
//			}
			mHeaderHeight += deltaY;
			setHeaderHeight(mHeaderHeight, true);
			
			break;
		}
		case MotionEvent.ACTION_UP: {
			// 这里做了下判断，当松开手的时候，会自动向两边滑动，具体向哪边滑，要看当前所处的位置

			int destHeight = 0;
			if (curY - mLastY > 0) {
				if (mHeaderHeight <= mOriginalHeaderHeight * 0.8) {
					mStatus = STATUS_COLLAPSED;
					destHeight = 0;
				} else {
					mStatus = STATUS_EXPANDED;
					destHeight = mOriginalHeaderHeight;
				}
			} else {
				if (mHeaderHeight >= mOriginalHeaderHeight * 0.2) {
					mStatus = STATUS_EXPANDED;
					destHeight = mOriginalHeaderHeight;
				} else {
					mStatus = STATUS_COLLAPSED;
					destHeight = 0;
				}
			}
			this.smoothSetHeaderHeight(mHeaderHeight, destHeight, 500);
			break;
		}
		default:
			break;
		}
		mLastX = x;
		mLastY = y;
		return true;
	}

	public void goTab() {
		int destHeight = 0;
		mStatus = STATUS_EXPANDED;
		this.smoothSetHeaderHeight(mHeaderHeight, destHeight, 500);
	}

	public void goHome() {
		int destHeight = 0;
		mStatus = STATUS_COLLAPSED;
		destHeight = mOriginalHeaderHeight;
		this.smoothSetHeaderHeight(mHeaderHeight, destHeight, 800);
	}

	public void smoothSetHeaderHeight(final int from, final int to,
			long duration) {
		smoothSetHeaderHeight(from, to, duration, false);
	}

	public void smoothSetHeaderHeight(final int from, final int to,
			long duration, final boolean modifyOriginalHeaderHeight) {
		final int frameCount = (int) (duration / 30f) + 1;
		final float partation = (to - from) / (float) frameCount;
		new Thread("Thread#smoothSetHeaderHeight") {

			@Override
			public void run() {
				
				for (int i = 0; i < frameCount; i++) {
					final int height;
					if (i == frameCount - 1) {
						
						height = to;
						Message msg = new Message();
						msg.arg1 = mStatus;
						h.sendMessage(msg);
					} else {
						height = (int) (from + partation * i);
					}
					post(new Runnable() {
						public void run() {
							setHeaderHeight(height, false);
						}
					});
					try {
						sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				if (modifyOriginalHeaderHeight) {
					setOriginalHeaderHeight(to);
				}
			};

		}.start();
	}

	private Handler h = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 != 0) {
				if (onMoveOver != null) {
					onMoveOver.onMoveOver(msg.arg1);
				}
			}
		}
	};

	public void setOriginalHeaderHeight(int originalHeaderHeight) {
		mOriginalHeaderHeight = originalHeaderHeight;
	}

	public void setmHeaderHeight(int height, boolean modifyOriginalHeaderHeight) {
		if (modifyOriginalHeaderHeight) {
			setOriginalHeaderHeight(height);
		}
		setHeaderHeight(height, false);
	}

	int minHeadHeight;

	public void setMinHeadHeight(int minHeadHeight) {
		this.minHeadHeight = minHeadHeight;
	}

	public void setHeaderHeight(int height, boolean isTouch) {
		if (!mInitDataSucceed) {
			initData();
		}
		
		if (DEBUG) {
			Tools.getLog(Tools.i, TAG, "setHeaderHeight height=" + height);
		}
		if (height <= minHeadHeight) {
			height = minHeadHeight;
		} else if (height > mOriginalHeaderHeight) {
			height = mOriginalHeaderHeight;
		}

		if (height == minHeadHeight) {
			mStatus = STATUS_COLLAPSED;
		} else {
			mStatus = STATUS_EXPANDED;
		}
		
		if(onMoveOver != null){
			onMoveOver.onMoving(height, isTouch);
		}

		if (mHeader != null && mHeader.getLayoutParams() != null) {
			mHeader.getLayoutParams().height = height;
			mHeader.requestLayout();
			mHeaderHeight = height;
		} else {
			if (DEBUG) {
				Log.e(TAG, "null LayoutParams when setHeaderHeight");
			}
		}
	}

	public int getHeaderHeight() {
		return mHeaderHeight;
	}

	public void setSticky(boolean isSticky) {
		mIsSticky = isSticky;
	}

	public void requestDisallowInterceptTouchEventOnHeader(
			boolean disallowIntercept) {
		mDisallowInterceptTouchEventOnHeader = disallowIntercept;
	}

}