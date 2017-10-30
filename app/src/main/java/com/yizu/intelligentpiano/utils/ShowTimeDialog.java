package com.yizu.intelligentpiano.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.yizu.intelligentpiano.R;

/**
 * Created by liuxiaozhu on 2017/9/28.
 * All Rights Reserved by YiZu
 */

public class ShowTimeDialog {
    private PopupWindow popupWindow;
    private Context mContext;
    private Activity activity;

    public ShowTimeDialog(Context context, Activity activity) {
        this.activity = activity;
        mContext = context;
    }

    /**
     * 剩余时间提示
     */
    public void showTimeView(final View footView) {
//        final LinearLayout views = (LinearLayout) findViewById(R.id.main_piano);
        LinearLayout view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.dialog_time, null);
        final WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        activity.getWindow().setAttributes(lp);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(footView, Gravity.TOP, 0, 114);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lp.alpha = 1f;
                activity.getWindow().setAttributes(lp);
            }
        });
    }

    public void closeDialog() {
        if (popupWindow == null) {
            return;
        }
        if (isShowing()) {
            popupWindow.dismiss();
        }
    }
    public boolean isShowing() {
        if (popupWindow == null) {
            return false;
        }
        return popupWindow.isShowing();
    }
}
