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
package com.liuxiaozhu.lowrecyclerviews.wedgit;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.liuxiaozhu.lowrecyclerviews.adapter.BaseRecyclerAdapter;
import com.liuxiaozhu.lowrecyclerviews.adapter.GridViewAdapter;
import com.liuxiaozhu.lowrecyclerviews.adapter.HGridViewAdapter;
import com.liuxiaozhu.lowrecyclerviews.adapter.HListViewAdapter;
import com.liuxiaozhu.lowrecyclerviews.adapter.ListViewAdapter;
import com.liuxiaozhu.lowrecyclerviews.adapter.WaterFullAdapter;

/**
 * Created by chenhui on 2017/4/26.
 * All Rights Reserved by YiZu
 * 通用recyclerView的分割线
 */
public class RecyclerDivider<T> extends RecyclerView.ItemDecoration {
    private Paint mPaint;
    private Drawable mDivider;
    protected int mDividerHeight = 2;//分割线高度，默认为2px
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};//系统的默认颜色
    protected BaseRecyclerAdapter<T> mAdapter;
    protected int mNunColumns = 2;
    private int head = 0;
    private int foot = 0;
    private int data = 0;

    /**
     * 默认分割线 高度为2px，颜色为灰色（系统色）
     * 此方法过时（主要是自定义分割线的特殊颜色如渐变等，一般开发中使用不到）
     */
    @Deprecated
    public RecyclerDivider(int dividerHeight, BaseRecyclerAdapter<T> adapter, int nunColumns) {
        mDividerHeight = dividerHeight;
        mNunColumns = nunColumns;
        mAdapter = adapter;
        head = mAdapter.getHeaderView().size();
        foot = mAdapter.getFooterView().size();
        data = mAdapter.getmData().size();
        final TypedArray a = mAdapter.getContext().obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    /**
     * 自定义分割线(高和颜色)
     *
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     * @param adapter
     * @param nunColumns RecyclerView的行数或列数
     */
    public RecyclerDivider(int dividerHeight, BaseRecyclerAdapter<T> adapter, int dividerColor, int nunColumns) {
//        this(dividerHeight, adapter, nunColumns);
        mDividerHeight = dividerHeight;
        mNunColumns = nunColumns;
        mAdapter = adapter;
        head = mAdapter.getHeaderView().size();
        foot = mAdapter.getFooterView().size();
        data = mAdapter.getmData().size();
//        初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        //填充
        mPaint.setStyle(Paint.Style.FILL);
    }
    /**
     * 设置分割线
     * 先调用此方法（设置完后在执行onDraw方法）
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int itemPosition = parent.getChildAdapterPosition(view);
        if (mAdapter instanceof ListViewAdapter) {
            //此方法是用来指定itemview的paddingLeft，paddingTop， paddingRight， paddingBottom
//            if (itemPosition != head + data + foot - 1) {
//                outRect.set(0, 0, 0, mDividerHeight);
//            }
            outRect.set(0, 0, 0, mDividerHeight);
        }
        if (mAdapter instanceof GridViewAdapter) {
            setGrid(itemPosition, outRect);
        }
        if (mAdapter instanceof WaterFullAdapter) {
            setWDicider(itemPosition, outRect);
        }
        if (mAdapter instanceof HGridViewAdapter) {
            outRect.set(0, 0, mDividerHeight, mDividerHeight);
        }
        if (mAdapter instanceof HListViewAdapter) {
            if (itemPosition != head + data + foot - 1) {
                outRect.set(0, 0, mDividerHeight, 0);
            }
        }
    }

    private void setWDicider(int itemPosition, Rect outRect) {
        if (itemPosition < head) {
            outRect.set(0, 0, 0, mDividerHeight);
        } else if (itemPosition < head + data) {
            outRect.set(0, 0, mDividerHeight, mDividerHeight);
        } else {
//            if (itemPosition != head + data + foot - 1) {
//                outRect.set(0, 0, 0, mDividerHeight);
//            }
            outRect.set(0, 0, 0, mDividerHeight);
        }
    }

    /**
     * 绘制分割线
     * getItemOffsets方法调用后会调用此方法
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mAdapter instanceof ListViewAdapter) {
            drawButtom(c, parent);
        }
        if (mAdapter instanceof HListViewAdapter) {
            drawRigth(c, parent);
        }
        if (mAdapter instanceof GridViewAdapter) {
            drawButtom(c, parent);
            drawRigth(c, parent);
        }
        if (mAdapter instanceof WaterFullAdapter) {
            drawButtom(c, parent);
            drawRigth(c, parent);
        }
        if (mAdapter instanceof HGridViewAdapter) {
            drawButtom(c, parent);
            drawRigth(c, parent);
        }
    }

    /**
     * 画divider （底部）
     * @param c
     * @param parent
     */
    private void drawButtom(Canvas c, RecyclerView parent) {
        //获取Item的数量
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            //获取对应poisition对应的item
            final View child = parent.getChildAt(i);
            int left = child.getLeft();
            int right = child.getRight() + mDividerHeight;
            int top = child.getBottom();
            int bottom = top + mDividerHeight;
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }

    /**
     * 画divider（右边）
     *
     * @param c
     * @param parent
     */
    private void drawRigth(Canvas c, RecyclerView parent) {
        //获取Item的数量
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            //获取对应poisition对应的item
            final View child = parent.getChildAt(i);
            int top = child.getTop();
            int left = child.getRight();
            int bottom = child.getBottom();
            int right = left + mDividerHeight;
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }

    public void setGrid(int position, Rect outRect) {
        //处理头部divider
        if (position < head) {
            //有头部只绘制底部
            outRect.set(0, 0, 0, mDividerHeight);
        } else if (position < head + data) {
            if (isLastColumn(position)) {
//                最后一列右侧不偏移，如果没添加尾部且是最后一行，底部不偏移
                if (mAdapter.getFooterView().size()==0 && isLastLines(position)) {
                    outRect.set(0, 0, 0, 0);
                } else {
                    outRect.set(0, 0, 0, mDividerHeight);
                }
            } else if (isLastLines(position)) {
//                最后一行
                if (mAdapter.getFooterView().size()!=0) {
                    outRect.set(0, 0, mDividerHeight, mDividerHeight);
                } else {
                    outRect.set(0, 0, mDividerHeight, 0);
                }
            } else {
                outRect.set(0, 0, mDividerHeight, mDividerHeight);
            }
        } else {
//            处理尾部，只绘制底部，最后一行底部不绘制
            if (position == head + data + foot - 1) {
                outRect.set(0, 0, 0, 0);
            } else {
                outRect.set(0, 0, 0, mDividerHeight);
            }
        }

    }

    /**
     * 判断是否是最后一列（竖直）
     *
     * @param position
     * @return
     */
    private boolean isLastColumn(int position) {
        int j = position - head;
        int po = j % mNunColumns;
        int i = mNunColumns - 1;
        return po == i;
    }

    /**
     * 判断是否是最后一行（竖直）
     *
     * @param position
     * @return
     */
    private boolean isLastLines(int position) {
        if (position < head + data - (data % mNunColumns)) {
            return false;
        } else {
            return true;
        }
    }

}
