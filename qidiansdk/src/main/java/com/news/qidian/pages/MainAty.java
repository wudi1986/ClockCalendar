package com.news.qidian.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.news.qidian.R;
import com.news.qidian.adapter.NewsFeedAdapter;
import com.news.qidian.common.BaseActivity;
import com.news.qidian.common.HttpConstant;
import com.news.qidian.database.ChannelItemDao;
import com.news.qidian.entity.ChannelItem;
import com.news.qidian.entity.NewsFeed;
import com.news.qidian.entity.UserVisitorEntity;
import com.news.qidian.net.volley.DetailOperateRequest;
import com.news.qidian.utils.DeviceInfoUtil;
import com.news.qidian.utils.Logger;
import com.news.qidian.utils.TextUtil;
import com.news.qidian.utils.ToastUtil;
import com.news.qidian.utils.manager.SharedPreManager;
import com.news.qidian.widget.FeedDislikePopupWindow;
import com.news.qidian.widget.channel.ChannelTabStrip;
import com.news.qidian.widget.tag.TagCloudLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

//import com.news.qidian.widget.tag.TagCloudLayout;
//import com.umeng.analytics.MobclickAgent;
//import com.umeng.update.UmengUpdateAgent;

/**
 * Created by fengjigang on 15/10/28.
 * 主界面
 */
public class MainAty extends BaseActivity implements View.OnClickListener, NewsFeedFgt.NewsSaveDataCallBack {

    public static final int REQUEST_CODE = 1001;
    public static final String ACTION_USER_LOGIN = "com.news.qidian.ACTION_USER_LOGIN";
    public static final String ACTION_USER_LOGOUT = "com.news.qidian.ACTION_USER_LOGOUT";
    public static final String KEY_INTENT_USER_URL = "key_intent_user_url";
    private ArrayList<ChannelItem> mUnSelChannelItems;

    private ChannelTabStrip mChannelTabStrip;
    private ViewPager mViewPager;
    private MyViewPagerAdapter mViewPagerAdapter;
    private ImageView mChannelExpand;
    private ChannelItemDao mChannelItemDao;
    private Handler mHandler = new Handler();
    private UserLoginReceiver mReceiver;
    private long mLastPressedBackKeyTime;
    private ArrayList<ChannelItem> mSelChannelItems;//默认展示的频道
    private HashMap<String, ArrayList<NewsFeed>> mSaveData = new HashMap<>();
    //baidu Map
//    public LocationClient mLocationClient = null;
//    public BDLocationListener myListener = new MyLocationListener();
//    private SimpleDraweeView mUserCenter;
    private TextView mDetailLeftBack;

    /**
     * 自定义的PopWindow
     */
    FeedDislikePopupWindow dislikePopupWindow;


    @Override
    public void result(String channelId, ArrayList<NewsFeed> results) {
        mSaveData.put(channelId, results);
    }


    private class UserLoginReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            if (ACTION_USER_LOGIN.equals(intent.getAction())) {
//                String url = intent.getStringExtra(KEY_INTENT_USER_URL);
//                if (!TextUtil.isEmptyString(url)) {
//                    mUserCenter.setImageURI(Uri.parse(url));
//                }
//            } else if (ACTION_USER_LOGOUT.equals(intent.getAction())) {
//                mUserCenter.setImageURI(null);
//            }
        }
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.qd_aty_main);
    }


    @Override
    protected void initializeViews() {
//        MobclickAgent.onEvent(this,"bainews_user_assess_app");
        mChannelItemDao = new ChannelItemDao(this);
        mSelChannelItems = new ArrayList<>();
        mChannelTabStrip = (ChannelTabStrip) findViewById(R.id.mChannelTabStrip);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        mViewPager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);

        mDetailLeftBack = (TextView) findViewById(R.id.mDetailLeftBack);
        mDetailLeftBack.setOnClickListener(this);
        mViewPager.setOffscreenPageLimit(2);
        mChannelExpand = (ImageView) findViewById(R.id.mChannelExpand);
        mChannelExpand.setOnClickListener(this);
        mViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mChannelTabStrip.setViewPager(mViewPager);
//                mUserCenter.setImageURI(Uri.parse("http://wx.qlogo.cn/mmopen/PiajxSqBRaEIVrCBZPyFk7SpBj8OW2HA5IGjtic5f9bAtoIW2uDr8LxIRhTTmnYXfejlGvgsqcAoHgkBM0iaIx6WA/0"));
        dislikePopupWindow = (FeedDislikePopupWindow) findViewById(R.id.feedDislike_popupWindow);
        dislikePopupWindow.setVisibility(View.GONE);
        dislikePopupWindow.setItemClickListerer(new TagCloudLayout.TagItemClickListener() {
            Handler mHandler = new Handler();

            @Override
            public void itemClick(int position) {
                switch (position) {
                    case 0://不喜欢
//
//                        NewsFeedFgt newsFeedFgt= (NewsFeedFgt) mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
//                        newsFeedFgt.disLikeItem();
                    case 1://重复、旧闻
                    case 2://内容质量差
                    case 3://不喜欢
                        mNewsFeedAdapter.disLikeDeleteItem();
                        dislikePopupWindow.setVisibility(View.GONE);
                        ToastUtil.showReduceRecommendToast(MainAty.this);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                            }
                        }, 100);
                        break;
                }
            }
        });
        /**更新右下角用户登录图标*/
//        User user = SharedPreManager.getUser(this);
//        if (user != null) {
//            if (!TextUtil.isEmptyString(user.getUserIcon())) {
//                mUserCenter.setImageURI(Uri.parse(user.getUserIcon()));
//            }
//        }
        /**注册用户登录广播*/
        mReceiver = new UserLoginReceiver();
        IntentFilter filter = new IntentFilter(ACTION_USER_LOGIN);
        filter.addAction(ACTION_USER_LOGOUT);
        registerReceiver(mReceiver, filter);

        if (SharedPreManager.getVisitorUser(this) == null) {
            //未注册

            Log.i("aaa", "initializeViews:  ////  //未注册");
            userRegister();

        } else {
            // 登录
            userLogin();
            Log.i("aaa", "initializeViews:  //// 登录");
        }


    }

    /**
     * 开始顶部 progress 刷新动画
     */
    public void startTopRefresh() {
    }

    /**
     * 停止顶部 progress 刷新动画
     */
    public void stopTopRefresh() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    protected void loadData() {
        //添加umeng更新
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                UmengUpdateAgent.setUpdateAutoPopup(true);
//                UmengUpdateAgent.update(MainAty.this);
//            }
//        }, 2000);
    }

    private void userRegister() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject json = new JSONObject();
        try {
            json.put("utype", 2);
            json.put("platform", 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DetailOperateRequest userRegisterRequest = new DetailOperateRequest(Request.Method.POST, HttpConstant.URL_REGISTER_VISITOR,
                json.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("aaa", "onResponse: " + response);
                if (response.has("data")){
                    try {
                        String data = response.getString("data");
                        Gson gson = new Gson();
                        UserVisitorEntity userVisitorEntity = gson.fromJson(data, UserVisitorEntity.class);
                        SharedPreManager.saveVisitorUser(userVisitorEntity);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("aaa", "VolleyError: " + error);
            }
        });
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Content-Type", "application/json");
        userRegisterRequest.setRequestHeader(map);
        userRegisterRequest.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 1, 1.0f));
        requestQueue.add(userRegisterRequest);
    }

    private void userLogin() {

        UserVisitorEntity userVisitor = SharedPreManager.getVisitorUser(this);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Log.i("aaa", "userLogin: "+userVisitor.toString());
        JSONObject json = new JSONObject();
        try {
            json.put("uid", Integer.parseInt(userVisitor.getUid()));
            json.put("password", userVisitor.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DetailOperateRequest userLoginRequest = new DetailOperateRequest(Request.Method.POST,
                HttpConstant.URL_VISITOR_LOGIN, json.toString(), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i("aaa", "onResponse: userLoginRequest"+response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("aaa", "onErrorResponse: " + error);

            }
        });
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Content-Type", "application/json");
        userLoginRequest.setRequestHeader(map);
        userLoginRequest.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 1, 1.0f));
        requestQueue.add(userLoginRequest);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mChannelExpand) {

//        switch (v.getId()) {
////            case R.id.mTopSearch:
////                Intent topicSearch = new Intent(MainAty.this, TopicSearchAty.class);
////                startActivity(topicSearch);
////                break;
//            case R.id.mChannelExpand:
            Intent channelOperate = new Intent(MainAty.this, ChannelOperateAty.class);
//                Mobclic/kAgent.onEvent(this, "user_open_channel_edit_page");
            startActivityForResult(channelOperate, REQUEST_CODE);
        }
//                break;
//            case R.id.mUserCenter:
//                User user = SharedPreManager.getUser(this);
////                //FIXME debug
////                if (user == null){
////                    user = new User();
////                    user.setUserName("forward_one");
////                    user.setUserIcon("http://wx.qlogo.cn/mmopen/PiajxSqBRaEIVrCBZPyFk7SpBj8OW2HA5IGjtic5f9bAtoIW2uDr8LxIRhTTmnYXfejlGvgsqcAoHgkBM0iaIx6WA/0");
////                }
//                if (user == null) {
//                    Intent loginAty = new Intent(this, LoginAty.class);
//                    startActivity(loginAty);
//                } else {
//                    Intent userCenterAty = new Intent(this, UserCenterAty.class);
//                    startActivity(userCenterAty);
//                }
//                MobclickAgent.onEvent(this, "qidian_user_open_user_center");
//                break;
        else if (v.getId() == R.id.mDetailLeftBack) {
//            case R.id.mDetailLeftBack:
            MainAty.this.finish();
//                break;

        }
    }

    ChannelItem item1;
    ArrayList<ChannelItem> channelItems;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQUEST_CODE) {
            channelItems = (ArrayList<ChannelItem>) data.getSerializableExtra(ChannelOperateAty.KEY_USER_SELECT);
            int currPosition = mViewPager.getCurrentItem();
            item1 = mSelChannelItems.get(currPosition);
            int index = -1;
            for (int i = 0; i < channelItems.size(); i++) {
                ChannelItem item = channelItems.get(i);
                if (item1.getId().equals(item.getId())) {
                    index = i;
                }
            }
            if (index == -1) {
                Logger.e("jigang", "index = " + index);
                index = currPosition > channelItems.size() - 1 ? channelItems.size() - 1 : currPosition;
            }
            mViewPager.setCurrentItem(index);
            Fragment item = mViewPagerAdapter.getItem(index);
            if (item != null) {
                ((NewsFeedFgt) item).setNewsFeed(mSaveData.get(item1.getId()));
            }
            mViewPagerAdapter.setmChannelItems(channelItems);
            mViewPagerAdapter.notifyDataSetChanged();
            mChannelTabStrip.setViewPager(mViewPager);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (dislikePopupWindow.getVisibility() == View.VISIBLE) {//判断自定义的 popwindow 是否显示 如果现实按返回键关闭
//                dislikePopupWindow.setVisibility(View.GONE);
//                return true;
//            }
//            long pressedBackKeyTime = System.currentTimeMillis();
//            if ((pressedBackKeyTime - mLastPressedBackKeyTime) < 2000) {
//                finish();
//            } else {
//                if (DeviceInfoUtil.isFlyme()) {
//                    ToastUtil.toastShort(getString(R.string.press_back_again_exit));
//                } else {
//                    ToastUtil.showToastWithIcon(getString(R.string.press_back_again_exit), R.drawable.release_time_logo);// (this, getString(R.string.press_back_again_exit));
//                }
//                mLastPressedBackKeyTime = pressedBackKeyTime;
//                return true;
//            }
//        }

        return super.onKeyDown(keyCode, event);
    }

    public class MyViewPagerAdapter extends FragmentStatePagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
            mSelChannelItems = mChannelItemDao.queryForSelected();
            mUnSelChannelItems = mChannelItemDao.queryForNormal();

            //统计用户频道订阅/非订阅 频道数
            HashMap<String, String> unSubChannel = new HashMap<>();
            unSubChannel.put("unsubscribed_channels", TextUtil.List2String(mUnSelChannelItems));
//            MobclickAgent.onEventValue(MainAty.this, "user_unsubscribe_channels", unSubChannel, mUnSelChannelItems.size());

            HashMap<String, String> subChannel = new HashMap<>();
            subChannel.put("subscribed_channels", TextUtil.List2String(mSelChannelItems));
//            MobclickAgent.onEventValue(MainAty.this, "user_subscribed_channels", subChannel, mSelChannelItems.size());

        }

        public void setmChannelItems(ArrayList<ChannelItem> pChannelItems) {
            mSelChannelItems = pChannelItems;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mSelChannelItems.get(position).getName();
        }

        @Override
        public int getItemPosition(Object object) {
            int index = -1;
            if (channelItems != null && channelItems.size() > 0) {
                for (int i = 0; i < channelItems.size(); i++) {
                    ChannelItem item = channelItems.get(i);
                    if (item1.getId().equals(item.getId())) {
                        index = i;
                    }
                }
            }
            if (index == -1) {
                return POSITION_NONE;
            } else {
                return POSITION_UNCHANGED;
            }
        }

        @Override
        public int getCount() {
            return mSelChannelItems.size();
        }

        @Override
        public Fragment getItem(int position) {
            String channelId = mSelChannelItems.get(position).getId();
            NewsFeedFgt feedFgt = NewsFeedFgt.newInstance(channelId);
            feedFgt.setNewsFeedFgtPopWindow(mNewsFeedFgtPopWindow);
            feedFgt.setNewsSaveDataCallBack(MainAty.this);
            return feedFgt;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            String channelId = mSelChannelItems.get(position).getId();
            NewsFeedFgt fgt = (NewsFeedFgt) super.instantiateItem(container, position);
            ArrayList<NewsFeed> newsFeeds = mSaveData.get(channelId);
            if (TextUtil.isListEmpty(newsFeeds)) {
//                fgt.refreshData();
            } else {
                fgt.setNewsFeed(newsFeeds);
            }
            return fgt;
        }

//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
////            super.destroyItem(container, position, object);
//
//        }

    }

    NewsFeedAdapter mNewsFeedAdapter;
    NewsFeedFgt.NewsFeedFgtPopWindow mNewsFeedFgtPopWindow = new NewsFeedFgt.NewsFeedFgtPopWindow() {
        @Override
        public void showPopWindow(int x, int y, String PubName, NewsFeedAdapter mAdapter) {
            mNewsFeedAdapter = mAdapter;
            dislikePopupWindow.setSourceList("来源：" + PubName);
            dislikePopupWindow.showView(x, y - DeviceInfoUtil.getStatusBarHeight(MainAty.this));

        }

    };

}
