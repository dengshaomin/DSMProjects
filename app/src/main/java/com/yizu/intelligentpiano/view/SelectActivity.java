package com.yizu.intelligentpiano.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
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
import com.yizu.intelligentpiano.bean.MusicHistort;
import com.yizu.intelligentpiano.bean.Songs;
import com.yizu.intelligentpiano.bean.UserInfo;
import com.yizu.intelligentpiano.broadcast.TimeChangeReceiver;
import com.yizu.intelligentpiano.constens.Constents;
import com.yizu.intelligentpiano.constens.HttpUrls;
import com.yizu.intelligentpiano.constens.INetStatus;
import com.yizu.intelligentpiano.constens.IOkHttpCallBack;
import com.yizu.intelligentpiano.utils.MyLogUtils;
import com.yizu.intelligentpiano.utils.MyToast;
import com.yizu.intelligentpiano.utils.OkHttpUtils;
import com.yizu.intelligentpiano.utils.PreManger;

import java.util.HashMap;
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

    //水平 ,-1代表打分，0-6代表歌曲,
    private int level = -2;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
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
        setSongs1();
        setSongs2();
        setSongs3();
        setSongs4();
        setSongs5();
        setSongs6();
    }


    /**
     * 设置视频-练习指导
     */
    private void setVedio() {
        mAdapterVedio = new ListViewAdapter<Songs.DataBean.ListBean>(null, this, R.layout.item_song_item) {
            @Override
            protected void setData(BaseViewHoloder holder, int position, Songs.DataBean.ListBean item) {
                if (0 == level && position == vertical) {
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
                if (1 == level && position == vertical) {
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
                if (2 == level && position == vertical) {
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
                if (3 == level && position == vertical) {
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
                if (4 == level && position == vertical) {
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
                if (5 == level && position == vertical) {
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
                if (6 == level && position == vertical) {
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
        Map<String, String> maps = new HashMap<>();
        maps.put("user_id", Constents.user_id);
        maps.put("device_id", PreManger.instance().getMacId());
        OkHttpUtils.postMap(HttpUrls.GETUSERINFO, maps, new IOkHttpCallBack() {
            @Override
            public void success(String result) {
                UserInfo bean = OkHttpUtils.Json2Bean(result, UserInfo.class);
                if (bean.getCode().equals("000")) {
                    if (bean.getData().getLeftscore().length() < 6) {
                        int time = Integer.parseInt(bean.getData().getLeftscore());
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
                        mUserName.setText(bean.getData().getNickname());
                        Glide.with(SelectActivity.this).load(bean.getData().getHeadimg()).into(mIcon);
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
                    intent.putExtra("what", Constents.LOGOUT_FINISH);
                    SelectActivity.this.sendBroadcast(intent);
                    MyLogUtils.e(TAG, "结束广播，网络断开");
                }
            }
        });
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
                if (level == -1 && position == vertical) {
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

//    /**
//     * 设置歌曲列表
//     */
//    private void setSongList() {
////        mSongList = new ArrayList<>();
////        mSongList.add("");
////        mSongList.add("");
////        mSongList.add("");
////        mSongList.add("");
////        mSongList.add("");
////        mSongList.add("");
////        mSongList.add("");
////        songAdapter = new HListViewAdapter<String>(mSongList, this, R.layout.item_song) {
////            @Override
////            protected void setData(BaseViewHoloder holder, int position, String item) {
////                holder.getTextView(R.id.song_title).setText(item);
////                RecyclerView views = holder.getRecyclerView(R.id.song_recycler);
////                RecyclerMap.put(position, views);
////                setSongs(position, views);
////            }
////        };
////        new LowRecyclerViewUtils<SongList.DataBean>(mSongRecycler, 0, songAdapter);
//////        new LowRecyclerViewUtils<SongList.DataBean>(mSongRecycler, 1, songAdapter).addItemDecoration(30, R.color.none);
////        mSongRecycler.setAdapter(songAdapter);
////        mSongRecycler.smoothScrollToPosition(6);
////        mSongRecycler.smoothScrollToPosition(0);
//        // TODO: 2017/10/28
////        songAdapter.upData();
////        mSongRecycler.scrollToPosition(0);
////        getSongList();
//    }

//    /**
//     * 获取歌曲列表
//     */
//    private void getSongList() {
//        OkHttpUtils.postMap(HttpUrls.GETCATEGORY, null, new IOkHttpCallBack() {
//            @Override
//            public void success(String result) {
//                SongList bean = OkHttpUtils.Json2Bean(result, SongList.class);
//                if (bean.getCode().equals("000")) {
//                    List<SongList.DataBean> data = new ArrayList<>();
//                    data.add(new SongList.DataBean(""));
//                    data.addAll(bean.getData());
//                    songAdapter.insertDatas(data);
//                }
//            }
//        });
//    }

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

//    /**
//     * 二级歌曲列表
//     *
//     * @param mPosition
//     * @param views
//     */
//    private void setSongs(final int mPosition, RecyclerView views) {
//        if (songDataMap == null) {
//            songDataMap = new HashMap<>();
//        }
//        if (songMap == null) {
//            songMap = new HashMap<>();
//        }
//        final ListViewAdapter<Songs.DataBean.ListBean> adapter = new ListViewAdapter<Songs.DataBean.ListBean>(songDataMap.get(mPosition), this, R.layout.item_song_item) {
//            @Override
//            protected void setData(BaseViewHoloder holder, int position, Songs.DataBean.ListBean item) {
//                if (mPosition == level && position == vertical) {
//                    holder.getAbroadView().setSelected(true);
//                } else {
//                    holder.getAbroadView().setSelected(false);
//                }
//                if (position < 9) {
//                    holder.getTextView(R.id.song_id).setText("0" + (position + 1));
//                } else {
//                    holder.getTextView(R.id.song_id).setText("" + (position + 1));
//                }
//                holder.getTextView(R.id.song_context).setText(item.getTitle() + "-" + item.getAuther());
//                if (mPosition == 0) {
//                    holder.getImageView(R.id.song_img).setImageResource(R.mipmap.vdio);
//                } else {
//                    holder.getImageView(R.id.song_img).setImageResource(R.mipmap.music);
//                }
//            }
//        };
//        new LowRecyclerViewUtils<Songs.DataBean.ListBean>(views, 0, adapter).addItemDecoration(1, R.color.gary);
//        views.setAdapter(adapter);
//        songMap.put(mPosition, adapter);
//        adapter.setPullToData(20, true, new IPullLoading() {
//            @Override
//            public void PullToLoading() {
////                    分页加载
//                if (mPosition == 0) {
//                    getVideo();
//                } else {
//                    getAllList(mPosition, mAdapterSong1);
//                }
//            }
//        });
//        //            只有第一次加载数据
//        if (mPosition == 0 && adapter.getItemCount() == 0) {
//            getVideo();
//        }
//        if (mPosition > 0 && adapter.getItemCount() == 0) {
//            getAllList(mPosition, mAdapterSong1);
//        }
//    }


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
                    level--;
                    vertical = 0;
                    setLevelSelect();
                }
                return true;

            case KeyEvent.KEYCODE_DPAD_UP:
                //上
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
                    level++;
                    vertical = 0;
                    setLevelSelect();
                }
                return true;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                //下
                MyLogUtils.e(TAG, "下");
                vertical++;
                setVerticalSelect();
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                //确定
                MyLogUtils.e(TAG, "确定");
                MyLogUtils.e(TAG, "确定" + level);
                if (timeDialog.getVisibility() == View.VISIBLE) {
                    timeDialog.setVisibility(View.GONE);
                    MyLogUtils.e(TAG, "时间提示框消失");
                } else if (level > 0) {
                    if (selectView.getVisibility() == View.VISIBLE) {
                        if (popData == null) {
                            return true;
                        }
                        selectView.setVisibility(View.GONE);
                        isShowDialog = false;//开启另一个activity的时候要设置为false，防止dialog在本页出现
                        Intent intent = new Intent(SelectActivity.this, PianoActivity.class);
                        if (popLeftImg.isSelected()) {
                            intent.putExtra("isShowPull", true);
                        } else if (popRigetImg.isSelected()) {
                            intent.putExtra("isShowPull", false);
                        }
                        intent.putExtra("type", level);
                        intent.putExtra("title", popData.getTitle());
                        intent.putExtra("auther", popData.getAuther());
                        intent.putExtra("xml", popData.getMusic_xml());
                        startActivity(intent);
                    } else {
                        setPopWinndow();
                    }
                } else if (level == 0) {
                    popData = (Songs.DataBean.ListBean) mAdapterVedio.getmData().get(vertical);
                    if (popData != null) {
                        isShowDialog = false;//开启另一个activity的时候要设置为false，防止dialog在本页出现
                        Intent intent = new Intent(SelectActivity.this, VideoActivity.class);
                        intent.putExtra("title", popData.getTitle());
                        intent.putExtra("auther", popData.getAuther());
                        intent.putExtra("xml", popData.getMusic_xml());
                        if (popData.getMusic_xml() == null) {
                            return true;
                        }
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
                    finish();
                }
                return true;
            case KeyEvent.KEYCODE_ENTER:
                MyLogUtils.e(TAG, "确定");
                MyLogUtils.e(TAG, "确定" + level);
                if (timeDialog.getVisibility() == View.VISIBLE) {
                    timeDialog.setVisibility(View.GONE);
                    MyLogUtils.e(TAG, "时间提示框消失");
                } else if (level > 0) {
                    if (selectView.getVisibility() == View.VISIBLE) {
                        if (popData == null) {
                            return true;
                        }
                        selectView.setVisibility(View.GONE);
                        isShowDialog = false;//开启另一个activity的时候要设置为false，防止dialog在本页出现
                        Intent intent = new Intent(SelectActivity.this, PianoActivity.class);
                        if (popLeftImg.isSelected()) {
                            intent.putExtra("isShowPull", true);
                        } else if (popRigetImg.isSelected()) {
                            intent.putExtra("isShowPull", false);
                        }
                        intent.putExtra("type", level);
                        intent.putExtra("title", popData.getTitle());
                        intent.putExtra("auther", popData.getAuther());
                        intent.putExtra("xml", popData.getMusic_xml());
                        startActivity(intent);
                    } else {
                        setPopWinndow();
                    }
                } else if (level == 0) {
                    popData = (Songs.DataBean.ListBean) mAdapterVedio.getmData().get(vertical);
                    if (popData != null) {
                        isShowDialog = false;//开启另一个activity的时候要设置为false，防止dialog在本页出现
                        Intent intent = new Intent(SelectActivity.this, VideoActivity.class);
                        intent.putExtra("title", popData.getTitle());
                        intent.putExtra("auther", popData.getAuther());
                        intent.putExtra("xml", popData.getMusic_xml());

                        MyLogUtils.e(TAG, "title" + popData.getTitle());
                        MyLogUtils.e(TAG, "auther" + popData.getAuther());
                        MyLogUtils.e(TAG, "xml" + popData.getMusic_xml());
                        startActivity(intent);
                    }
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 上下移动
     */
    private void setVerticalSelect() {
        if (level < -1) {
            level = -1;
        }
        if (vertical < 0) {
            vertical = 0;
        }
        switch (level) {
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
        if (level < -1) {
            level = -1;
        } else if (level > 6) {
            level = 6;
        }
        switch (level) {
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
        MyLogUtils.e(TAG, "显示弹出框");
        selectView.setVisibility(View.VISIBLE);
        switch (level) {
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
        if (popData == null) {
            return;
        }
        popText.setText(popData.getTitle() + "—" + popData.getAuther());
        final WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
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
}
