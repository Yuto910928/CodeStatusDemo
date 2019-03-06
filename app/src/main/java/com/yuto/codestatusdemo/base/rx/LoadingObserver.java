package com.yuto.codestatusdemo.base.rx;

import android.arch.lifecycle.MutableLiveData;

import com.yuto.codestatusdemo.base.bean.BaseBean;
import com.yuto.codestatusdemo.base.bean.CodeStatus;

import io.reactivex.disposables.Disposable;

public abstract class LoadingObserver<T extends BaseBean<E>, E> extends BaseObserver<T, E> {

    public LoadingObserver(MutableLiveData<CodeStatus> pCodeStatus) {
        super(pCodeStatus);
        if (mCodeStatus != null) {
            mCodeStatus.setValue(CodeStatus.DIALOG_SHOW);
        }
    }

    @Override
    public void onNext(T pT) {
        if (mCodeStatus != null) {
            mCodeStatus.setValue(CodeStatus.DIALOG_HIDE);
        }
        super.onNext(pT);
    }

    @Override
    public void onError(Throwable e) {
        if (mCodeStatus != null) {
            mCodeStatus.setValue(CodeStatus.DIALOG_HIDE);
        }
        super.onError(e);
    }
}
