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
import android.view.ViewGroup;

import com.liuxiaozhu.lowrecyclerviews.adapter.viewholder.BaseViewHoloder;

import java.util.List;

/**
 * Created by liuxiaozhu on 2017/7/18.
 * All Rights Reserved by YiZu
 * 横向滑动的GridView适配器
 */

public abstract class HGridViewAdapter<T> extends BaseRecyclerAdapter {
    public HGridViewAdapter(List<T> data, Context mContext, @LayoutRes int layoutId) {
        super(data, mContext, layoutId);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    @Override
    public BaseViewHoloder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHoloder(mInflater.inflate(mLayoutId,parent,false),this);
    }

    @Override
    public void onBindViewHolder(BaseViewHoloder holder, int position) {
        //此处将每一个item的position传入adapter
        holder.setmPosition(position);
        setData(holder,position, (T) mData.get(position));
    }
    /**
     * 设置列表数据
     * @param holder
     * @param position
     * @param item
     */
    protected abstract void setData(BaseViewHoloder holder, int position, T item);
}
