package com.yizu.intelligentpiano.view;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.liuxiaozhu.lowrecyclerviews.adapter.ListViewAdapter;
import com.liuxiaozhu.lowrecyclerviews.adapter.viewholder.BaseViewHoloder;
import com.liuxiaozhu.lowrecyclerviews.callbacks.IPullLoading;
import com.liuxiaozhu.lowrecyclerviews.utils.LowRecyclerViewUtils;
import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.appliction.MyAppliction;
import com.yizu.intelligentpiano.bean.Category;
import com.yizu.intelligentpiano.bean.OneSong;
import com.yizu.intelligentpiano.bean.Songs;
import com.yizu.intelligentpiano.bean.UserInfo;
import com.yizu.intelligentpiano.bean.WebSocketBean;
import com.yizu.intelligentpiano.constens.Constents;
import com.yizu.intelligentpiano.constens.HttpUrls;
import com.yizu.intelligentpiano.constens.IGetSelectData;
import com.yizu.intelligentpiano.constens.IMusic;
import com.yizu.intelligentpiano.constens.INetStatus;
import com.yizu.intelligentpiano.constens.IOkHttpCallBack;
import com.yizu.intelligentpiano.constens.ITimeNot;
import com.yizu.intelligentpiano.dialog.TimeDialog;
import com.yizu.intelligentpiano.utils.MyLogUtils;
import com.yizu.intelligentpiano.utils.MyToast;
import com.yizu.intelligentpiano.utils.OkHttpUtils;
import com.yizu.intelligentpiano.utils.PreManger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 选择播放，演奏或者其他
 */
public class SelectActivity extends BaseActivity {
    private final static String TAG = "SelectActivity";
    private TextView mUserName, mSongName, mSongFraction;
    private ImageView mIcon;

    //是否是微信登陆，默认不是
    private boolean isWXLogin = false;
    //动态广播
    private MyBroadcastReceiver receiver;
    private boolean isShowDialog = true;

    //选择类型，水平 ,-1代表打分，0-6代表歌曲,
    private int type = -1;
    private int vertical = -1;

    //记录分页状态
    private Map<Integer, Integer> pagingMap = new HashMap<>();

    private List<RecyclerView> recyclerViewList = new ArrayList<>();
    private List<ListViewAdapter<Songs.DataBean.ListBean>> mAdapterList = new ArrayList<>();

    private HorizontalScrollView scrollView;
    //选择播放模式
    private TextView popText;
    private ImageView popLeftImg;
    private ImageView popRigetImg;
    private Songs.DataBean.ListBean popData;
    private RelativeLayout selectView;


    private String nickname = "";
    private String icon = "";
    private String music_title;
    private String music_auther;
    private String music_xml;
    private String music_id;
    private String music_updatatime;

    public static SelectActivity selectActivity;
    private LinearLayout layout;
    //是否刷新记录数据
    public boolean isUpdata = false;
    private TimeDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        selectActivity = this;
    }

    @Override
    protected void initView() {
        layout = findViewById(R.id.linearLayout);
        mUserName = (TextView) findViewById(R.id.user_name);
        mSongName = (TextView) findViewById(R.id.user_song);
        mSongFraction = (TextView) findViewById(R.id.user_songfunction);
        mIcon = (ImageView) findViewById(R.id.user_icon);
        recyclerViewList.add(0, (RecyclerView) findViewById(R.id.record));
        scrollView = findViewById(R.id.scrollView);
        selectView = findViewById(R.id.select_view);
        popText = findViewById(R.id.dialog_songname);
        popLeftImg = findViewById(R.id.dialog_satisfied);
        popRigetImg = findViewById(R.id.dialog_play);
        popLeftImg.setSelected(true);
    }

    @Override
    protected void setData() {
        setRegisterReceiver();
        if (getIntent() != null) {
            isWXLogin = getIntent().getBooleanExtra("isWXLogin", false);
            if (isWXLogin) {
                nickname = getIntent().getStringExtra("username");
                icon = getIntent().getStringExtra("pic");
                mUserName.setText(nickname);
                Glide.with(SelectActivity.this).load(icon).into(mIcon);
            }
        }
        //获取用户信息
//        getUserInfo();
        //打分
        setFraction();
        //设置视频view
        setVedioView();
        //获取歌曲分类信息
        getCategory();
    }

    private void setVedioView() {
        View view = LayoutInflater.from(this).inflate(R.layout.song, null);
        TextView textView = (TextView) view.findViewById(R.id.list_name);
        textView.setText("视频-练习指导");
        recyclerViewList.add(1, (RecyclerView) view.findViewById(R.id.list_recycler));
        setVedio();
        layout.addView(view);
    }

    /**
     * 获取分类信息
     */
    private void getCategory() {
        OkHttpUtils.getInstance().postMap(HttpUrls.GETCATEGORY, new HashMap<String, String>(), new IOkHttpCallBack() {
            @Override
            public void success(String result) {
                Category bean = OkHttpUtils.Json2Bean(result, Category.class);
                if (bean.getCode().equals("000")) {
                    setSongView(bean.getData());
                } else {
                    MyToast.ShowLong(bean.getMessage());
                }
            }
        });
    }

    /**
     * 设置HorizontalScrollView
     *
     * @param data
     */
    private void setSongView(List<Category.Song> data) {
        if (data == null) return;
        for (int i = 0; i < data.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.song, null);
            TextView textView = (TextView) view.findViewById(R.id.list_name);
            textView.setText(data.get(i).getTitle());
            MyLogUtils.e(TAG, data.get(i).getTitle());
            recyclerViewList.add(i + 2, (RecyclerView) view.findViewById(R.id.list_recycler));
            setSongs(i + 2, data.get(i).getId());
            layout.addView(view);
        }
    }


    /**
     * 设置视频-练习指导
     */
    private void setVedio() {
        pagingMap.put(1, 1);
        mAdapterList.add(1, new ListViewAdapter<Songs.DataBean.ListBean>(null, this, R.layout.item_song_item) {
            @Override
            public void setData(BaseViewHoloder holder, int position, Songs.DataBean.ListBean item) {
                if (1 == type && position == vertical) {
                    holder.getAbroadView().setSelected(true);
                } else {
                    holder.getAbroadView().setSelected(false);
                }
                if (position < 9) {
                    holder.getTextView(R.id.song_id).setText("0" + (position + 1));
                } else {
                    holder.getTextView(R.id.song_id).setText("" + (position + 1));
                }
                holder.getTextView(R.id.song_context).setText(item.getTitle() + "-" + item.getAuther());
                holder.getImageView(R.id.song_img).setImageResource(R.mipmap.vdio);
            }
        });
        mAdapterList.get(1).setPullToData(20, true, new IPullLoading() {
            @Override
            public void PullToLoading() {
                pagingMap.put(1, pagingMap.get(1) + 1);
                getVideo();
            }
        });
        new LowRecyclerViewUtils<Songs.DataBean.ListBean>(recyclerViewList.get(1), 0, mAdapterList.get(1)).addItemDecoration(1, R.color.gary);
        recyclerViewList.get(1).setAdapter(mAdapterList.get(1));
        getVideo();
    }

    /**
     * 儿童
     *
     * @param i
     * @param id
     */
    private void setSongs(final int i, final String id) {
        pagingMap.put(i, 1);
        mAdapterList.add(i, new ListViewAdapter<Songs.DataBean.ListBean>(null, this, R.layout.item_song_item) {
            @Override
            protected void setData(BaseViewHoloder holder, int position, Songs.DataBean.ListBean item) {
                if (type == i && position == vertical) {
                    holder.getAbroadView().setSelected(true);
                } else {
                    holder.getAbroadView().setSelected(false);
                }
                if (position < 9) {
                    holder.getTextView(R.id.song_id).setText("0" + (position + 1));
                } else {
                    holder.getTextView(R.id.song_id).setText("" + (position + 1));
                }
                holder.getTextView(R.id.song_context).setText(item.getTitle() + "-" + item.getAuther());

            }
        });
        mAdapterList.get(i).setPullToData(20, true, new IPullLoading() {
            @Override
            public void PullToLoading() {
                pagingMap.put(i, pagingMap.get(i) + 1);
                getAllList(id, mAdapterList.get(i), pagingMap.get(i));
            }
        });
        new LowRecyclerViewUtils<Songs.DataBean.ListBean>(recyclerViewList.get(i), 0, mAdapterList.get(i)).addItemDecoration(1, R.color.gary);
        recyclerViewList.get(i).setAdapter(mAdapterList.get(i));
        getAllList(id, mAdapterList.get(i), pagingMap.get(i));
    }

    /**
     * 注册广播
     */
    private void setRegisterReceiver() {
        receiver = new MyBroadcastReceiver();
        IntentFilter iFilter = new IntentFilter(Constents.ACTION);
        registerReceiver(receiver, iFilter);
    }

    /**
     * 获取用户信息
     */
//    private void getUserInfo() {
//        if (Constents.user_id.equals("")) {
//            return;
//        }
//        final Map<String, String> maps = new HashMap<>();
//        maps.put("user_id", Constents.user_id);
//        maps.put("device_id", PreManger.instance().getMacId());
//        OkHttpUtils.getInstance().postMap(HttpUrls.GETUSERINFO, maps, new IOkHttpCallBack() {
//            @Override
//            public void success(String result) {
//                UserInfo mUserInfo = OkHttpUtils.Json2Bean(result, UserInfo.class);
//                if (mUserInfo == null) return;
//                if (mUserInfo.getCode().equals("000")) {
//                    if (mUserInfo.getData().getLeftscore().length() < 6) {
//
//                    }
//
//                }
//            }
//        });
//
//    }

    @Override
    protected void setLinster() {
        OkHttpUtils.getInstance().getNetStatus(new INetStatus() {
            @Override
            public void isNoNet() {
                if (isWXLogin) {
                    MyToast.ShowLong("网络异常断开，请联网后登陆");
                    Intent intent = new Intent(Constents.ACTION);
                    intent.putExtra(Constents.KEY, Constents.LOGOUT_FINISH);
                    SelectActivity.this.sendBroadcast(intent);
                }
            }
        });
        OkHttpUtils.getInstance().getMusic(new IMusic() {
            @Override
            public void music(WebSocketBean.Datas data) {
                if (data == null) return;
                //音乐推送
                music_id = data.getMusic_id();
                music_title = data.getMusic_title();
                music_auther = data.getAuther();
                music_xml = data.getFile_xml();
                music_updatatime = data.getUpdatetime();
                if (type != -1) {
                    int a = type;
                    type = 2;
                    mAdapterList.get(a).upData();
                } else {
                    type = 2;
                }
                if (getTopActivityName(MyAppliction.getContext())) {
                    selectView.setVisibility(View.VISIBLE);
                    popText.setText(music_title + "—" + music_auther);
                } else {
                    Intent intent = new Intent(Constents.ACTION);
                    intent.putExtra(Constents.KEY, Constents.MUSIC);
                    SelectActivity.this.sendBroadcast(intent);
                }
            }
        });
        OkHttpUtils.getInstance().getTimeNot(new ITimeNot() {
            @Override
            public void notTime() {
                if (isShowDialog) {
                    dialog = new TimeDialog(SelectActivity.this);
                    dialog.show();
                    MyLogUtils.e(TAG, "不足5分钟");
                } else {
                    Intent intent = new Intent(Constents.ACTION);
                    intent.putExtra(Constents.KEY, Constents.NOTIME_5);
                    SelectActivity.this.sendBroadcast(intent);
                }
            }
        });
//        popLeftImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectView.setVisibility(View.GONE);
//                if (music_title == null || music_title.equals("")) return;
//                if (music_auther == null || music_auther.equals("")) return;
//                if (music_xml == null || music_xml.equals("")) return;
//                if (music_id == null || music_id.equals("")) return;
//                if (music_updatatime == null || music_updatatime.equals("")) return;
//                isShowDialog = false;//开启另一个activity的时候要设置为false，防止dialog在本页出现
//                Intent intent = new Intent(SelectActivity.this, PullViewActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    /**
     * 获取当前activity
     *
     * @param context
     * @return
     */
    public boolean getTopActivityName(Context context) {
        String topActivityClassName = null;
        ActivityManager activityManager = (ActivityManager) (context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            topActivityClassName = f.getClassName();
        }
//        MyLogUtils.e(TAG, "当前topActivity：" + topActivityClassName);
        return topActivityClassName.contains(TAG);
    }

    /**
     * 设置分数列表,用来展示，不做任何操作
     */
    private void setFraction() {
        pagingMap.put(0, 1);
        mAdapterList.add(0, new ListViewAdapter<Songs.DataBean.ListBean>(null, this, R.layout.item_song_item) {
            @Override
            public void setData(BaseViewHoloder holder, int position, Songs.DataBean.ListBean item) {
                if (position == 0) {
                    mSongName.setText(item.getMusic_title() + "-" + item.getAuther());
                    mSongFraction.setText(item.getScore() + "分");
                }
                if (type == 0 && position == vertical) {
                    holder.getAbroadView().setSelected(true);
                } else {
                    holder.getAbroadView().setSelected(false);
                }
                if (position < 9) {
                    holder.getTextView(R.id.song_id).setText("0" + (position + 1));
                } else {
                    holder.getTextView(R.id.song_id).setText("" + (position + 1));
                }
                holder.getTextView(R.id.song_context).setText(item.getMusic_title() + "-" + item.getAuther());
                TextView text = holder.getTextView(R.id.song_fraction);
                text.setText(item.getScore() + "分");
                text.setVisibility(View.VISIBLE);
                holder.getImageView(R.id.song_img).setVisibility(View.GONE);
            }
        });
        mAdapterList.get(0).setPullToData(20, true, new IPullLoading() {
            @Override
            public void PullToLoading() {
                pagingMap.put(0, pagingMap.get(0) + 1);
                getFraction(false);
            }
        });
        new LowRecyclerViewUtils<Songs.DataBean.ListBean>(recyclerViewList.get(0), 0, mAdapterList.get(0)).addItemDecoration(1, R.color.gary);
        recyclerViewList.get(0).setAdapter(mAdapterList.get(0));
        getFraction(true);
    }

    /**
     * 获取分数列表
     */
    private void getFraction(final boolean isUpdatas) {
        Map<String, String> maps = new HashMap<>();
        maps.put("sort", "1");//排序
        maps.put("user_id", Constents.user_id);//排序
        maps.put("page", "" + pagingMap.get(0));//分页，每页20条
        OkHttpUtils.getInstance().postMap(HttpUrls.MUSICHISTORY, maps, new IOkHttpCallBack() {
            @Override
            public void success(String result) {
                Songs bean = OkHttpUtils.Json2Bean(result, Songs.class);
                if (bean.getCode().equals("000")) {
                    if (isUpdatas) {
                        mAdapterList.get(0).upData(bean.getData().getList());
                    } else {
                        mAdapterList.get(0).insertDatas(bean.getData().getList());
                    }
                }
            }
        });

    }

    /**
     * 获取所有的歌曲集合
     *
     * @param id
     * @param adapter
     */
    private void getAllList(final String id, final ListViewAdapter<Songs.DataBean.ListBean> adapter, int pager) {
        Map<String, String> maps = new HashMap<>();
        maps.put("category_id", id);
        maps.put("page", "" + pager);
        OkHttpUtils.getInstance().postMap(HttpUrls.GETLIST, maps, new IOkHttpCallBack() {
            @Override
            public void success(String result) {
                Songs beans = OkHttpUtils.Json2Bean(result, Songs.class);
                if (beans.getCode().equals("000")) {
                    adapter.insertDatas(beans.getData().getList());
                }
            }
        });
    }

    /**
     * 获取视频集合
     */
    private void getVideo() {
        Map<String, String> maps = new HashMap<>();
        maps.put("page", "" + pagingMap.get(1));
        OkHttpUtils.getInstance().postMap(HttpUrls.GETVIDEOLIST, maps, new IOkHttpCallBack() {
            @Override
            public void success(String result) {
                Songs bean = OkHttpUtils.Json2Bean(result, Songs.class);
                if (bean.getCode().equals("000")) {
                    mAdapterList.get(1).insertDatas(bean.getData().getList());
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                //左
                if (selectView.getVisibility() == View.VISIBLE) {
                    popLeftImg.setSelected(true);
                    popRigetImg.setSelected(false);
                } else {
//                    MyLogUtils.e(TAG, "左");
                    type--;
                    vertical = 0;
                    setLevelSelect();
                }
                return true;

            case KeyEvent.KEYCODE_DPAD_UP:
                //上
                if (selectView.getVisibility() == View.VISIBLE) return true;
//                MyLogUtils.e(TAG, "上");
                vertical--;
                setVerticalSelect();
                return true;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                //右
                if (selectView.getVisibility() == View.VISIBLE) {
                    popRigetImg.setSelected(true);
                    popLeftImg.setSelected(false);
                } else {
//                    MyLogUtils.e(TAG, "右");
                    type++;
                    vertical = 0;
                    setLevelSelect();
                }
                return true;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                //下
                if (selectView.getVisibility() == View.VISIBLE) return true;
//                MyLogUtils.e(TAG, "下");
                vertical++;
                setVerticalSelect();
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (type > 1 || type == 0) {
                    if (selectView.getVisibility() == View.VISIBLE) {
                        startNextActivity();
                    } else {
                        if (type == 0) {
                            if (mAdapterList.get(type).getmData().size() == 0) {
                                vertical = 0;
                                return true;
                            }
                            if (mAdapterList.get(type).getmData().size() - 1 < vertical)
                                vertical = mAdapterList.get(type).getmData().size() - 1;
                            popData = null;
                            popData = (Songs.DataBean.ListBean) mAdapterList.get(type).getmData().get(vertical);
                            if (popData == null) return true;
                            Map<String, String> map = new HashMap<>();
                            map.put("id", popData.getMusic_id());
                            OkHttpUtils.getInstance().postMap(HttpUrls.GETONEMUSIC, map, new IOkHttpCallBack() {
                                @Override
                                public void success(String result) {
                                    OneSong data = OkHttpUtils.Json2Bean(result, OneSong.class);
                                    if (data != null && data.getCode().equals("000")) {
                                        popData = data.getData();
                                        selectView.setVisibility(View.VISIBLE);
                                        music_title = popData.getTitle();
                                        music_auther = popData.getAuther();
                                        music_xml = popData.getMusic_xml();
                                        music_id = popData.getMusic_id();
                                        music_updatatime = popData.getUpdatetime();
                                        popText.setText(music_title + "—" + music_auther);
                                    } else {
                                        MyToast.ShowLong("获取数据失败");
                                    }
                                }
                            });
                        } else {
                            setPopWinndow();
                        }
                    }
                } else if (type == 1) {
                    popData = (Songs.DataBean.ListBean) mAdapterList.get(1).getmData().get(vertical);
                    if (popData != null) {
                        if (popData.getVideo_xml() == null || popData.getVideo_xml().equals("")) {
                            MyToast.ShowLong("没有有效的路径");
                            return true;
                        }
                        isShowDialog = false;//开启另一个activity的时候要设置为false，防止dialog在本页出现
                        Intent intent = new Intent(SelectActivity.this, VideoActivity.class);
                        intent.putExtra("title", popData.getTitle());
                        intent.putExtra("auther", popData.getAuther());
                        intent.putExtra("xml", popData.getVideo_xml());
                        startActivity(intent);
                    }
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                if (selectView.getVisibility() == View.VISIBLE) {
                    selectView.setVisibility(View.GONE);
                    return true;
                } else if (isWXLogin) {
                    MyToast.ShowLong("请求退出中...");
                    Intent intent = new Intent(Constents.ACTION);
                    intent.putExtra(Constents.KEY, Constents.LOGOUT);
                    SelectActivity.this.sendBroadcast(intent);
                } else {
                    finish();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void startNextActivity() {
        selectView.setVisibility(View.GONE);
        isShowDialog = false;//开启另一个activity的时候要设置为false，防止dialog在本页出现
        if (popLeftImg.isSelected()) {
            Intent intent = new Intent(SelectActivity.this, PullViewActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SelectActivity.this, PianoActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 上下移动
     */
    private void setVerticalSelect() {
        if (type == -1) type = 0;
        if (mAdapterList.get(type).getmData().size() == 0) return;
        if (vertical > mAdapterList.get(type).getmData().size() - 1) {
            vertical = mAdapterList.get(type).getmData().size() - 1;
        } else if (vertical < 0) {
            vertical = 0;
        }
        recyclerViewList.get(type).smoothScrollToPosition(vertical);
        mAdapterList.get(type).upData();
    }

    /**
     * 选择视频，歌曲(向左右移动)
     */
    private void setLevelSelect() {
        int size = mAdapterList.size();
        if (type < 0) {
            type = 0;
        } else if (type > size - 1) {
            type = size - 1;
        }
        if (type == 0) {
            recyclerViewList.get(type).smoothScrollToPosition(vertical);
            mAdapterList.get(type).upData();
            mAdapterList.get(type + 1).upData();
        } else if (type == 1) {
            recyclerViewList.get(type).smoothScrollToPosition(vertical);
            scrollView.smoothScrollTo(680 * (type - 1), 0);
            mAdapterList.get(type - 1).upData();
            mAdapterList.get(type).upData();
            if (type < size - 1) mAdapterList.get(type + 1).upData();
        } else {
            recyclerViewList.get(type).smoothScrollToPosition(vertical);
            scrollView.smoothScrollTo(680 * (type - 1), 0);
            mAdapterList.get(type - 1).upData();
            mAdapterList.get(type).upData();
            if (type < size - 1) mAdapterList.get(type + 1).upData();
        }
        if (mAdapterList.get(type).getmData().size() == 0) return;
    }

    /**
     * 选择演奏模式
     */
    private void setPopWinndow() {
//        MyLogUtils.e(TAG, "选择模式");
        if (mAdapterList.get(type).getmData().size() == 0) {
            vertical = 0;
            return;
        }
        if (mAdapterList.get(type).getmData().size() - 1 < vertical)
            vertical = mAdapterList.get(type).getmData().size() - 1;
        popData = null;
        popData = (Songs.DataBean.ListBean) mAdapterList.get(type).getmData().get(vertical);
        if (popData == null) return;
        music_title = popData.getTitle();
        music_auther = popData.getAuther();
        music_xml = popData.getMusic_xml();
        music_id = popData.getMusic_id();
        music_updatatime = popData.getUpdatetime();

        if (music_xml == null || music_xml.equals("")) {
            MyToast.ShowLong("没有发现该文件");
            return;
        }

        if (music_title == null || music_title.equals("")) {
            MyToast.ShowLong("歌曲名字为空");
            return;
        }

        if (music_auther == null || music_auther.equals("")) {
            MyToast.ShowLong("作者为空");
            return;
        }
        if (music_id == null || music_id.equals("")) {
            MyToast.ShowLong("Id为空");
            return;
        }

        if (music_updatatime == null || music_updatatime.equals(""))
            return;

        selectView.setVisibility(View.VISIBLE);
        popText.setText(music_title + "—" + music_auther);
    }


    /**
     * 广播接收器
     */
    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra(Constents.KEY)) {
                case Constents.LOGOUT_FINISH:
                    //activity直接退出
                    SelectActivity.this.finish();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        selectActivity = null;
        unregisterReceiver(receiver);
    }

    public void getData(IGetSelectData data) {
        if (data == null) return;
        data.data(nickname, icon,//昵称，用户头像
                music_updatatime.replace(" ", "_"), music_title,
                music_auther, music_xml, music_id);//音乐相关
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isUpdata) {
            pagingMap.put(0, 1);
            getFraction(true);
            isUpdata = false;
        }
    }
}
