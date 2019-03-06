package com.yuto.codestatusdemo.base.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.yuto.codestatusdemo.base.bean.CodeStatus;

/**
 * Created by lvhongzhen on 2018/8/13.
 */

public abstract class BaseViewModel extends ViewModel {
    protected MutableLiveData<CodeStatus> mLDPageStatus = new MutableLiveData<>();

    public MutableLiveData<CodeStatus> getPageStatus() {
        return mLDPageStatus;
    }

    public void changePageStatus(CodeStatus pPageStatus) {
        mLDPageStatus.setValue(pPageStatus);
    }

    public void pageSuccess() {
        changePageStatus(CodeStatus.SUCCESS);
    }

    public void pageLoading() {
        changePageStatus(CodeStatus.LOADING);
    }
}
