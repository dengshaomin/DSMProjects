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

package com.liuxiaozhu.lowrecyclerviews.adapter.viewholder;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liuxiaozhu.lowrecyclerviews.callbacks.IRcyclerClickBack;


/**
 * Created by liuxiaozhu on 2017/4/26.
 * All Rights Reserved by YiZu
 * ViewHoloder的封装
 */

public class BaseViewHoloder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    /**
     * 存储每个item中的子控件（牺牲了一定的内存，但是不用每次都需要findviewbyid）
     */
    private SparseArray<View> mSparseArray = new SparseArray<>();
    /**
     * 用来接收每个item对应的view
     */
    private View mView;
    /**
     * 每个item对应的position
     */
    private int mPosition = 0;
    /**
     * 点击事件的回掉
     */
    private IRcyclerClickBack mCallBack;


    public BaseViewHoloder(View view, final IRcyclerClickBack mCallBack) {
        super(view);//必须实现
        this.mView = view;
        this.mCallBack = mCallBack;
    }

    /**
     * 设置position
     *
     * @return
     */
    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    /**
     * 获取item的view
     *
     * @return
     */
    public View getAbroadView() {
        return mView;
    }

    /**
     * View中找到viewId对应的控件
     *
     * @param viewId
     * @param <T>
     * @return
     */
    private <T extends View> T findView(@IdRes int viewId) {
        View view = null;
        if (mView != null) {
            view = mView.findViewById(viewId);
        }
        if (view != null) {
            mSparseArray.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 获取某个控件的view
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(@IdRes int viewId) {
        View view = mSparseArray.get(viewId);
        if (view != null) {
            return (T) view;
        } else {
            view = findView(viewId);
            return (T) view;
        }
    }


    /**
     * 子view点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (mCallBack != null) {
            mCallBack.onViewClick(mPosition, v, this);
        }
    }

    /**
     * 子view长按事件
     *
     * @param v
     * @return
     */
    @Override
    public boolean onLongClick(View v) {
        if (mCallBack != null) {
            mCallBack.onViewLongClick(mPosition, v, this);
        }
        return true;
    }

    /**
     * 给一个view设置点击事件监听
     *
     * @param viewId ：
     * @return ：
     */
    public BaseViewHoloder setClickListener(@IdRes int viewId) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnClickListener(this);
        }
        return this;
    }

    /**
     * 给一个view设置长按点击事件监听
     *
     * @param viewId ：
     * @return ：
     */
    public BaseViewHoloder setLongClickListener(@IdRes int viewId) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnLongClickListener(this);
        }
        return this;
    }

    /**
     * 给一个item设置点击事件监听
     *
     * @return ：
     */
    public BaseViewHoloder setItemClickListener() {
        if (mView != null) {
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onItemClickBack(mPosition, v, BaseViewHoloder.this);
                }
            });
        }
        return this;
    }

    /**
     * 给一个item设置长按点击事件监听
     *
     * @return ：
     */
    public BaseViewHoloder setItemLongClickListener() {
        if (mView != null) {
            mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mCallBack.onLongItemClickBack(mPosition, v, BaseViewHoloder.this);
                    return true;
                }
            });
        }
        return this;
    }

    /**
     * 根据id获取TextView
     *
     * @param viewId
     * @return
     */
    public TextView getTextView(@IdRes int viewId) {
        return getView(viewId);
    }

    /**
     * 根据id获取Imageview
     *
     * @param viewId
     * @return
     */
    public ImageView getImageView(@IdRes int viewId) {
        return getView(viewId);
    }

    /**
     * 根据id获取LinearLayout
     *
     * @param viewId
     * @return
     */
    public LinearLayout getLinearLayout(@IdRes int viewId) {
        return getView(viewId);
    }

    /**
     * 根据id获取RelativeLayout
     *
     * @param viewId
     * @return
     */
    public RelativeLayout getRelativeLayout(@IdRes int viewId) {
        return getView(viewId);
    }

    /**
     * 根据id获取Button
     *
     * @param viewId
     * @return
     */
    public Button getButton(@IdRes int viewId) {
        return getView(viewId);
    }

    /**
     * 根据id获取EditText
     *
     * @param viewId
     * @return
     */
    public EditText getEditText(@IdRes int viewId) {
        return getView(viewId);
    }

    /**
     * 设置图片的高
     *
     * @param viewId
     * @param height
     */
    public void setImageHeight(@IdRes int viewId, int height) {
        ImageView view = getView(viewId);
        if (view != null) {
            ViewGroup.LayoutParams para = view.getLayoutParams();
            para.height = height;
            view.setLayoutParams(para);
        }
    }

    /**
     * 设置图片的宽
     *
     * @param viewId
     * @param width
     */
    public void setImageWidth(@IdRes int viewId, int width) {
        ImageView view = getView(viewId);
        if (view != null) {
            ViewGroup.LayoutParams para = view.getLayoutParams();
            para.width = width;
            view.setLayoutParams(para);
        }
    }

    public RecyclerView getRecyclerView(@IdRes int viewId) {
        return getView(viewId);
    }
}
