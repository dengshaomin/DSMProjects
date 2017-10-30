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
import android.view.View;
import android.view.ViewGroup;

import com.liuxiaozhu.lowrecyclerviews.adapter.viewholder.BaseViewHoloder;

import java.util.List;

/**
 * Created by liuxiaozhu on 2017/7/17.
 * All Rights Reserved by YiZu
 * ListView的适配器
 * 实现功能：
 * 1.添加headerView和FooterView
 * 2.添加上拉加载
 * 3.没有数据时现实默认布局（没有数据）
 */

public abstract class ListViewAdapter<T> extends BaseRecyclerAdapter {

    public ListViewAdapter(List<T> data, Context mContext, @LayoutRes int layoutId) {
        super(data, mContext, layoutId);
    }

    /**
     * 设置item的数量
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return setItemCount();
    }

    /**
     * 设置Item的类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return setItemType(position);
    }

    /**
     * 创建item的view
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public BaseViewHoloder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHoloder holoder = null;
        if (viewType == 40000) {
            //没有数据
            holoder = new BaseViewHoloder(mInflater.inflate(noDataViewId, parent, false), this);
        } else {
            if (viewType < 10000) {
                //headerView
                View headerView = mInflater.inflate((Integer) getHeaderView().get(viewType), parent, false);
                holoder = new BaseViewHoloder(headerView, this);
            } else if (viewType == 10000) {
                //列表数据
                holoder = new BaseViewHoloder(mInflater.inflate(mLayoutId, parent, false), this);
            } else if (viewType < 40000) {
                //footerView
                View footerView = mInflater.inflate((Integer) getFooterView().get(viewType - 20000), parent, false);
                holoder = new BaseViewHoloder(footerView, this);
            }
//            else if (viewType==30000) {
//                //loading
//                holoder = new BaseViewHoloder(getLoadView(), this);
//            }
        }
        return holoder;
    }

    /**
     * 绑定item的数据
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(BaseViewHoloder holder, int position) {
        //此处将每一个item的position传入adapter
        holder.setmPosition(position);
        if (isNoData) {
            if (mIEmptyView != null) {
                mIEmptyView.setEmptyView(holder.getAbroadView());
            }
        } else {
            if (position < getHeaderView().size()) {
                mIHeaderView.HeaderView(holder.getAbroadView(), position);
            } else if (position >= getHeaderView().size() && position < mData.size() + getHeaderView().size()) {
                setData(holder, position - getHeaderView().size(), (T) mData.get(position - getHeaderView().size()));
            } else {
                mIFootView.FooterView(holder.getAbroadView(),position-getHeaderView().size()-mData.size());
            }
            //上拉加载,绑定数据的时候，如果position为
            if (isLoading) {
                if (position-getHeaderView().size() >= pullLoadingPosition - 1 && mIPullLoading != null) {
                    mIPullLoading.PullToLoading();
                    pullLoadingPosition = pullLoadingPosition + 20;
                }
            }

        }
    }

    /**
     * 设置列表数据
     *
     * @param holder
     * @param position
     * @param item
     */
    protected abstract void setData(BaseViewHoloder holder, int position, T item);

}
