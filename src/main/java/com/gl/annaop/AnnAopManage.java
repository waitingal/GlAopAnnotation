package com.gl.annaop;

import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * Created by waiting on 2018/3/5.  //annotationProcessor
 */

public class AnnAopManage {

    public static Context context;
    private static AnnAopManage mInstance;

    public static AnnAopManage getInstance(Context c){
        if(mInstance == null){
            mInstance = new AnnAopManage();
            mInstance.init(c.getApplicationContext());
        }
        return mInstance;
    }

    public  void init(Context app) {
        if (app == null) {
            throw new IllegalArgumentException("application must not be null");
        }
        context = app;
    }

    public AnnAopManage setViewClickSpaceTime(long i){
        Config.ViewClickSpaceTime = i > 0 ? i : 0;
        return this;
    }

    public AnnAopManage setViewClickSpaceTips(String str){
        Config.ViewClickSpaceTips = str!=null ? str:"";
        return this;
    }

    public AnnAopManage setDefaultNetTips(String str){
        Config.defaultNetTips =  str!=null ? str:"";
        return this;
    }
}
