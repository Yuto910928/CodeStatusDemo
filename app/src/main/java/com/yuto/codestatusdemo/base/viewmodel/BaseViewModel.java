package com.yuto.codestatusdemo.base.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.yuto.codestatusdemo.base.bean.CodeStatus;

/**
 * Created by lvhongzhen on 2018/8/13.
 */

public abstract class BaseViewModel extends ViewModel {
    protected MutableLiveData<CodeStatus> mLDPageStatus = new MutableLiveData<>();

    public MutableLiveData<CodeStatus> getCodeStatus() {
        return mLDPageStatus;
    }

    public void changeCodeStatus(CodeStatus pPageStatus) {
        mLDPageStatus.setValue(pPageStatus);
    }

    public void pageSuccess() {
        changeCodeStatus(CodeStatus.SUCCESS);
    }

    public void pageLoading() {
        changeCodeStatus(CodeStatus.LOADING);
    }
}
