package com.yizu.intelligentpiano.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.constens.IFinish;
import com.yizu.intelligentpiano.utils.MyLogUtils;
import com.yizu.intelligentpiano.view.MainActivity;

import static android.content.ContentValues.TAG;

/**
 * Author：Created by liuxiaozhu on 2018/1/22.
 * Email: chenhuixueba@163.com
 */

public class TimeDialog {
    private AlertDialog dialog;

    public TimeDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.time, null);
        builder.setView(view);
        dialog = builder.create();
        final Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(0));
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    dissmiss();
                    return true;
                }
                return false;
            }
        });
//        dialog.setView(view);
    }

    public void show() {
        if (!isShowing())dialog.show();
    }

    public void dissmiss() {
        if (isShowing())dialog.dismiss();
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }
}
