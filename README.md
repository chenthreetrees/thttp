# thttp

该框架使用retrofit2.0+rxAndroid进行封装，该框架主要特性：

> * 统一管理网络请求接口
> * 灵活的线程切换
> * 支持请求结果的自定义数据结构
> * 统一的异常拦截处理，对特殊异常也可以单独处理
> * 支持文件带进度下载
> * 支持自定义拦截器（log输出，头部参数处理等）
> * 提供请求等待的加载动画入口


## Retrofit中http POST/GET请求(结合了rx)
Retrofit中的网络请求都是通过注解方式的接口方法来表示的,此处只对常用的post和get请求进行说明，
Retrofit还提供有put，delete等请求方式可自己研究官方文档使用。

### post请求
Body对象作为post参数：
```
@POST("user/login")
Observable<User> login(@Body LoginInfo loginInfo);
```

Field方式(常用):
```
@FormUrlEncoded
@POST("user/login")
Observable<User> login(@Field("username") String username,
                 @Field(password) String password);
```

FieldMap方式:
```
@FormUrlEncoded
@POST("user/login")
Call<User> login(@FieldMap Map<String,String> map);
```

参数较多时建议用Body方式和FieldMap方式

### get请求
直接请求某一地址获取列表:
```
@GET("order/orderList")
Observable<List<Order>> getOrderList();
```

url拼接固定查询条件:
```
@GET("order/orderList?id=2130")
Observable<List<Order>> getOrderList();
```

url中拼接地址信息:
```
@GET("order/{city}/orderList")
Observable<List<Order>> getOrderList(@Path("city") String city);
```

通过Query注解添加其他查询条件:
```
@GET("order/{city}/orderList")
Observable<List<Order>> getOrderList(@Path("city") String city
									@Query("date") String date
                                    @Query("type") String type);
```

查询条件较多时同样有QueryMap注解方法供使用:
```
@GET("order/{city}/orderList")
Observable<List<Order>> getOrderList(@Path("city") String city
									@QueryMap<String, String> options);
```

### Header请求头设置
为请求添加固定请求头:
```
//添加单个固定请求头
@Header("Cache-Control: max-aget-640000")
@GET("order/orderList")
Observable<List<Order>> getOrderList();

//多个请求头以数组的形式提交
@Header(
    {"Accept: application/json",
    "User-Agent: Retrofit-Sample-App"
    })
@GET("order/orderList")
Observable<List<Order>> getOrderList();
```

动态添加请求头:
```
@GET("order/orderList")
Observable<List<Order>> getOrderList(@Header(Authorization) String authorization);
```

上面的两种添加请求头的方法作用范围只是添加注解的单个方法，
如果为每个请求都添加头部，可以通过使用拦截器来实现,如下：
```
public class HttpInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Request request = chain.request()
                .newBuilder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("X-SIGN", "5df4gfwe5gag5s5g55a")
                .addHeader("X-VERSION", ""+ BuildConfig.VERSION_CODE)
                .addHeader("X-MECHINE", "2")
                .addHeader("MEMBERID", "1")
                .build();
        Response response = chain.proceed(request);
        return response;
    }
}
```

## 简单使用

### 初始化
```
	RetrofitManager.getInstence()
                .baseUrl("your baseUrl")
                .addInterceptor(new HttpInterceptor())
                .serviceClass(ApiService.class)
                .create();
```

### 业务请求
具体的业务请求类继承HttpPresenter，传入一些配置参数：
```
public class DocPresenter extends HttpPresenter {
    private ApiService mApiService;
    public DocPresenter(ILoadingView iLoadingView)
    {
        super(iLoadingView);
        mApiService = (ApiService)RetrofitManager.getInstence().getRetrofitService();
    }

    /**
     * 网络请求的具体实现
     */
    public void getDoc()
    {
        Observable observable = mApiService.getDoc(1,4);//调用接口
        subscribeHttp(observable, new IHttpResultListener<String>() {
            @Override
            public void onSuccess(String s)
            {
                //请求成功，判断下页面是否还是active，防止因为页面被回收而出现空指针异常
                if(mLoadingView.isActive())
                    mLoadingView.toast(0,s);
            }

            @Override
            public boolean onError(int i, String s)
            {
                //错误或者异常处理，如果要拦截则返回true，否则框架统一处理
                return false;
            }
        });
    }
}
```

在需要使用的地方调用：
```
DocPresenter docPresenter = new DocPresenter(new ILoadingView() {
            @Override
            public void showLoading(boolean isCancel)
            {
				//显示加载框
            }

            @Override
            public void dismissLoading()
            {
				//隐藏加载框
            }

            @Override
            public boolean isActive()
            {
			//页面是否激活，建议在页面创建的时候返回true，在页面关闭的时候返回false
                return true;
            }

        });
```

上面的代码看起来还是不够简洁，建议根据业务需求进一步封装基类

### 文件下载
```
                DownLoadPresenter presenter = new DownLoadPresenter(new IProgressView() {
                    @Override
                    public void start()
                    {
                        
                    }

                    @Override
                    public void error(int i, String s)
                    {

                    }

                    @Override
                    public void progress(long progress, long total, float speed)
                    {

                    }

                    @Override
                    public void success()
                    {

                    } 
                });
                presenter.setFileDir(FileUtil.ROOT_PATH);
                presenter.setDestFileName(FileUtil.NAME);
                presenter.download("http://upload.cbg.cn/2016/0726/1469533389366.jpg");
```

**注意**：记得在页面关闭的时候调用HttpPresenter的destroy()

## 更多设置

如果需要在异常处理之前，做一些特殊的处理，可以通过重写以下函数：
```
    protected boolean handleError(ApiException ex)
    {
        return false;//若拦截了返回true
    }
```

异常统一处理，需要重写该函数：
```
    protected void showError(ApiException ex)
    {

    }
```

此框架默认的请求结果的数据结构，默认采用如下：
```
public class Result<T> {
    public boolean success;
    public String errorMessage;
    public int errorCode;
    public T data;
}
```

如果和该结构不同，可以通过重写以下函数：
```
    protected <T>Observable mapResult(Observable observable)
    {
        return observable.map(new Function<Result<T>, T>() {
            @Override
            public T apply(@NonNull Result<T> result) throws Exception
            {
				//处理请求数据，如果不成功，则通过service异常来处理 
                if (!result.success) {
                    throw new ServerException(result.errorCode, result.errorMessage);
                }
                return result.data;
            }
        });
    }
```

其他的设置或者修改，请参考demo和源码