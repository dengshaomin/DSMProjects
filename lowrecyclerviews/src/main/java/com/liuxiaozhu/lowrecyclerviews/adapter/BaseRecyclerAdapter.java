/*
Copyright 2017 liusmallpig

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.liuxiaozhu.lowrecyclerviews.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liuxiaozhu.lowrecyclerviews.R;
import com.liuxiaozhu.lowrecyclerviews.adapter.viewholder.BaseViewHoloder;
import com.liuxiaozhu.lowrecyclerviews.callbacks.IEmptyView;
import com.liuxiaozhu.lowrecyclerviews.callbacks.IFooterView;
import com.liuxiaozhu.lowrecyclerviews.callbacks.IHeaderView;
import com.liuxiaozhu.lowrecyclerviews.callbacks.IPullLoading;
import com.liuxiaozhu.lowrecyclerviews.callbacks.IRcyclerClickBack;
import com.liuxiaozhu.lowrecyclerviews.callbacks.OnClick;
import com.liuxiaozhu.lowrecyclerviews.callbacks.OnItemClick;
import com.liuxiaozhu.lowrecyclerviews.callbacks.OnItemLongClick;
import com.liuxiaozhu.lowrecyclerviews.callbacks.OnLongClick;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiaozhu on 2017/7/15.
 * All Rights Reserved by YiZu
 * RecyclerView的适配器的基类
 * 所有的adapter必须继承该类
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseViewHoloder> implements IRcyclerClickBack {
    //存放列表数据
    protected List<T> mData;
    //布局填充器
    protected LayoutInflater mInflater;
    protected Context mContext;
    //列表Item的Id
    protected int mLayoutId = 0;
    //存放头部、尾部的ViewId集合
    private List<Integer> mHeaderView;
    private List<Integer> mFooterView;

    //EmptyView的Id
    protected int noDataViewId = R.layout.no_data;
    //是否显示EmptyView，默认显示
    protected boolean isShowEmptyView = false;
    //主要来判断RecyclerView有没有数据，false表示recyclerView中有数据
    // (主要和isShowEmptyView配合使用，控制是否显示EmptyView)
    protected boolean isNoData = false;

    //上拉加载从第几个位置加载（位置包括HeaderView和FooterView）
    //默认recyclerView的Itemsize大于等于10的时候上啦加载
    //// TODO: 2017/8/17 上啦加载待优化
    protected int pullLoadingPosition = 10;
    public boolean isLoading = false;
    //主要处理RecyclerView列表的数据（不包括）
    private OnClick onClick;
    private OnItemClick onItemClick;
    private OnLongClick onLongClick;
    private OnItemLongClick onItemLongClick;

    //HeaderView回掉接口，用来设置HeaderView
    protected IHeaderView mIHeaderView;
    //FooterView回掉接口，用来设置FooterView
    protected IFooterView mIFootView;
    //EmptyView回掉接口,主要用来设置EmptyView
    protected IEmptyView mIEmptyView;
    //PullToLoading回掉接口，主要实现无感上拉刷新
    // ，当绑定最后一个数据的时候会执行该接口的回掉方法
    protected IPullLoading mIPullLoading;

    /**
     * 通用的构造方法
     * 这个方法主要是为LowRecyclerView中的所有适配器提供公共的方法和数据
     *
     * @param data
     * @param mContext
     * @param layoutId
     */
    public BaseRecyclerAdapter(List<T> data, Context mContext, @LayoutRes int layoutId) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        if (data != null) {
            mData.addAll(data);
        }
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        if (layoutId != 0) {
            mLayoutId = layoutId;
        }
        // TODO: 2017/7/25 待优化
        if (mFooterView == null) {
            mFooterView = new ArrayList<>();
        }
        if (mHeaderView == null) {
            mHeaderView = new ArrayList<>();
        }
    }

    /**
     * 这个构造方法主要是用来定制adapter
     * 主要是用来扩展，实现更多功能
     */
    public BaseRecyclerAdapter(Context context, List<T> data) {
        this(data, context, 0);
    }

    /**
     * *******RecyclerView.Adapter的四个方法，这里复写主要是让子类继承***************
     */

    /**
     * 创建Item的View
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public BaseViewHoloder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    /**
     * 绑定Item数据
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(BaseViewHoloder holder, int position) {
    }


    /**
     * 设置Item数量
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return 0;
    }

    /**
     * 设置每个Item的类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    /**
     * 获取列表数据
     *
     * @return
     */
    public List<T> getmData() {
        return mData;
    }

    /**
     * 获取Context
     *
     * @return
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * ********设置HeaderView、FooterView和PullToLoading（上拉加载）***********
     */

    /**
     * 获取HeaderView集合
     *
     * @return
     */
    public List<Integer> getHeaderView() {
        return mHeaderView;
    }

    /**
     * 添加HeaderView集合
     *
     * @param headerViewId
     */
    public void addHeaderView(List<Integer> headerViewId, IHeaderView IHeaderView) {
        if (IHeaderView == null && headerViewId == null) {
            throw new RuntimeException("IIHeaderView或者headerViewId不能为空");
        }
        mHeaderView.addAll(headerViewId);
        this.mIHeaderView = IHeaderView;
    }

    /**
     * 添加一个HeaderView
     *
     * @param headerViewId
     */
    public void addHeaderView(@LayoutRes int headerViewId, IHeaderView IHeaderView) {
        if (IHeaderView == null) {
            throw new RuntimeException("IIHeaderView不能为空");
        }
        mHeaderView.add(headerViewId);
        this.mIHeaderView = IHeaderView;
    }

    /**
     * 获取FooterView
     *
     * @return
     */
    public List<Integer> getFooterView() {
        return mFooterView;
    }

    /**
     * 添加FooterView集合
     *
     * @param footerViewIdList
     */
    public void addFooterView(List<Integer> footerViewIdList, IFooterView IFooterView) {
        if (IFooterView == null && footerViewIdList == null) {
            throw new RuntimeException("IIHeaderView或者headerViewId不能为空");
        }
        mFooterView.addAll(footerViewIdList);
        this.mIFootView = IFooterView;
    }

    /**
     * 添加单个FooterView
     *
     * @param footerViewId
     */
    public void addFooterView(@LayoutRes int footerViewId, IFooterView IFooterView) {
        if (IFooterView == null) {
            throw new RuntimeException("IIHeaderView不能为空");
        }
        mFooterView.add(footerViewId);
        this.mIFootView = IFooterView;
    }

    /**
     * 上拉加载,当加载到最后一条Item的时候，会执行接口的回掉方法，
     * 且当最后一个Item的posiotion大于等于PullLoadStartPosiotion才会执行
     *
     * @param pullToLoading 回掉接口
     * @param num           RecyclerView的Item数量大于PullLoadStartPosiotion执行上拉加载
     */
    public void setPullToData(int num, boolean isLoading, IPullLoading pullToLoading) {
        if (pullToLoading != null) {
            this.isLoading = isLoading;
            mIPullLoading = pullToLoading;
            if (num > 1) {
                pullLoadingPosition = num;
            } else {
                throw new RuntimeException("setPullToData()的PullLoadStartPosiotion不能小于0");
            }
        } else {
            throw new RuntimeException("setPullToData()的pullToLoading不能为空");
        }

    }

    /**
     * *****************************设置EmptyView*************************
     */

    /**
     * 设置EmptyView的布局
     *
     * @param layoutId ：布局Id
     */
    public void setEmptyViewId(@LayoutRes int layoutId) {
        noDataViewId = layoutId;
    }

    /**
     * 设置EmptyView
     *
     * @param iEmptyView 接口
     */
    public void setEmptyView(IEmptyView iEmptyView) {
        this.mIEmptyView = iEmptyView;
    }

    /**
     * 设置是否显示EmptyView
     *
     * @param showEmptyView true：显示，false隐藏
     */
    public void setShowEmptyView(boolean showEmptyView) {
        isShowEmptyView = showEmptyView;
    }


    /**
     * ***************************处理RecyclerView返回Item数量以及每个Item返回的类型的公共方法*************
     */

    /**
     * 设置ItemType的类型
     *
     * @param posiotion
     * @return 返回类型说明：
     * 1.0-9999代表添加了HeaderView
     * 2.10000代表列表数据（HeaderView和FooterView之间的数据）
     * 3.20000-29999代表添加了FooterView
     * 4.40000代表显示没有数据的默认布局
     */
    protected int setItemType(int posiotion) {
        int type = 0;
        if (isNoData) {
            //没有数据
            type = 40000;
        } else {
            if (posiotion < mHeaderView.size()) {
                //添加了HeaderView
                type = posiotion;
            } else if (posiotion < mHeaderView.size() + mData.size()) {
                //
                type = 10000;
            } else {
                if (posiotion < mHeaderView.size() + mData.size() + mFooterView.size()) {
                    //添加了FooterView
                    type = (20000 + posiotion - mHeaderView.size() - mData.size());
                }
            }
        }
        return type;
    }

    /**
     * 设置Item的总数
     * 主要是getItemCount（）方法来调用
     *
     * @return ItemCount Irem的数量
     */
    protected int setItemCount() {
        int size;
        size = mHeaderView.size() + mData.size() + mFooterView.size();
        if (size == 0 && isShowEmptyView) {
            isNoData = true;
            size = 1;
        }
        return size;
    }


    /**
     * ************************点击事件的回掉方法*****************************
     */

    @Override
    public void onItemClickBack(int position, View view, BaseViewHoloder holder) {
        if (onItemClick != null) {
            onItemClick.onItemClick(position - getHeaderView().size(), view, holder);
        }
    }

    @Override
    public void onLongItemClickBack(int position, View view, BaseViewHoloder holder) {
        if (onItemLongClick != null) {
            onItemLongClick.onItemLongClick(position, view, holder);
        }
    }

    @Override
    public void onViewClick(int position, View view, BaseViewHoloder holder) {
        if (onClick != null) {
            onClick.onClick(position, view, holder);
        }
    }

    @Override
    public void onViewLongClick(int position, View view, BaseViewHoloder holder) {
        if (onLongClick != null) {
            onLongClick.onLongClick(position, view, holder);
        }
    }

    /**
     * 子view点击事件
     *
     * @param onClick
     */
    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }

    /**
     * Item点击事件
     *
     * @param onItemClick
     */
    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    /**
     * 子view长按点击事件
     *
     * @param onLongClick
     */
    public void setOnLongClick(OnLongClick onLongClick) {
        this.onLongClick = onLongClick;
    }

    /**
     * Item长按点击事件
     *
     * @param onItemLongClick
     */
    public void setOnItemLongClick(OnItemLongClick onItemLongClick) {
        this.onItemLongClick = onItemLongClick;
    }


    /**
     * ***************************adapter公用的方法***************************************
     *  这些方法只能操作除HeaderView和FooterView以外的列表数据，
     *  但是刷新的时候刷新的是整个RecyclerView，包括HeaderView和FooterView
     */

    /**
     * 更新整个recycle
     *
     * @param data 要更新的数据集合不能为空
     */
    public void upData(List<T> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    /**
     * 更新整个recycle
     */
    public void upData() {
        notifyDataSetChanged();
    }


    /**
     * 删除指定的Item
     *
     * @param position 指的是集合的下标位置
     */
    public void removeData(int position) {
        if (position < getItemCount()) {
            mData.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * 删除全部数据（不包括HeaderView和FooterView）
     */
    public void removeAllData() {
        mData.clear();
        notifyDataSetChanged();
    }

    /**
     * 在集合指定位置插入数据
     *
     * @param position 插入的位置的下标
     * @param data     插入的数据
     */
    public void insertData(int position, T data) {
        if (position < getItemCount()) {
            mData.add(position, data);
            notifyItemInserted(position);
        }
    }

    /**
     * 列表集合末尾插入数据
     *
     * @param data 插入的数据
     */
    public void insertData(T data) {
        if (data == null) return;
        mData.add(data);
//        notifyItemInserted(mData.size() - 1);
        notifyDataSetChanged();
    }

    /**
     * 列表集合末尾插入数据集合
     *
     * @param data 插入的数据集合
     */
    public void insertDatas(List<T> data) {
        if (data != null) {
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }

}
