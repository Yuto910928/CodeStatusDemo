package com.yuto.codestatusdemo;

import com.yuto.codestatusdemo.base.bean.BaseBean;
import com.yuto.codestatusdemo.base.rx.BaseObserver;
import com.yuto.codestatusdemo.base.rx.LoadingObserver;
import com.yuto.codestatusdemo.base.viewmodel.BaseViewModel;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends BaseViewModel {
    public void post() {
        BaseBean baseBean = new BaseBean();
        baseBean.setCode(2000);
        baseBean.setMessage("提交成功");
        valueObservable(baseBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LoadingObserver(getCodeStatus()) {

                    @Override
                    public void onDataSuccess(BaseBean pt) {

                    }

                    @Override
                    public void onDataFail(BaseBean pt) {

                    }

                });
    }

    public void token() {
        BaseBean baseBean = new BaseBean();
        baseBean.setCode(3001);
        baseBean.setMessage("token失效");
        valueObservable(baseBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LoadingObserver(getCodeStatus()) {

                    @Override
                    public void onDataSuccess(BaseBean pt) {

                    }

                    @Override
                    public void onDataFail(BaseBean pt) {

                    }

                });
    }

    public void permission() {
        BaseBean baseBean = new BaseBean();
        baseBean.setCode(2002);
        baseBean.setMessage("没有权限");
        valueObservable(baseBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LoadingObserver(getCodeStatus()) {

                    @Override
                    public void onDataSuccess(BaseBean pt) {

                    }

                    @Override
                    public void onDataFail(BaseBean pt) {

                    }

                });
    }

    public Observable<BaseBean<Object>> valueObservable(final BaseBean pBean) {

        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                Thread.sleep(3000);
                emitter.onNext(pBean);
                emitter.onComplete();
            }
        });
    }
}
