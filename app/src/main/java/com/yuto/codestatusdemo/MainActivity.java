package com.yuto.codestatusdemo;

import android.view.View;

import com.yuto.codestatusdemo.base.bean.CodeStatus;
import com.yuto.codestatusdemo.base.ui.BaseActivity;

public class MainActivity extends BaseActivity<MainViewModel> implements View.OnClickListener {
    private View text1;
    private View text2;
    private View text3;
    private View text4;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        text1 = findViewById(R.id.tv_text);
        text2 = findViewById(R.id.tv_text2);
        text3 = findViewById(R.id.tv_text3);
        text4 = findViewById(R.id.tv_text4);
        text1.setOnClickListener(this);
        text2.setOnClickListener(this);
        text3.setOnClickListener(this);
        text4.setOnClickListener(this);
    }

    @Override
    protected void bindData() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_text:
                viewModel.changeCodeStatus(new CodeStatus(CodeStatus.CODE_ERROR_HTTP,"http error 404"));
                break;
            case R.id.tv_text2:
                viewModel.post();
                break;
            case R.id.tv_text3:
                viewModel.permission();
                break;
            case R.id.tv_text4:
                viewModel.token();
                break;

        }
    }
}
