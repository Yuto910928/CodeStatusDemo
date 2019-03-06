package com.yuto.codestatusdemo.base.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.yuto.codestatusdemo.R;

/**
 * ProgressDialog
 * Created by lvhongzhen on 2018/3/22.
 */

public class ProgressDialog extends Dialog {
    private AnimationDrawable mAnimation;

    public ProgressDialog(@NonNull Context pContext, boolean pCanCancel){
        super(pContext, R.style.Dialog_NoNeedBg);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(pCanCancel);
        setCanceledOnTouchOutside(false);
        if (!pCanCancel)
            setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_loadding_view);
        ImageView imageView = findViewById(R.id.iv_dialog_progress);
        mAnimation = (AnimationDrawable) imageView.getDrawable();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAnimation.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAnimation.stop();
    }

}
