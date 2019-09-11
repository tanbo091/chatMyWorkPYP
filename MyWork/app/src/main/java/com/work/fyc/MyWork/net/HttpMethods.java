package com.work.fyc.MyWork.net;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HttpMethods {
    private final static int DEFAULT_TIMEOUT = 180;

    private ApiService apiService;

    private static HttpMethods methods = null;

    public static HttpMethods getInstance() {
        if (methods == null) methods = new HttpMethods();
        return methods;
    }

    public HttpMethods() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);


        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(HttpPath.url)
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public ApiService getApiService() {
        return apiService;
    }

    // 添加线程管理并订阅 rxJava
    public <T> void toSubscribe(Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    // 添加线程管理并订阅 rxJava
    public <T> void requestSubscribe(Observable<T> o, final Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<T>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        s.onStart();
                    }

                    @Override
                    public void onCompleted() {
                        s.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        s.onError(e);
                    }

                    @Override
                    public void onNext(T t) {
                        s.onNext(t);
                    }
                });
    }
}
