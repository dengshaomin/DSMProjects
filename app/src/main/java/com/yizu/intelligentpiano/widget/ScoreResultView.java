package com.yizu.intelligentpiano.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yizu.intelligentpiano.R;
import com.yizu.intelligentpiano.constens.IDialog;

/**
 * Created by dengshaomin on 2017/10/30.
 */

public class ScoreResultView extends LinearLayout {
    private View rootView;
    private TextView scoreView, score_again, score_exit;
    private ImageView score_img;
    private IDialog iDialog;

    public IDialog getiDialog() {
        return iDialog;
    }

    public void setiDialog(IDialog iDialog) {
        this.iDialog = iDialog;
    }

    public ScoreResultView(Context context) {
        this(context, null);
    }

    public ScoreResultView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScoreResultView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_score, this);
        initView();
    }

    private void initView() {
        scoreView = rootView.findViewById(R.id.score_score);
        score_again = rootView.findViewById(R.id.score_again);
        score_exit = rootView.findViewById(R.id.score_exit);
        score_img = rootView.findViewById(R.id.score_img);
        score_again.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iDialog != null) {
                    iDialog.sure();
                }
            }
        });
        score_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iDialog != null) {
                    iDialog.cancel();
                }
            }
        });
    }

    public void setViewData(int score) {
        boolean isGood = score > 90;
        score_img.setBackgroundResource(isGood ? R.mipmap.good : R.mipmap.bad);
        scoreView.setBackgroundResource(isGood ? R.mipmap.score_good : R.mipmap.score_bad);
        scoreView.setText(score + "");
    }

}
