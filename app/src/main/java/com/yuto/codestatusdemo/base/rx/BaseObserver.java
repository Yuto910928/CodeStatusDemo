package com.yuto.codestatusdemo.base.rx;

import android.arch.lifecycle.MutableLiveData;

import com.yuto.codestatusdemo.base.bean.BaseBean;
import com.yuto.codestatusdemo.base.bean.CodeStatus;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

public abstract class BaseObserver<T extends BaseBean<E>, E> implements Observer<T> {
    MutableLiveData<CodeStatus> mCodeStatus;

    public BaseObserver(MutableLiveData<CodeStatus> pCodeStatus) {
        mCodeStatus = pCodeStatus;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T pT) {
        int code = pT.getCode();
        if (code == 2000) {
            //服务器返回成功
            onDataSuccess(pT);
        } else {
            //异常数据，将状态码发送给页面处理
            if (mCodeStatus != null) {
                mCodeStatus.setValue(new CodeStatus(code,pT.getMessage()));
            }
            onDataFail(pT);
        }
    }

    @Override
    public void onError(Throwable e) {
        CodeStatus codeStatus = transformThrowable(e);
        if (mCodeStatus != null) {
            //主线程调用set
            mCodeStatus.setValue(codeStatus);
            //异步调用post
            //mCodeStatus.postValue(codeStatus);
        }
    }

    @Override
    public void onComplete() {

    }

    public abstract void onDataSuccess(T pt);

    public abstract void onDataFail(T pt);

    protected CodeStatus transformThrowable(Throwable e) {
        CodeStatus error;
        if (e instanceof HttpException) {
            error = new CodeStatus(CodeStatus.CODE_ERROR_HTTP, e.getMessage());
        } else {
            error = new CodeStatus(CodeStatus.CODE_ERROR_OTHER, e.getMessage());
        }
        return error;
    }
}
