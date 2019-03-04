package org.hhu.application;

import android.app.Application;
import android.content.Intent;

import com.baidu.mapapi.SDKInitializer;
import com.zhy.http.okhttp.OkHttpUtils;

import org.hhu.surface.RecordService;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;


public class MyApplication extends Application {

    @Override
    public void onCreate()
    {
        super.onCreate();

        SDKInitializer.initialize(getApplicationContext());

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                  .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                  .readTimeout(10000L, TimeUnit.MILLISECONDS)
                  //其他配置
                 .build();

        OkHttpUtils.initClient(okHttpClient);

        // 启动 Marvel service
        startService(new Intent(this, RecordService.class));

    }
}
