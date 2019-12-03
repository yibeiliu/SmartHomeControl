package com.smartlab;

import android.app.Application;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import static com.smartlab.Utils.WxShareUtils.APP_ID;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        IWXAPI mWxApi = WXAPIFactory.createWXAPI(this, APP_ID, true);
// 注册
        mWxApi.registerApp(APP_ID);
        ZXingLibrary.initDisplayOpinion(this);
    }
}
