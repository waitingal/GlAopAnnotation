package com.gl.annaop.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.WindowManager;
import com.gl.annaop.R;
import com.gl.annaop.utils.PermissionUtils;

import java.util.List;

public class AnnAopPermissionActivity extends FragmentActivity {

    private static String PERMISSION_ARRAY="permission_array";

    private static PermissionUtils.OnPermissionListener listener;

    private static int REQ_CODE_PERMISSION_REQUEST = 101;

    private  String[] permissionArray;

    private static void SetOnPermissionListener(PermissionUtils.OnPermissionListener permissionListener){
        listener = permissionListener;
    }

    public static void Open(Context c,PermissionUtils.OnPermissionListener mOnPermissionListener, List<String> Permissionlist){
        SetOnPermissionListener(mOnPermissionListener);
        Intent intent=new Intent(c,AnnAopPermissionActivity.class);
        intent.putExtra(PERMISSION_ARRAY,Permissionlist.toArray(new String[Permissionlist.size()]));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(intent);
        ((Activity)c).overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        setContentView(R.layout.permission_act);
       // setContentView(R.layout.activity_ann_aop_permission);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            setIntent(intent);
        }
        permissionArray=intent.getStringArrayExtra(PERMISSION_ARRAY);
        if(permissionArray!=null && permissionArray.length>0){
            ActivityCompat.requestPermissions(AnnAopPermissionActivity.this,permissionArray, REQ_CODE_PERMISSION_REQUEST );
        }else{
            if(listener!=null){
                listener.onPermissionFail("");
            }
            Exit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQ_CODE_PERMISSION_REQUEST){
           if(PermissionUtils.verifyPermissions(grantResults)){
                if(listener!=null){
                    listener.onPermissionSuccess();
                }
           }else{
               StringBuffer sb=new StringBuffer();
               for (int i=0;i<grantResults.length;i++){
                   if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                       String str = PermissionUtils.permissionExplain(AnnAopPermissionActivity.this,permissions[i]);
                       if(str!=null && str.length()>1){
                           sb.append("'"+str+"'");
                       }

                   }
               }
               if(listener!=null){
                   listener.onPermissionFail(sb.toString());
               }
           }
            Exit();
        }
    }

    private void Exit(){
        finish();
        overridePendingTransition(0, 0);
    }
}
