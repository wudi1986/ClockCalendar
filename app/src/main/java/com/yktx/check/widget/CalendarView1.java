package com.yktx.check.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.yktx.check.ClockMainActivity;
import com.yktx.check.R;
import com.yktx.check.bean.CustomDate;
import com.yktx.check.bean.PerformanceBean;
import com.yktx.check.util.DateUtil;
import com.yktx.check.util.Tools;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarView1 extends View {

	private static final String TAG = "CalendarView";
	Context mContext;
	/**
	 * 两种模式 （月份和星期）
	 */
	public static final int MONTH_STYLE = 0;
	public static final int WEEK_STYLE = 1;

	private static final int TOTAL_COL = 7;
	private static final int TOTAL_ROW = 6;

	/**
	 * 实际这个月份有几行
	 */
	private int curRow;

	private Paint mCirclePaint;
	private Paint mTextPaint;
	private int mViewWidth;
	private int mViewHight;
	private int mCellSpace;
	private Row rows[] = new Row[TOTAL_ROW];
	private static CustomDate mShowDate;// 自定义的日期 包括year month day
	public static int style = MONTH_STYLE;
	private static final int WEEK = 7;
	private CallBack mCallBack;// 回调
	private int touchSlop;
	private boolean callBackCellSpace;

	public interface CallBack {

		void clickDate(CustomDate date, int row);// 回调点击的日期

		void onMesureCellHeight(int cellSpace, int curRow);// 回调cell的高度确定slidingDrawer高度

		void changeDate(CustomDate date, int row);// 回调滑动viewPager改变的日期

		void startVibrator();
	}

	public CalendarView1(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init(context);

	}

	public CalendarView1(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

	}

	public CalendarView1(Context context) {
		super(context);
		init(context);
	}

	public CalendarView1(Context context, int style, CallBack mCallBack) {
		super(context);

		CalendarView1.style = style;
		this.mCallBack = mCallBack;
		init(context);
	}

	ArrayList<PerformanceBean> performanceBeans;

	public void setDateColor(ArrayList<PerformanceBean> performanceBeans) {
		this.performanceBeans = performanceBeans;
		this.invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (int i = 0; i < TOTAL_ROW; i++) {
			if (rows[i] != null)
				rows[i].drawCells(canvas);
		}
	}

	private void init(Context context) {

		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCirclePaint.setStyle(Paint.Style.FILL);
		mCirclePaint.setColor(getResources().getColor(R.color.white));
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		initDate();

	}

	private void initDate() {
		if (style == MONTH_STYLE) {
			mShowDate = new CustomDate();
		} else if (style == WEEK_STYLE) {
			mShowDate = DateUtil.getNextSunday(null);
		}
		fillDate(null);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mViewWidth = w;
		mViewHight = h;
		mCellSpace = Math.min(mViewHight / TOTAL_ROW, mViewWidth / TOTAL_COL);
		if (!callBackCellSpace) {
			mCallBack.onMesureCellHeight(mCellSpace, curRow);
			callBackCellSpace = true;
		}
		mTextPaint.setTextSize(mCellSpace / 4);
	}

	public static Cell mClickCell;
	private float mDownX;
	private float mDownY;

	/*
	 * 
	 * 触摸事件为了确定点击的位置日期
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownX = event.getX();
			mDownY = event.getY(); 
			break;
		case MotionEvent.ACTION_UP:
			float disX = event.getX() - mDownX;
			float disY = event.getY() - mDownY;
			if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) {
				int col = (int) (mDownX / mCellSpace);
				int row = (int) (mDownY / mCellSpace);
				measureClickCell(col, row);
			}
			break;
		}
		return true;
	}

	private void measureClickCell(int col, int row) {
		if (col >= TOTAL_COL || row >= TOTAL_ROW)
			return;

		if (System.currentTimeMillis() < CustomDate
				.DateToUnixTime(rows[row].cells[col].date)) {
			if (mCallBack != null) {
				mCallBack.startVibrator();
			}
			return;
		}

		if (rows[row].cells[col].state == State.PAST_MONTH_DAY) {
			leftSilde();
			return;
		}
		if (rows[row].cells[col].state == State.NEXT_MONTH_DAY) {
			rightSilde();
			return;
		}

		if (mClickCell != null
				&& mClickCell.date.toString().equals(
						rows[mClickCell.j].cells[mClickCell.i].date.toString())) {
			rows[mClickCell.j].cells[mClickCell.i] = mClickCell;

			rows[mClickCell.j].cells[mClickCell.i].isClick = false;
		}
		if (rows[row] != null) {
			mClickCell = new Cell(rows[row].cells[col].date,
					rows[row].cells[col].state, rows[row].cells[col].i,
					rows[row].cells[col].j, true);
			rows[row].cells[col].isClick = true;
			CustomDate date = rows[row].cells[col].date;
			date.week = col;
			Tools.getLog(Tools.i, "aaa", "style ========== " + style);
			if (style == MONTH_STYLE) {
				mCallBack.clickDate(date, row);
				curRow = row;
			} else if (style == WEEK_STYLE) {
				mCallBack.clickDate(date, DateUtil.getWeekofMonth(date));
			}
			invalidate();
		}
	}

	// 组
	class Row {
		public int j;

		Row(int j) {
			this.j = j;
		}

		public Cell[] cells = new Cell[TOTAL_COL];

		public void drawCells(Canvas canvas) {
			for (int i = 0; i < cells.length; i++) {
				if (cells[i] != null)
					cells[i].drawSelf(canvas);
			}

		}
	}

	// 单元格
	class Cell {
		public CustomDate date;
		public State state;
		public int i;
		public int j;
		public boolean isClick;

		public Cell(CustomDate date, State state, int i, int j, boolean isClick) {
			super();
			this.date = date;
			this.state = state;
			this.i = i;
			this.j = j;
			this.isClick = isClick;
		}

		// 绘制一个单元格 如果颜色需要自定义可以修改
		public void drawSelf(Canvas canvas) {
			switch (state) {
			case CURRENT_MONTH_DAY:
			case TODAY:
				mTextPaint.setColor(Color.parseColor("#ffffff"));
				Calendar cl = Calendar.getInstance();
				boolean isSat, isSun;
				cl.set(date.getYear(), date.getMonth() - 1,
						date.getDay());
				isSat = cl.get(Calendar.DAY_OF_WEEK) == 7 ? true
						: false;
				isSun = cl.get(Calendar.DAY_OF_WEEK) == 1 ? true
						: false;
				if (ClockMainActivity.curMap != null) {

					if (ClockMainActivity.curMap.get(date.getDate()) != null) {
					
						if (ClockMainActivity.curMap.get(date.getDate())
								.equals("2")) {//打完了所有的卡
							mTextPaint.setTypeface(Typeface.DEFAULT);
							mTextPaint.setColor(getResources().getColor(
									R.color.white));
							mCirclePaint.setColor(getResources().getColor(
									R.color.meibao_color_1));
							Rect rect = null;
							if(isSat){
								rect = new Rect((int) (mCellSpace * (i)+0.5),
									(int) ((j) * mCellSpace+0.5), (int) (mCellSpace
											* (i) + mCellSpace), (int) (j
											* mCellSpace + mCellSpace-0.5));
							} else if(isSun){
								rect = new Rect((int) (mCellSpace * (i)),
										(int) ((j) * mCellSpace+0.5), (int) (mCellSpace
												* (i) + mCellSpace+0.5), (int) (j
												* mCellSpace + mCellSpace-0.5));
							} else {
								rect = new Rect((int) (mCellSpace * (i)+0.5),
										(int) ((j) * mCellSpace+0.5), (int) (mCellSpace
												* (i) + mCellSpace-0.5), (int) (j
												* mCellSpace + mCellSpace-0.5));
							}
							canvas.drawRect(rect, mCirclePaint);
							
							if (isClick) {//click 的某一天 默认是今天（打全的状态）
								mTextPaint.setColor(getResources().getColor(
										R.color.white));
								mCirclePaint.setColor(getResources().getColor(
										R.color.meibao_color_4));
								canvas.drawRect(rect, mCirclePaint);
								
							}
							
						} else if (ClockMainActivity.curMap.get(date.getDate())
								.equals("1")) {//打了一部分的卡
							
							mTextPaint.setTypeface(Typeface.DEFAULT);
							mTextPaint.setColor(getResources().getColor(
									R.color.white));
							mCirclePaint.setColor(getResources().getColor(
									R.color.meibao_color_2));
							Rect rect = null;
							if(isSat){
								rect = new Rect((int) (mCellSpace * (i)+0.5),
									(int) ((j) * mCellSpace+0.5), (int) (mCellSpace
											* (i) + mCellSpace), (int) (j
											* mCellSpace + mCellSpace-0.5));
							} else if(isSun){
								rect = new Rect((int) (mCellSpace * (i)),
										(int) ((j) * mCellSpace+0.5), (int) (mCellSpace
												* (i) + mCellSpace+0.5), (int) (j
												* mCellSpace + mCellSpace-0.5));
							} else {
								rect = new Rect((int) (mCellSpace * (i)+0.5),
										(int) ((j) * mCellSpace+0.5), (int) (mCellSpace
												* (i) + mCellSpace-0.5), (int) (j
												* mCellSpace + mCellSpace-0.5));
							}
							canvas.drawRect(rect, mCirclePaint);


							if (isClick) {//click 的某一天 默认是今天（没有打全的状态）
								mTextPaint.setColor(getResources().getColor(
										R.color.white));
								mCirclePaint.setColor(getResources().getColor(
										R.color.meibao_color_4));
								canvas.drawRect(rect, mCirclePaint);
								
							}

						} else {//今天以前的没有打过卡的时间

							mTextPaint.setTypeface(Typeface.DEFAULT);
							mTextPaint.setColor(getResources().getColor(
									R.color.meibao_color_11));
							mCirclePaint.setColor(getResources().getColor(
									R.color.white));
							// canvas.drawCircle((float) (mCellSpace * (i +
							// 0.5)),
							// (float) ((j + 0.5) * mCellSpace), mCellSpace / 2,
							// mCirclePaint);
							Rect rect = null;
							if(isSat){
								rect = new Rect((int) (mCellSpace * (i)+0.5),
									(int) ((j) * mCellSpace+0.5), (int) (mCellSpace
											* (i) + mCellSpace), (int) (j
											* mCellSpace + mCellSpace-0.5));
							} else if(isSun){
								rect = new Rect((int) (mCellSpace * (i)),
										(int) ((j) * mCellSpace+0.5), (int) (mCellSpace
												* (i) + mCellSpace+0.5), (int) (j
												* mCellSpace + mCellSpace-0.5));
							} else {
								rect = new Rect((int) (mCellSpace * (i)+0.5),
										(int) ((j) * mCellSpace+0.5), (int) (mCellSpace
												* (i) + mCellSpace-0.5), (int) (j
												* mCellSpace + mCellSpace-0.5));
							}
							canvas.drawRect(rect, mCirclePaint);
							if (isClick) {
								// canvas.drawBitmap(
								// bmp,
								// (int) (mCellSpace * (i + 0.5)
								// - bmp.getWidth() + mCellSpace / 2),
								// (int) ((j + 0.5) * mCellSpace - mCellSpace /
								// 2),
								// mCirclePaint);


								mTextPaint.setColor(getResources().getColor(
										R.color.white));
								mCirclePaint.setColor(getResources().getColor(
										R.color.meibao_color_4));
								Rect rect2 = null;
								if(isSat){
									rect2 = new Rect((int) (mCellSpace * (i)+0.5),
										(int) ((j) * mCellSpace+0.5), (int) (mCellSpace
												* (i) + mCellSpace), (int) (j
												* mCellSpace + mCellSpace-0.5));
								} else if(isSun){
									rect2 = new Rect((int) (mCellSpace * (i)),
											(int) ((j) * mCellSpace+0.5), (int) (mCellSpace
													* (i) + mCellSpace+0.5), (int) (j
													* mCellSpace + mCellSpace-0.5));
								} else {
									rect2 = new Rect((int) (mCellSpace * (i)+0.5),
											(int) ((j) * mCellSpace+0.5), (int) (mCellSpace
													* (i) + mCellSpace-0.5), (int) (j
													* mCellSpace + mCellSpace-0.5));
								}
								 canvas.drawRect(rect2, mCirclePaint);
							}
						}
					} else {//今天以后的没有打过卡的时间
						mTextPaint.setTypeface(Typeface.DEFAULT);
						mTextPaint.setColor(getResources().getColor(
								R.color.meibao_color_11));
						mCirclePaint.setColor(getResources().getColor(
								R.color.white));
						// canvas.drawCircle((float) (mCellSpace * (i +
						// 0.5)),
						// (float) ((j + 0.5) * mCellSpace), mCellSpace / 2,
						// mCirclePaint);
						Rect rect = null;
						if(isSat){
							rect = new Rect((int) (mCellSpace * (i)+0.5),
								(int) ((j) * mCellSpace+0.5), (int) (mCellSpace
										* (i) + mCellSpace), (int) (j
										* mCellSpace + mCellSpace-0.5));
						} else if(isSun){
							rect = new Rect((int) (mCellSpace * (i)),
									(int) ((j) * mCellSpace+0.5), (int) (mCellSpace
											* (i) + mCellSpace+0.5), (int) (j
											* mCellSpace + mCellSpace-0.5));
						} else {
							rect = new Rect((int) (mCellSpace * (i)+0.5),
									(int) ((j) * mCellSpace+0.5), (int) (mCellSpace
											* (i) + mCellSpace-0.5), (int) (j
											* mCellSpace + mCellSpace-0.5));
						}
						canvas.drawRect(rect, mCirclePaint);
						if (isClick) {

							mTextPaint.setColor(getResources().getColor(
									R.color.white));
							mCirclePaint.setColor(getResources().getColor(
									R.color.white));
							Rect rect2 = null;
							if(isSat){
								rect2 = new Rect((int) (mCellSpace * (i)+0.5),
									(int) ((j) * mCellSpace+0.5), (int) (mCellSpace
											* (i) + mCellSpace), (int) (j
											* mCellSpace + mCellSpace-0.5));
							} else if(isSun){
								rect2 = new Rect((int) (mCellSpace * (i)),
										(int) ((j) * mCellSpace+0.5), (int) (mCellSpace
												* (i) + mCellSpace+0.5), (int) (j
												* mCellSpace + mCellSpace-0.5));
							} else {
								rect2 = new Rect((int) (mCellSpace * (i)+0.5),
										(int) ((j) * mCellSpace+0.5), (int) (mCellSpace
												* (i) + mCellSpace-0.5), (int) (j
												* mCellSpace + mCellSpace-0.5));
							}
							 canvas.drawRect(rect2, mCirclePaint);
						}
					}
				}

				break;
			// case NEXT_MONTH_DAY: {
			// mTextPaint.setColor(getResources().getColor(
			// R.color.meibao_color_13));
			// mCirclePaint.setColor(Color.parseColor("#ffffff"));
			// Rect rect = new Rect(
			// (int) (mCellSpace * (i + 0.5) - mCellSpace / 2),
			// (int) ((j + 0.5) * mCellSpace - mCellSpace / 2),
			// (int) (mCellSpace * (i + 0.5) + mCellSpace / 2 - 0.5),
			// (int) ((j + 0.5) * mCellSpace + mCellSpace / 2 - 0.5));
			// canvas.drawRect(rect, mCirclePaint);
			// }
			// break;
			case NEXT_MONTH_DAY:
			case PAST_MONTH_DAY: {//过去的月的日期
				mTextPaint.setColor(getResources().getColor(R.color.white));
				mCirclePaint.setColor(getResources().getColor(
						R.color.white));
				Rect rect = new Rect((int) (mCellSpace * (i)),
						(int) ((j) * mCellSpace),
						(int) (mCellSpace * (i) + mCellSpace), (int) ((j)
								* mCellSpace + mCellSpace));
				canvas.drawRect(rect, mCirclePaint);
			}
				break;

			// case TODAY:
			//
			// mTextPaint.setColor(Color.parseColor("#F24949"));
			//
			// break;
			// case CLICK_DAY:
			//
			// mTextPaint.setColor(Color.parseColor("#ffffff"));
			// mCirclePaint.setColor(getResources().getColor(
			// R.color.meibao_color_3));
			// // canvas.drawCircle((float) (mCellSpace * (i + 0.5)),
			// // (float) ((j + 0.5) * mCellSpace), mCellSpace / 2,
			// // mCirclePaint);
			// Rect rect = new Rect(
			// (int) (mCellSpace * (i + 0.5) - mCellSpace / 2),
			// (int) ((j + 0.5) * mCellSpace - mCellSpace / 2),
			// (int) (mCellSpace * (i + 0.5) + mCellSpace / 2 - 0.5),
			// (int) ((j + 0.5) * mCellSpace + mCellSpace / 2 - 0.5));
			// canvas.drawRect(rect, mCirclePaint);
			// break;
			}
			// 绘制文字

			if (state == State.TODAY) {
				String content = "今天";

				mTextPaint.setTextSize((float) (mCellSpace / 4));

				mTextPaint.setTypeface(Typeface.DEFAULT);
				canvas.drawText(content,
						(float) ((i + 0.5) * mCellSpace - mTextPaint
								.measureText(content) / 2), (float) ((j + 0.75)
								* mCellSpace - mTextPaint.measureText(content,
								0, 1) / 2-5), mTextPaint);
			} else {
				mTextPaint.setTextSize(mCellSpace / 4);
				String content = date.day + "";
				canvas.drawText(content,
						(float) ((i + 0.5) * mCellSpace - mTextPaint
								.measureText(content) / 2), (float) ((j + 0.72)
								* mCellSpace - mTextPaint.measureText(content,
								0, 1) / 2-5), mTextPaint);
			}
		}
	}

	private boolean isHasDate(String[] str, String date) {
		for (int i = 0; i < str.length; i++) {
			if (str[i].equals(date)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @author huang cell的state 当前月日期，过去的月的日期，下个月的日期，今天，点击的日期
	 * 
	 */
	enum State {
		CURRENT_MONTH_DAY, PAST_MONTH_DAY, NEXT_MONTH_DAY, TODAY, CLICK_DAY;
	}

	/**
	 * 填充日期的数据
	 */
	private void fillDate(CustomDate curDate) {
		if (curDate != null)
			mClickCell.date = curDate;
		else
			mClickCell = null;

		if (style == MONTH_STYLE) {
			fillMonthDate(curDate);
		} else if (style == WEEK_STYLE) {
			fillWeekDate(curDate);
		}
		// if (mClickCell != null) {
		// mCallBack.changeDate(mClickCell.date, curRow);
		// } else {
		mCallBack.changeDate(mShowDate, curRow);
		// }
	}

	/**
	 * 填充星期模式下的数据 默认通过当前日期得到所在星期天的日期，然后依次填充日期
	 */
	private void fillWeekDate(CustomDate curDate) {
		int lastMonthDays = DateUtil.getMonthDays(mShowDate.year,
				mShowDate.month - 1);
		// ClockMainActivity.isTodayCanSee = false;

		rows[0] = new Row(0);
		int day = mShowDate.day;
		int month = mShowDate.month;
		CustomDate curShowDate = new CustomDate(mShowDate.year, month, day);

		for (int i = TOTAL_COL - 1; i >= 0; i--) {
			day -= 1;
			if (day < 1) {
				day = lastMonthDays;
				curShowDate.month = month - 1;
			} else if (day > lastMonthDays) {
				curShowDate.month = month - 1;
			}

			CustomDate date = CustomDate.modifiDayForObject(curShowDate, day);

			if (DateUtil.isToday(date)) {

				date.week = i;

				if (mClickCell == null || DateUtil.isToday(mClickCell.date)) {
					rows[0].cells[i] = new Cell(date, State.TODAY, i, 0, true);
					mClickCell = rows[0].cells[i];
					ClockMainActivity.isTodayCanSee = true;
					mCallBack.clickDate(date, DateUtil.getWeekofMonth(date));
				} else {
					rows[0].cells[i] = new Cell(date, State.TODAY, i, 0, false);
				}
				continue;
			}
			// if (System.currentTimeMillis() < curDate.DateToUnixTime(date)) {
			// rows[0].cells[i] = new Cell(date, State.NEXT_MONTH_DAY, i, 0,
			// true);
			// } else {
			if (curDate != null && date.getDate().equals(curDate.getDate())) {
				rows[0].cells[i] = new Cell(date, State.CURRENT_MONTH_DAY, i,
						0, true);
				mClickCell = rows[0].cells[i];
			} else {
				rows[0].cells[i] = new Cell(date, State.CURRENT_MONTH_DAY, i,
						0, false);
			}
			// }

		}
	}

	/**
	 * 填充月份模式下数据 通过getWeekDayFromDate得到一个月第一天是星期几就可以算出所有的日期的位置 然后依次填充 这里最好重构一下
	 */
	private void fillMonthDate(CustomDate curDate) {
		curRow = 0;

		// ClockMainActivity.isTodayCanSee = false;
		boolean isThisMonth = true; // 判读是否是下月第一天
		int monthDay = DateUtil.getCurrentMonthDay();

		int lastMonthDays = DateUtil.getMonthDays(mShowDate.year,
				mShowDate.month - 1);
		int currentMonthDays = DateUtil.getMonthDays(mShowDate.year,
				mShowDate.month);
		int firstDayWeek = DateUtil.getWeekDayFromDate(mShowDate.year,
				mShowDate.month);
		boolean isCurrentMonth = false;
		if (DateUtil.isCurrentMonth(mShowDate)) {
			isCurrentMonth = true;
		}
		int day = 0;
		for (int j = 0; j < TOTAL_ROW; j++) {
			rows[j] = new Row(j);
			for (int i = 0; i < TOTAL_COL; i++) {
				int postion = i + j * TOTAL_COL;
				if (postion >= firstDayWeek
						&& postion < firstDayWeek + currentMonthDays) {
					// 本月的
					day++;
					CustomDate date = CustomDate.modifiDayForObject(mShowDate,
							day);
					if (DateUtil.isToday(date)) {

						// if (curDate == null
						// || curDate.getDate().equals(date.getDay())) {
						// ClockMainActivity.isTodayCanSee = true;
						// }

						if (mClickCell == null
								|| DateUtil.isToday(mClickCell.date)) {
							rows[j].cells[i] = new Cell(date, State.TODAY, i,
									j, true);
							mClickCell = new Cell(date, State.TODAY, i, j, true);
							mCallBack.clickDate(date, j);
							ClockMainActivity.isTodayCanSee = true;
						} else {
							rows[j].cells[i] = new Cell(date, State.TODAY, i,
									j, false);
						}
						date.week = i;

						continue;
					}
					// if (System.currentTimeMillis() < CustomDate
					// .DateToUnixTime(date)) {
					// rows[j].cells[i] = new Cell(
					// CustomDate.modifiDayForObject(mShowDate, day),
					// State.NEXT_MONTH_DAY, i, j, false);
					// } else {

					if (curDate != null
							&& date.getDate().equals(curDate.getDate())) {
						rows[j].cells[i] = new Cell(
								CustomDate.modifiDayForObject(mShowDate, day),
								State.CURRENT_MONTH_DAY, i, j, true);
						mClickCell = rows[j].cells[i];

					} else {
						rows[j].cells[i] = new Cell(
								CustomDate.modifiDayForObject(mShowDate, day),
								State.CURRENT_MONTH_DAY, i, j, false);
					}
					// }

				} else if (postion < firstDayWeek) {
					// 上个月的
					rows[j].cells[i] = new Cell(new CustomDate(mShowDate.year,
							mShowDate.month - 1, lastMonthDays
									- (firstDayWeek - postion - 1)),
							State.PAST_MONTH_DAY, i, j, false);

				} else if (postion >= firstDayWeek + currentMonthDays) {

					// 下个月的
					if (isThisMonth) {
						if (i == 0) {
							curRow = j;
						} else {
							curRow = j + 1;
						}
						isThisMonth = false;
					}
					rows[j].cells[i] = new Cell((new CustomDate(mShowDate.year,
							mShowDate.month + 1, postion - firstDayWeek
									- currentMonthDays + 1)),
							State.NEXT_MONTH_DAY, i, j, false);
				}
			}
		}
	}

	public void update(CustomDate curDate) {
		fillDate(curDate);
		invalidate();
	}

	public void backToday() {
		initDate();
		invalidate();
	}

	// 切换style
	public void switchStyle(int style, CustomDate curDate) {
		CalendarView1.style = style;
		if (style == MONTH_STYLE) {
			if (curDate != null) {
				mShowDate = curDate;
			} else {
				mShowDate = new CustomDate();
			}

			update(curDate);
		} else if (style == WEEK_STYLE) {
			// if(curDate == null){
			mShowDate = DateUtil.getNextSunday(curDate);
			// } else {
			// mShowDate = curDate;
			// }
			update(curDate);
		}
	}

	// 向右滑动
	public void rightSilde() {
		if (style == MONTH_STYLE) {

			if (mShowDate.month == 12) {
				mShowDate.month = 1;
				mShowDate.year += 1;
			} else {
				mShowDate.month += 1;
			}

		} else if (style == WEEK_STYLE) {
			Tools.getLog(Tools.i, "aaa", "rightSilde mShowDate =========== "
					+ mShowDate.toString());
			int currentMonthDays = DateUtil.getMonthDays(mShowDate.year,
					mShowDate.month);
			if (mShowDate.day + WEEK > currentMonthDays) {
				if (mShowDate.month == 12) {
					mShowDate.month = 1;
					mShowDate.year += 1;
				} else {
					mShowDate.month += 1;
				}
				mShowDate.day = WEEK - currentMonthDays + mShowDate.day;
			} else {
				mShowDate.day += WEEK;

			}
		}
		if (mClickCell != null)
			update(mClickCell.date);
		else
			update(null);
	}

	public void leftSilde() {

		if (style == MONTH_STYLE) {
			if (mShowDate.month == 1) {
				mShowDate.month = 12;
				mShowDate.year -= 1;
			} else {
				mShowDate.month -= 1;
			}

		} else if (style == WEEK_STYLE) {
			int lastMonthDays = DateUtil.getMonthDays(mShowDate.year,
					mShowDate.month);
			if (mShowDate.day - WEEK < 1) {

				if (mShowDate.month == 1) {
					mShowDate.month = 12;
					mShowDate.year -= 1;
				} else {
					mShowDate.month -= 1;
				}
				mShowDate.day = lastMonthDays - WEEK + mShowDate.day;

			} else {
				mShowDate.day -= WEEK;
			}
		}
		if (mClickCell != null) {
			update(mClickCell.date);

		} else
			update(null);
	}
}
