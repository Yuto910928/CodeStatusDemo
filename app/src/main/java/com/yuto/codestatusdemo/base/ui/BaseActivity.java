package com.yuto.codestatusdemo.base.ui;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.yuto.codestatusdemo.base.bean.CodeStatus;
import com.yuto.codestatusdemo.base.viewmodel.BaseViewModel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseActivity<T extends BaseViewModel> extends AppCompatActivity {
    protected T viewModel;
    protected Dialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = LayoutInflater.from(this).inflate(getContentViewId(), null, false);
        setContentView(rootView);
        Type t = getClass().getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            viewModel = ViewModelProviders.of(this).get((Class<T>) ((ParameterizedType) t).getActualTypeArguments()[0]);
            viewModel.getPageStatus().observe(this, new Observer<CodeStatus>() {
                @Override
                public void onChanged(@Nullable CodeStatus pPageStatus) {
                    //此部分可以根据业务需求进行调整
                    switch (pPageStatus.getCode()) {
                        case CodeStatus.CODE_LOADING:
                            Toast.makeText(BaseActivity.this, "页面加载中", Toast.LENGTH_SHORT);
                            break;
                        case CodeStatus.CODE_SUCCESS:
                            Toast.makeText(BaseActivity.this, "页面加载成功", Toast.LENGTH_SHORT);
                            break;
                        case CodeStatus.CODE_DIALOG_LOADING_SHOW:
                            showLoadingDialog();
                            break;
                        case CodeStatus.CODE_DIALOG_LOADING_HIDE:
                            hideLoadingDialog();
                            break;
                        case CodeStatus.CODE_TOAST:
                            Toast.makeText(BaseActivity.this, pPageStatus.getMessage(), Toast.LENGTH_SHORT);
                            break;
                        case CodeStatus.CODE_PERMISSION:
                            Toast.makeText(BaseActivity.this, "页面暂无权限", Toast.LENGTH_SHORT);
                            break;
                        case CodeStatus.CODE_TOKEN:
                            Toast.makeText(BaseActivity.this, "token失效请重新登录", Toast.LENGTH_SHORT);
                            break;
                        case CodeStatus.CODE_ERROR_HTTP:
                            Toast.makeText(BaseActivity.this, "网络异常请重试", Toast.LENGTH_SHORT);
                            break;
                        case CodeStatus.CODE_ERROR_OTHER:
                            Toast.makeText(BaseActivity.this, "服务器异常请重试", Toast.LENGTH_SHORT);
                            break;

                    }
                }
            });
        } else {
            throw new RuntimeException("BaseActivity:onCreate:这个不是ParameterizedType类型");
        }
        initView();
        viewModel.pageLoading();
        bindData();
    }

    protected Dialog getLoadingDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(this, true);
        }
        return mDialog;
    }

    protected void hideLoadingDialog() {
        if (getLoadingDialog().isShowing()) {
            getLoadingDialog().dismiss();
        }
    }

    protected void showLoadingDialog() {
        if (!getLoadingDialog().isShowing()) {
            getLoadingDialog().show();
        }
    }
    //布局文件的id
    protected abstract int getContentViewId();

    //初始化页面与数据无关
    protected abstract void initView();

    //获取数据绑定页面
    protected abstract void bindData();
}
