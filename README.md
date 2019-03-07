# Android架构的思考与封装（如何将状态码与页面变化相对应）
## 前言
在APP的开发过程中，我们会经常会遇到各种各样的状态码与页面变化。
状态码：服务器报错的状态码（404、502），服务器自定义的状态码（token失效，无权限访问）等。
页面的变化：数据出错，数据为空，没有权限、弹Toast、显示正在加载的loding等。
如果每个页面状态，每种状态码都需要我们在每个页面中处理，我们的工作量会非常大，如何合理封装，高效开发呢，希望看完本篇的方法会对你有所帮助。
## 框架介绍
本文将使用的框架有Retrofit+Rxjava2+ViewModel+LiveData+Activity/Fragment
## Rxjava封装
服务器返回的json格式
```
{
  "code": 2000,//服务器返回的状态码
  "message": "ok",//服务器返回的状态码描述，如2000（请求成功）、3000（token失效），3001（无权限）
  "data": {}//服务器请求成功后的数据
 }
```
json对象的基类封装
```
public class BaseBean<T> {
    private int code;
    private String message;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int pCode) {
        code = pCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String pMessage) {
        message = pMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T pData) {
        data = pData;
    }
}
```
状态码的Bean类封装
```
public class CodeStatus {
    public static final int CODE_SUCCESS = 1;//请求成功
    public static final int CODE_LOADING = 0;//正在请求
    public static final int CODE_DIALOG_LOADING_SHOW = 2;//表单提交显示dialog
    public static final int CODE_DIALOG_LOADING_HIDE = 3;//表单提交返回结构隐藏dialog
    public static final int CODE_TOAST = 4;//提示

    public static final int CODE_TOKEN = 3001;//token过期
    public static final int CODE_PERMISSION = 2002;//无权限
    public static final int CODE_ERROR_HTTP = -1;//HTTP请求出错404，503
    public static final int CODE_ERROR_OTHER = -2;//其他异常json转换异常等
    /**
     * 先将常用的不需要描述的状态码封装好
     */
    public static final CodeStatus LOADING=new CodeStatus(0);
    public static final CodeStatus SUCCESS=new CodeStatus(1);
    public static final CodeStatus DIALOG_SHOW=new CodeStatus(2);
    public static final CodeStatus DIALOG_HIDE=new CodeStatus(3);
    protected int code;
    protected String message;

    public CodeStatus(int pCode) {
        code = pCode;
    }

    public CodeStatus(int pCode, String pMessage) {
        code = pCode;
        message = pMessage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int pCode) {
        code = pCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String pMessage) {
        message = pMessage;
    }
}
```
状态类仅仅是封装了状态码与描述，如果有其他需求可以继续扩展

接下来我们封装Rxjava，上代码
```
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
```
基类只是处理了异常情况,当请求出现异常情况，会将状态码通过mCodeStatus通知给请求的界面，进行异常的统一处理。
除了基类封装我们还可以简单的封装一下loading的Observer，例如当我们提交表单时需要弹出loading，而在结果返回之后，关闭loading
```
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
```
## ViewModel+LiveData封装
ViewModel 将视图的数据和逻辑从具有生命周期特性的实体（如 Activity 和 Fragment）中剥离开来。直到关联的 Activity 或 Fragment 完全销毁时，ViewModel 才会随之消失，也就是说，即使在旋转屏幕导致 Fragment 被重新创建等事件中，视图数据依旧会被保留。ViewModels 不仅消除了常见的生命周期问题，而且可以帮助构建更为模块化、更方便测试的用户界面。
LiveData 是一个可以感知 Activity 、Fragment生命周期的数据容器。当 LiveData 所持有的数据改变时，它会通知相应的界面代码进行更新。同时，LiveData 持有界面代码 Lifecycle 的引用，这意味着它会在界面代码（LifecycleOwner）的生命周期处于 started 或 resumed 时作出相应更新，而在 LifecycleOwner 被销毁时停止更新。
viewmodel的封装相对简单很多，只需要给activity/fragment提供LiveData即可
```
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

```
## Activity与Fragment的封装
首先是Activity
~~~
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
~~~
Fragment的封装和Activity相同
~~~
public abstract class BaseFragment<T extends BaseViewModel> extends Fragment {
    protected T viewModel;
    protected Dialog mDialog;
    protected View mRootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getContentViewId(), container, false);
        Type t = getClass().getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            viewModel = ViewModelProviders.of(this).get((Class<T>) ((ParameterizedType) t).getActualTypeArguments()[0]);
            viewModel.getPageStatus().observe(this, new Observer<CodeStatus>() {
                @Override
                public void onChanged(@Nullable CodeStatus pPageStatus) {
                    //此部分可以根据业务需求进行调整
                    switch (pPageStatus.getCode()) {
                        case CodeStatus.CODE_LOADING:
                            Toast.makeText(getActivity(), "页面加载中", Toast.LENGTH_SHORT);
                            break;
                        case CodeStatus.CODE_SUCCESS:
                            Toast.makeText(getActivity(), "页面加载成功", Toast.LENGTH_SHORT);
                            break;
                        case CodeStatus.CODE_DIALOG_LOADING_SHOW:
                            showLoadingDialog();
                            break;
                        case CodeStatus.CODE_DIALOG_LOADING_HIDE:
                            hideLoadingDialog();
                            break;
                        case CodeStatus.CODE_TOAST:
                            Toast.makeText(getActivity(), pPageStatus.getMessage(), Toast.LENGTH_SHORT);
                            break;
                        case CodeStatus.CODE_PERMISSION:
                            Toast.makeText(getActivity(), "页面暂无权限", Toast.LENGTH_SHORT);
                            break;
                        case CodeStatus.CODE_TOKEN:
                            Toast.makeText(getActivity(), "token失效请重新登录", Toast.LENGTH_SHORT);
                            break;
                        case CodeStatus.CODE_ERROR_HTTP:
                            Toast.makeText(getActivity(), "网络异常请重试", Toast.LENGTH_SHORT);
                            break;
                        case CodeStatus.CODE_ERROR_OTHER:
                            Toast.makeText(getActivity(), "服务器异常请重试", Toast.LENGTH_SHORT);
                            break;

                    }
                }
            });
        } else {
            throw new RuntimeException("BaseFragment:onCreateView:这个不是ParameterizedType类型");
        }
        initView();
        viewModel.pageLoading();
        bindData();
        return mRootView;
    }

    protected Dialog getLoadingDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(getActivity(), true);
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
~~~
至此我们的封装已经算基本完成，剩下的就是子类继承父类，渲染各自ui即可，当页面需要改变状态时只需要调用viewmodel的changestatus方法并传递状态码即可。



