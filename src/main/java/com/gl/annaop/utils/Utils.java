package com.gl.annaop.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.gl.annaop.NetType;

/**
 * Created by waiting on 2018/3/5.
 */

public class Utils {

    public static Context GetActContext(Object object){
        if(object == null){
            return null;
        }
        if (object instanceof Activity) {
            return (Activity) object;
        } else if (object instanceof android.app.Fragment) {
            android.app.Fragment fragment = (android.app.Fragment) object;
            return fragment.getActivity();
        }else if(object instanceof android.support.v4.app.Fragment){
            android.support.v4.app.Fragment fragment = (android.support.v4.app.Fragment) object;
            return fragment.getActivity();
        }else{
            return null;
        }
    }

    public static Context GetContext(Object object){
        if(object == null){
            return null;
        }
        if (object instanceof Activity) {
            return (Activity) object;
        } else if (object instanceof android.app.Fragment) {
            android.app.Fragment fragment = (android.app.Fragment) object;
            return fragment.getActivity();
        }else if(object instanceof android.support.v4.app.Fragment){
            android.support.v4.app.Fragment fragment = (android.support.v4.app.Fragment) object;
            return fragment.getActivity();
        } else if (object instanceof View) {
            View view = (View) object;
            return view.getContext();
        } else if (object instanceof Service) {
            return (Service)object;
        }
        return null;
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static NetType getConnectedType(Context context) {
        NetworkInfo net = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (net != null && net.isAvailable()) {
            switch (net.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return NetType.Wifi;
                case ConnectivityManager.TYPE_MOBILE:
                    return NetType.Mobile;
//                default:
//                    return NetType.All;
            }
        }
        return NetType.None;

    }

}
