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
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.liuxiaozhu.lowrecyclerviews.adapter.viewholder.BaseViewHoloder;

import java.util.List;

/**
 * Created by liuxiaozhu on 2017/7/18.
 * All Rights Reserved by YiZu
 */

public abstract class WaterFullAdapter<T> extends BaseRecyclerAdapter {
    public WaterFullAdapter(List<T> data, Context mContext, @LayoutRes int layoutId) {
        super(data, mContext, layoutId);
    }

    @Override
    public int getItemCount() {
        return setItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return setItemType(position);
    }


    @Override
    public BaseViewHoloder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHoloder holoder = null;
        if (viewType == 40000) {
            //没有数据
            holoder = new BaseViewHoloder(mInflater.inflate(noDataViewId,parent,false), this);
        } else {
            if (viewType < 10000) {
                //headerView
                View headerView = mInflater.inflate((Integer) getHeaderView().get(viewType), parent, false);
                holoder = new BaseViewHoloder(headerView , this);
            } else if (viewType == 10000) {
                //列表数据
                holoder = new BaseViewHoloder(mInflater.inflate(mLayoutId, parent,false), this);
            } else if (viewType < 40000) {
                //footerView
                View footerView = mInflater.inflate((Integer) getFooterView().get(viewType - 20000), parent, false);
                holoder = new BaseViewHoloder(footerView, this);
            }
        }
        return holoder;
    }

    @Override
    public void onBindViewHolder(BaseViewHoloder holder, int position) {
        //此处将每一个item的position传入adapter
        holder.setmPosition(position);
        if (isNoData) {
            setWaterFall(holder);
            if (mIEmptyView != null) {
                mIEmptyView.setEmptyView(holder.getAbroadView());
            }
        } else {
            if (position < getHeaderView().size()) {
                setWaterFall(holder);
                mIHeaderView.HeaderView(holder.getAbroadView(),position);
            } else if (position < mData.size()+getHeaderView().size()) {
                setData(holder, position - getHeaderView().size(), (T) mData.get(position - getHeaderView().size()));
            } else {
                setWaterFall(holder);
                mIFootView.FooterView(holder.getAbroadView(),position-getHeaderView().size()-mData.size());
            }
            if (position >= pullLoadingPosition - 1 && mIPullLoading != null) {
                mIPullLoading.PullToLoading();
            }

        }

    }
    /**
     * 瀑布流时将头部最外面的布局设置成铺满
     *
     * @param holder
     */
    private void setWaterFall(BaseViewHoloder holder) {
        StaggeredGridLayoutManager.LayoutParams clp =
                (StaggeredGridLayoutManager.LayoutParams) holder.getAbroadView().getLayoutParams();
        clp.setFullSpan(true);
    }

    /**
     * 设置列表数据
     * @param holder
     * @param position
     * @param item
     */
    protected abstract void setData(BaseViewHoloder holder, int position, T item);
}
