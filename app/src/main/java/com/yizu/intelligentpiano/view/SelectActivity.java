package com.yizu.intelligentpiano.view;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.liuxiaozhu.lowrecyclerviews.adapter.ListViewAdapter;
import com.liuxiaozhu.lowrecyclerviews.adapter.viewholder.BaseViewHoloder;
import com.liuxiaozhu.lowrecyclerviews.callbacks.IPullLoading;
import com.liuxiaozhu.lowrecyclerviews.utils.LowRecyclerViewUtils;
import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.appliction.MyAppliction;
import com.yizu.intelligentpiano.bean.MusicHistort;
import com.yizu.intelligentpiano.bean.Songs;
import com.yizu.intelligentpiano.bean.UserInfo;
import com.yizu.intelligentpiano.broadcast.MyMessageReceiver;
import com.yizu.intelligentpiano.broadcast.TimeChangeReceiver;
import com.yizu.intelligentpiano.constens.Constents;
import com.yizu.intelligentpiano.constens.HttpUrls;
import com.yizu.intelligentpiano.constens.IGetSelectData;
import com.yizu.intelligentpiano.constens.IMusic;
import com.yizu.intelligentpiano.constens.INetStatus;
import com.yizu.intelligentpiano.constens.IOkHttpCallBack;
import com.yizu.intelligentpiano.utils.MyLogUtils;
import com.yizu.intelligentpiano.utils.MyToast;
import com.yizu.intelligentpiano.utils.OkHttpUtils;
import com.yizu.intelligentpiano.utils.PreManger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 选择播放，演奏或者其他
 */
public class SelectActivity extends BaseActivity {
    private final static String TAG = "SelectActivity";
    private TextView mUserName, mSongName, mSongFraction;
    private ImageView mIcon;
    private RecyclerView mFractionRecyler;

    private ListViewAdapter<MusicHistort.DataBean.ListBean> fractionAdapter;

    //是否是微信登陆，默认不是,如果是在activity销毁的时候停止计时
    private boolean isWXLogin = false;
    //定时器
    private Timer timer;
    //动态广播
    private MyBroadcastReceiver receiver;
    private boolean isShowDialog = true;

    //选择类型，水平 ,-1代表打分，0-6代表歌曲,
    private int type = -2;
    private int vertical = -1;

    //显示时间的dialog
    private RelativeLayout timeDialog;
    //记录分页状态 0视频 1-6歌曲 7；打分
    private int[] pagingMap = {1, 1, 1, 1, 1, 1, 1, 1};


    private RecyclerView recycler_video;
    private RecyclerView recycler_song1;
    private RecyclerView recycler_song2;
    private RecyclerView recycler_song3;
    private RecyclerView recycler_song4;
    private RecyclerView recycler_song5;
    private RecyclerView recycler_song6;

    private ListViewAdapter<Songs.DataBean.ListBean> mAdapterVedio;
    private ListViewAdapter<Songs.DataBean.ListBean> mAdapterSong1;
    private ListViewAdapter<Songs.DataBean.ListBean> mAdapterSong2;
    private ListViewAdapter<Songs.DataBean.ListBean> mAdapterSong3;
    private ListViewAdapter<Songs.DataBean.ListBean> mAdapterSong4;
    private ListViewAdapter<Songs.DataBean.ListBean> mAdapterSong5;
    private ListViewAdapter<Songs.DataBean.ListBean> mAdapterSong6;
    private HorizontalScrollView scrollView;
    //选择播放模式
    private TextView popText;
    private ImageView popLeftImg;
    private ImageView popRigetImg;
    private Songs.DataBean.ListBean popData;
    private RelativeLayout selectView;


    private String nickname;
    private String icon;
    private String music_title;
    private String music_auther;
    private String music_xml;
    private String music_id;
    private String music_type;

    public static SelectActivity selectActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        selectActivity = this;
    }

    @Override
    protected void initView() {
        mUserName = (TextView) findViewById(R.id.user_name);
        mSongName = (TextView) findViewById(R.id.user_song);
        mSongFraction = (TextView) findViewById(R.id.user_songfunction);
        mIcon = (ImageView) findViewById(R.id.user_icon);
        mFractionRecyler = (RecyclerView) findViewById(R.id.record);
        recycler_video = (RecyclerView) findViewById(R.id.recycler_video);
        recycler_song1 = (RecyclerView) findViewById(R.id.recycler_song1);
        recycler_song2 = (RecyclerView) findViewById(R.id.recycler_song2);
        recycler_song3 = (RecyclerView) findViewById(R.id.recycler_song3);
        recycler_song4 = (RecyclerView) findViewById(R.id.recycler_song4);
        recycler_song5 = (RecyclerView) findViewById(R.id.recycler_song5);
        recycler_song6 = (RecyclerView) findViewById(R.id.recycler_song6);
        scrollView = findViewById(R.id.scrollView);

        timeDialog = findViewById(R.id.time);

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
        }
        //获取用户信息
        getUserInfo();
        //打分
        setFraction();
        setVedio();
        //儿童
        setSongs1();
        //金典
        setSongs2();
        //怀古
        setSongs3();
        //流行
        setSongs4();
        //动漫游戏
        setSongs5();
        //伤感
        setSongs6();
    }


    /**
     * 设置视频-练习指导
     */
    private void setVedio() {
        mAdapterVedio = new ListViewAdapter<Songs.DataBean.ListBean>(null, this, R.layout.item_song_item) {
            @Override
            protected void setData(BaseViewHoloder holder, int position, Songs.DataBean.ListBean item) {
                if (0 == type && position == vertical) {
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
        };
        mAdapterVedio.setPullToData(20, true, new IPullLoading() {
            @Override
            public void PullToLoading() {
                int size = pagingMap[0] + 1;
                pagingMap[0] = size;
                getVideo();
            }
        });
        new LowRecyclerViewUtils<Songs.DataBean.ListBean>(recycler_video, 0, mAdapterVedio).addItemDecoration(1, R.color.gary);
        recycler_video.setAdapter(mAdapterVedio);
        getVideo();
    }

    /**
     * 儿童
     */
    private void setSongs1() {
        mAdapterSong1 = new ListViewAdapter<Songs.DataBean.ListBean>(null, this, R.layout.item_song_item) {
            @Override
            protected void setData(BaseViewHoloder holder, int position, Songs.DataBean.ListBean item) {
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

            }
        };
        mAdapterSong1.setPullToData(20, true, new IPullLoading() {
            @Override
            public void PullToLoading() {
                int size = pagingMap[1] + 1;
                pagingMap[1] = size;
                getAllList(1, mAdapterSong1, pagingMap[1]);
            }
        });
        new LowRecyclerViewUtils<Songs.DataBean.ListBean>(recycler_song1, 0, mAdapterSong1).addItemDecoration(1, R.color.gary);
        recycler_song1.setAdapter(mAdapterSong1);
        getAllList(1, mAdapterSong1, pagingMap[1]);
    }

    /**
     * 金典
     */
    private void setSongs2() {
        mAdapterSong2 = new ListViewAdapter<Songs.DataBean.ListBean>(null, this, R.layout.item_song_item) {
            @Override
            protected void setData(BaseViewHoloder holder, int position, Songs.DataBean.ListBean item) {
                if (2 == type && position == vertical) {
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
        };
        mAdapterSong2.setPullToData(20, true, new IPullLoading() {
            @Override
            public void PullToLoading() {
                int size = pagingMap[2] + 1;
                pagingMap[2] = size;
                getAllList(2, mAdapterSong2, pagingMap[2]);
            }
        });
        new LowRecyclerViewUtils<Songs.DataBean.ListBean>(recycler_song2, 0, mAdapterSong2).addItemDecoration(1, R.color.gary);
        recycler_song2.setAdapter(mAdapterSong2);
        getAllList(2, mAdapterSong2, pagingMap[2]);
    }

    /**
     * 怀古
     */
    private void setSongs3() {
        mAdapterSong3 = new ListViewAdapter<Songs.DataBean.ListBean>(null, this, R.layout.item_song_item) {
            @Override
            protected void setData(BaseViewHoloder holder, int position, Songs.DataBean.ListBean item) {
                if (3 == type && position == vertical) {
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
        };
        mAdapterSong3.setPullToData(20, true, new IPullLoading() {
            @Override
            public void PullToLoading() {
                int size = pagingMap[3] + 1;
                pagingMap[3] = size;
                getAllList(3, mAdapterSong3, pagingMap[3]);
            }
        });
        new LowRecyclerViewUtils<Songs.DataBean.ListBean>(recycler_song3, 0, mAdapterSong3).addItemDecoration(1, R.color.gary);
        recycler_song3.setAdapter(mAdapterSong3);
        getAllList(3, mAdapterSong3, pagingMap[3]);
    }

    /**
     * 流行
     */
    private void setSongs4() {
        mAdapterSong4 = new ListViewAdapter<Songs.DataBean.ListBean>(null, this, R.layout.item_song_item) {
            @Override
            protected void setData(BaseViewHoloder holder, int position, Songs.DataBean.ListBean item) {
                if (4 == type && position == vertical) {
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
        };
        mAdapterSong4.setPullToData(20, true, new IPullLoading() {
            @Override
            public void PullToLoading() {
                int size = pagingMap[4] + 1;
                pagingMap[4] = size;
                getAllList(4, mAdapterSong4, pagingMap[4]);
            }
        });
        new LowRecyclerViewUtils<Songs.DataBean.ListBean>(recycler_song4, 0, mAdapterSong4).addItemDecoration(1, R.color.gary);
        recycler_song4.setAdapter(mAdapterSong4);
        getAllList(4, mAdapterSong4, pagingMap[4]);
    }

    /**
     * 动漫游戏
     */
    private void setSongs5() {
        mAdapterSong5 = new ListViewAdapter<Songs.DataBean.ListBean>(null, this, R.layout.item_song_item) {
            @Override
            protected void setData(BaseViewHoloder holder, int position, Songs.DataBean.ListBean item) {
                if (5 == type && position == vertical) {
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
        };
        mAdapterSong5.setPullToData(20, true, new IPullLoading() {
            @Override
            public void PullToLoading() {
                int size = pagingMap[5] + 1;
                pagingMap[5] = size;
                getAllList(5, mAdapterSong5, pagingMap[5]);
            }
        });
        new LowRecyclerViewUtils<Songs.DataBean.ListBean>(recycler_song5, 0, mAdapterSong5).addItemDecoration(1, R.color.gary);
        recycler_song5.setAdapter(mAdapterSong5);
        getAllList(5, mAdapterSong5, pagingMap[5]);
    }

    /**
     * 伤感
     */
    private void setSongs6() {
        mAdapterSong6 = new ListViewAdapter<Songs.DataBean.ListBean>(null, this, R.layout.item_song_item) {
            @Override
            protected void setData(BaseViewHoloder holder, int position, Songs.DataBean.ListBean item) {
                if (6 == type && position == vertical) {
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
        };
        mAdapterSong6.setPullToData(20, true, new IPullLoading() {
            @Override
            public void PullToLoading() {
                int size = pagingMap[6] + 1;
                pagingMap[6] = size;
                getAllList(6, mAdapterSong6, pagingMap[6]);
            }
        });
        new LowRecyclerViewUtils<Songs.DataBean.ListBean>(recycler_song6, 0, mAdapterSong6).addItemDecoration(1, R.color.gary);
        recycler_song6.setAdapter(mAdapterSong6);
        getAllList(6, mAdapterSong6, pagingMap[6]);
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
    private void getUserInfo() {
        if (Constents.user_id.equals("")) {
            return;
        }
        final Map<String, String> maps = new HashMap<>();
        maps.put("user_id", Constents.user_id);
        maps.put("device_id", PreManger.instance().getMacId());
        OkHttpUtils.postMap(HttpUrls.GETUSERINFO, maps, new IOkHttpCallBack() {
            @Override
            public void success(String result) {
                UserInfo mUserInfo = OkHttpUtils.Json2Bean(result, UserInfo.class);
                if (mUserInfo == null) return;
                if (mUserInfo.getCode().equals("000")) {
                    if (mUserInfo.getData().getLeftscore().length() < 6) {
                        int time = Integer.parseInt(mUserInfo.getData().getLeftscore());
                        if (isShowDialog) {
                            if (time <= 60) {
//                                直接退出，时间不足一分钟，请求服务器
                                Intent intent = new Intent(Constents.ACTION);
                                intent.putExtra(Constents.KEY, Constents.LOGOUT);
                                SelectActivity.this.sendBroadcast(intent);
                                MyToast.ShowLong("时间不足，请去充值");
                                MyLogUtils.e(TAG, "结束广播，没有时间");
                            } else if (time / 60 == 5) {
                                //不足5分钟
                                RelativeLayout view = (RelativeLayout) findViewById(R.id.main_select);
                                timeDialog.setVisibility(View.VISIBLE);
                                MyLogUtils.e(TAG, "不足5分钟");
                            }
                        } else {
                            if (time <= 60) {
                                Intent intent = new Intent(Constents.ACTION);
                                intent.putExtra(Constents.KEY, Constents.LOGOUT_FINISH);
                                SelectActivity.this.sendBroadcast(intent);
                                MyToast.ShowLong("时间不足，请去充值");
                                MyLogUtils.e(TAG, "结束广播，没有时间");
                            } else if (time / 60 == 5) {
                                Intent intent = new Intent(Constents.ACTION);
                                intent.putExtra(Constents.KEY, Constents.NOTIME_5);
                                SelectActivity.this.sendBroadcast(intent);
                            }
                        }

                    }
                    if (mUserName.getText().toString().trim().equals("")) {
                        nickname = mUserInfo.getData().getNickname();
                        icon = mUserInfo.getData().getHeadimg();
                        mUserName.setText(nickname);
                        Glide.with(SelectActivity.this).load(mUserInfo.getData().getHeadimg()).into(mIcon);
                    }
                    if (PreManger.instance().getStatus().equals("1")) {
                        timer = new Timer();
                        //60s发送一次请求
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                //公用
                                getUserInfo();
                            }
                        }, 1000 * 60);
                    }
                }
            }
        });

    }

    @Override
    protected void setLinster() {
        TimeChangeReceiver.getNetStatus(new INetStatus() {
            @Override
            public void isNoNet() {
                if (isWXLogin) {
                    MyToast.ShowLong("网络断开，请联网后登陆");
                    Intent intent = new Intent(Constents.ACTION);
                    intent.putExtra(Constents.KEY, Constents.LOGOUT_FINISH);
                    SelectActivity.this.sendBroadcast(intent);
                    MyLogUtils.e(TAG, "结束广播，网络断开");
                }
            }
        });
        popLeftImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectView.getVisibility() == View.VISIBLE) {
                    selectView.setVisibility(View.GONE);
                    if (nickname == null || nickname.equals("")) return;
                    if (icon == null || icon.equals("")) return;
                    if (music_title == null || music_title.equals("")) return;
                    if (music_auther == null || music_auther.equals("")) return;
                    if (music_xml == null || music_xml.equals("")) return;
                    if (music_id == null || music_id.equals("")) return;
                    isShowDialog = false;//开启另一个activity的时候要设置为false，防止dialog在本页出现
                    Intent intent = new Intent(SelectActivity.this, PianoActivity.class);
                    startActivity(intent);
                } else {
                    setPopWinndow();
                }
            }
        });
        MyMessageReceiver.getMusic(new IMusic() {
            @Override
            public void music(Map<String, String> map) {
                if (map == null) return;
                //音乐推送
                music_id = map.get("music_id");
                music_title = map.get("music_title");
                music_auther = map.get("auther");
                music_xml = map.get("file_xml");
                music_type = map.get("type");
                MyLogUtils.e(TAG, "music_id" + music_id);
                MyLogUtils.e(TAG, "music_title" + music_title);
                MyLogUtils.e(TAG, "music_auther" + music_auther);
                MyLogUtils.e(TAG, "music_xml" + music_xml);
                MyLogUtils.e(TAG, "music_type" + music_type);
                MyLogUtils.e(TAG, "" + getTopActivityName(MyAppliction.getContext()));
                if (getTopActivityName(MyAppliction.getContext())) {
                    selectView.setVisibility(View.VISIBLE);
                    popText.setText(music_title + "—" + music_auther);
                    type = 1;
                } else {
                    Intent intent = new Intent(Constents.ACTION);
                    intent.putExtra(Constents.KEY, Constents.MUSIC);
                    SelectActivity.this.sendBroadcast(intent);
                }
            }
        });
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
        MyLogUtils.e(TAG, "当前topActivity：" + topActivityClassName);
        return topActivityClassName.contains(TAG);
    }

    /**
     * 设置分数列表,用来展示，不做任何操作
     */
    private void setFraction() {
        fractionAdapter = new ListViewAdapter<MusicHistort.DataBean.ListBean>(null, this, R.layout.item_song_item) {
            @Override
            protected void setData(BaseViewHoloder holder, int position, MusicHistort.DataBean.ListBean item) {
                if (position == 0) {
                    mSongName.setText(item.getMusic_title() + "-" + item.getAuther());
                    mSongFraction.setText(item.getScore() + "分");
                }
                if (type == -1 && position == vertical) {
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
        };
        fractionAdapter.setPullToData(20, true, new IPullLoading() {
            @Override
            public void PullToLoading() {
                int size = pagingMap[7] + 1;
                pagingMap[7] = size;
                getFraction();
            }
        });
        new LowRecyclerViewUtils<MusicHistort.DataBean.ListBean>(mFractionRecyler, 0, fractionAdapter).addItemDecoration(1, R.color.gary);
        mFractionRecyler.setAdapter(fractionAdapter);
        getFraction();
    }

    /**
     * 获取分数列表
     */
    private void getFraction() {
        Map<String, String> maps = new HashMap<>();
        maps.put("sort", "1");//排序
        maps.put("user_id", Constents.user_id);//排序
        maps.put("page", "" + pagingMap[7]);//分页，每页20条
        OkHttpUtils.postMap(HttpUrls.MUSICHISTORY, maps, new IOkHttpCallBack() {
            @Override
            public void success(String result) {
                MusicHistort bean = OkHttpUtils.Json2Bean(result, MusicHistort.class);
                if (bean.getCode().equals("000")) {
                    fractionAdapter.insertDatas(bean.getData().getList());
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
    private void getAllList(final int id, final ListViewAdapter<Songs.DataBean.ListBean> adapter, int pager) {
        Map<String, String> maps = new HashMap<>();
        maps.put("category_id", "" + id);
        maps.put("page", "" + pager);
        OkHttpUtils.postMap(HttpUrls.GETLIST, maps, new IOkHttpCallBack() {
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
        maps.put("page", "" + pagingMap[0]);
        OkHttpUtils.postMap(HttpUrls.GETVIDEOLIST, maps, new IOkHttpCallBack() {
            @Override
            public void success(String result) {
                Songs bean = OkHttpUtils.Json2Bean(result, Songs.class);
                if (bean.getCode().equals("000")) {
                    mAdapterVedio.insertDatas(bean.getData().getList());
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
                    MyLogUtils.e(TAG, "左");
                    type--;
                    vertical = 0;
                    setLevelSelect();
                }
                return true;

            case KeyEvent.KEYCODE_DPAD_UP:
                //上
                if (selectView.getVisibility() == View.VISIBLE) return true;
                MyLogUtils.e(TAG, "上");
                vertical--;
                setVerticalSelect();
                return true;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                //右
                if (selectView.getVisibility() == View.VISIBLE) {
                    popRigetImg.setSelected(true);
                    popLeftImg.setSelected(false);
                } else {
                    MyLogUtils.e(TAG, "右");
                    type++;
                    vertical = 0;
                    setLevelSelect();
                }
                return true;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                //下
                if (selectView.getVisibility() == View.VISIBLE) return true;
                MyLogUtils.e(TAG, "下");
                vertical++;
                setVerticalSelect();
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
//                MyLogUtils.e(TAG, "确定");
//                MyLogUtils.e(TAG, "确定" + type);
                if (timeDialog.getVisibility() == View.VISIBLE) {
                    timeDialog.setVisibility(View.GONE);
                    MyLogUtils.e(TAG, "时间提示框消失");
                } else if (type > 0) {
                    if (selectView.getVisibility() == View.VISIBLE) {
                        selectView.setVisibility(View.GONE);
                        if (nickname == null || nickname.equals("")) return true;
                        if (icon == null || icon.equals("")) return true;
                        if (music_title == null || music_title.equals("")) return true;
                        if (music_auther == null || music_auther.equals("")) return true;
                        if (music_xml == null || music_xml.equals("")) return true;
                        if (music_id == null || music_id.equals("")) return true;
                        if (music_type == null || music_type.equals("")) return true;
                        isShowDialog = false;//开启另一个activity的时候要设置为false，防止dialog在本页出现
                        Intent intent = new Intent(SelectActivity.this, PianoActivity.class);
                        startActivity(intent);
                    } else {
                        setPopWinndow();
                    }
                } else if (type == 0) {
                    popData = (Songs.DataBean.ListBean) mAdapterVedio.getmData().get(vertical);
                    if (popData != null) {
                        isShowDialog = false;//开启另一个activity的时候要设置为false，防止dialog在本页出现
                        Intent intent = new Intent(SelectActivity.this, VideoActivity.class);
                        intent.putExtra("title", popData.getTitle());
                        intent.putExtra("auther", popData.getAuther());
                        intent.putExtra("xml", popData.getMusic_xml());
                        startActivity(intent);
                    }
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                //返回
                MyLogUtils.e(TAG, "返回");
                if (timeDialog.getVisibility() == View.VISIBLE) {
                    timeDialog.setVisibility(View.GONE);
                } else if (selectView.getVisibility() == View.VISIBLE) {
                    selectView.setVisibility(View.GONE);
                } else if (isWXLogin) {
                    Intent intent = new Intent(Constents.ACTION);
                    intent.putExtra(Constents.KEY, Constents.LOGOUT);
                    SelectActivity.this.sendBroadcast(intent);
                }
                return true;
//            case KeyEvent.KEYCODE_ENTER:
//                MyLogUtils.e(TAG, "确定");
//                MyLogUtils.e(TAG, "确定" + type);
//                if (timeDialog.getVisibility() == View.VISIBLE) {
//                    timeDialog.setVisibility(View.GONE);
//                    MyLogUtils.e(TAG, "时间提示框消失");
//                } else if (type > 0) {
//                    if (selectView.getVisibility() == View.VISIBLE) {
//                        selectView.setVisibility(View.GONE);
//                        if (nickname == null || nickname.equals("")) return true;
//                        if (icon == null || icon.equals("")) return true;
//                        if (music_title == null || music_title.equals("")) return true;
//                        if (music_auther == null || music_auther.equals("")) return true;
//                        if (music_xml == null || music_xml.equals("")) return true;
//                        if (music_id == null || music_id.equals("")) return true;
//                        if (music_type == null || music_type.equals("")) return true;
//                        isShowDialog = false;//开启另一个activity的时候要设置为false，防止dialog在本页出现
//                        Intent intent = new Intent(SelectActivity.this, PianoActivity.class);
//                        startActivity(intent);
//                    } else {
//                        setPopWinndow();
//                    }
//                } else if (type == 0) {
//                    popData = (Songs.DataBean.ListBean) mAdapterVedio.getmData().get(vertical);
//                    if (popData != null) {
//                        isShowDialog = false;//开启另一个activity的时候要设置为false，防止dialog在本页出现
//                        Intent intent = new Intent(SelectActivity.this, VideoActivity.class);
//                        intent.putExtra("title", popData.getTitle());
//                        intent.putExtra("auther", popData.getAuther());
//                        intent.putExtra("xml", popData.getMusic_xml());
//                        startActivity(intent);
//                    }
//                }
//                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 上下移动
     */
    private void setVerticalSelect() {
        if (type < -1) {
            type = -1;
        }
        if (vertical < 0) {
            vertical = 0;
        }
        switch (type) {
            case -1:
//                打分
                if (vertical >= fractionAdapter.getmData().size()) {
                    vertical = fractionAdapter.getmData().size() - 1;
                }
                mFractionRecyler.smoothScrollToPosition(vertical);
                fractionAdapter.upData();
                break;
            case 0:
//                视频
                if (vertical >= mAdapterVedio.getmData().size()) {
                    vertical = mAdapterVedio.getmData().size() - 1;
                }
                recycler_video.smoothScrollToPosition(vertical);
                mAdapterVedio.upData();
                scrollView.smoothScrollTo(0, 0);
                break;
            case 1:
                if (vertical >= mAdapterSong1.getmData().size()) {
                    vertical = mAdapterSong1.getmData().size() - 1;
                }
                recycler_song1.smoothScrollToPosition(vertical);
                mAdapterSong1.upData();
                scrollView.smoothScrollTo(680, 0);
                break;
            case 2:
                if (vertical >= mAdapterSong2.getmData().size()) {
                    vertical = mAdapterSong2.getmData().size() - 1;
                }
                recycler_song2.smoothScrollToPosition(vertical);
                mAdapterSong2.upData();
                scrollView.smoothScrollTo(680 * 2, 0);
                break;
            case 3:
                if (vertical >= mAdapterSong3.getmData().size()) {
                    vertical = mAdapterSong3.getmData().size() - 1;
                }
                recycler_song3.smoothScrollToPosition(vertical);
                mAdapterSong3.upData();
                scrollView.smoothScrollTo(680 * 3, 0);
                break;
            case 4:
                if (vertical >= mAdapterSong4.getmData().size()) {
                    vertical = mAdapterSong4.getmData().size() - 1;
                }
                recycler_song4.smoothScrollToPosition(vertical);
                mAdapterSong4.upData();
                scrollView.smoothScrollTo(680 * 4, 0);
                break;
            case 5:
                if (vertical >= mAdapterSong5.getmData().size()) {
                    vertical = mAdapterSong5.getmData().size() - 1;
                }
                recycler_song5.smoothScrollToPosition(vertical);
                mAdapterSong5.upData();
                scrollView.smoothScrollTo(680 * 5, 0);
                break;
            case 6:
                if (vertical >= mAdapterSong6.getmData().size()) {
                    vertical = mAdapterSong6.getmData().size() - 1;
                }
                recycler_song6.smoothScrollToPosition(vertical);
                mAdapterSong6.upData();
                scrollView.smoothScrollTo(680 * 6, 0);
                break;
        }
    }

    /**
     * 选择视频，歌曲(向左右移动)
     */
    private void setLevelSelect() {
        if (type < -1) {
            type = -1;
        } else if (type > 6) {
            type = 6;
        }
        switch (type) {
            case -1:
//                打分
                mFractionRecyler.smoothScrollToPosition(vertical);
                fractionAdapter.upData();
                mAdapterVedio.upData();
                break;
            case 0:
//                视频
                scrollView.smoothScrollTo(0, 0);
                recycler_video.smoothScrollToPosition(vertical);
                fractionAdapter.upData();
                mAdapterVedio.upData();
                mAdapterSong1.upData();
                break;
            case 1:
                scrollView.smoothScrollTo(680, 0);
                recycler_song1.smoothScrollToPosition(vertical);
                mAdapterVedio.upData();
                mAdapterSong1.upData();
                mAdapterSong2.upData();
                break;
            case 2:
                scrollView.smoothScrollTo(680 * 2, 0);
                recycler_song2.smoothScrollToPosition(vertical);
                mAdapterSong1.upData();
                mAdapterSong2.upData();
                mAdapterSong3.upData();
                break;
            case 3:
                scrollView.smoothScrollTo(680 * 3, 0);
                recycler_song3.smoothScrollToPosition(vertical);
                mAdapterSong2.upData();
                mAdapterSong3.upData();
                mAdapterSong4.upData();
                break;
            case 4:
                scrollView.smoothScrollTo(680 * 4, 0);
                recycler_song4.smoothScrollToPosition(vertical);
                mAdapterSong3.upData();
                mAdapterSong4.upData();
                mAdapterSong5.upData();
                break;
            case 5:
                scrollView.smoothScrollTo(680 * 5, 0);
                recycler_song5.smoothScrollToPosition(vertical);
                mAdapterSong4.upData();
                mAdapterSong5.upData();
                mAdapterSong6.upData();
                break;
            case 6:
                scrollView.smoothScrollTo(680 * 6, 0);
                recycler_song6.smoothScrollToPosition(vertical);
                mAdapterSong5.upData();
                mAdapterSong6.upData();
                break;
        }
    }

    /**
     * 选择演奏模式
     */
    private void setPopWinndow() {
        if (popData == null) return;
        switch (type) {
            case 1:
                popData = (Songs.DataBean.ListBean) mAdapterSong1.getmData().get(vertical);
                break;
            case 2:
                popData = (Songs.DataBean.ListBean) mAdapterSong2.getmData().get(vertical);
                break;
            case 3:
                popData = (Songs.DataBean.ListBean) mAdapterSong3.getmData().get(vertical);
                break;
            case 4:
                popData = (Songs.DataBean.ListBean) mAdapterSong4.getmData().get(vertical);
                break;
            case 5:
                popData = (Songs.DataBean.ListBean) mAdapterSong5.getmData().get(vertical);
                break;
            case 6:
                popData = (Songs.DataBean.ListBean) mAdapterSong6.getmData().get(vertical);
                break;
        }
        if (popData == null) return;
        selectView.setVisibility(View.VISIBLE);
        music_title = popData.getTitle();
        music_auther = popData.getAuther();
        music_xml = popData.getMusic_xml();
        music_id = popData.getMusic_id();
        music_type = popData.getCategory_id();
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
                    if (timer != null) {
                        timer.cancel();
                    }
                    SelectActivity.this.finish();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public void getData(IGetSelectData data) {
        if (data == null) return;
        data.data(nickname, icon, popLeftImg.isSelected(),//昵称，用户头像，是否是瀑布流
                music_type, music_title, music_auther, music_xml, music_id);//音乐相关
    }

}
