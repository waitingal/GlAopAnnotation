package com.gl.annaop.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.gl.annaop.R;
import com.gl.annaop.ui.AnnAopPermissionActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by waiting on 2018/3/9.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class PermissionUtils {
//    private static OnPermissionListener mOnPermissionListener;
//    private static int mRequestCode = -1;
    public interface OnPermissionListener {
        void onPermissionSuccess();

        /**
         *
         * @param explain  说明缺少的权限所对应中文说明
         */
        void onPermissionFail(String explain,String[] deniedPermission);
    }

    /**
     * 业务接受权限请求结果回调
     * code: PermissionAnnotation注解的RequestCode值，默认为-1，不回调
     * result：权限请求结果
     */
    public interface PermissionRequestCallBack{
        void PermissionResult(int code,boolean result,String[] deniedPermission);
    }

    /**
     * 需要特殊处理的权限
     * @param permissionarray
     * @return
     */
    public static List<String> IsHaveSpecialPermission(String[] permissionarray){
        List<String> speciaPermissionList = new ArrayList<>();
        for (String s:permissionarray){
            if(s.equals(Manifest.permission.SYSTEM_ALERT_WINDOW) ||
                    s.equals(Manifest.permission.WRITE_SETTINGS)){
                speciaPermissionList.add(s);
            }
        }
        return speciaPermissionList;
    }

    public static List<String> DeniedPermission(Context context ,String[] permissionarray){
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            ArrayList<String> requests=new ArrayList<>();
            for (String s:permissionarray){
                if(s.equals(Manifest.permission.SYSTEM_ALERT_WINDOW) ||
                        s.equals(Manifest.permission.WRITE_SETTINGS)){
                    continue;
                }else{
                    int result = ContextCompat.checkSelfPermission(context, s);
                    if (result != PackageManager.PERMISSION_GRANTED){
                        requests.add(s);
                    }
                }

            }
            return requests;
        }
        return null;
    }


    public static void DoRequestPermission(Context c, List<String> Permissionlist,List<String>speciaPermissionList,
                                           OnPermissionListener listener){
        AnnAopPermissionActivity.Open(c,listener,Permissionlist,speciaPermissionList);
    }

    /**
     * 是否有 Manifest.permission.WRITE_SETTINGS 权限
     * @param act
     * @param REQUEST_CODE_ASK_WRITE_SETTINGS
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.M)
    public static boolean SpecialWriteSettingsPermission(Activity act, int REQUEST_CODE_ASK_WRITE_SETTINGS){
        if(!Settings.System.canWrite(act)){
            Log.e("","权限申请---package ="+ act.getPackageName());
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,Uri.parse("package:" + act.getPackageName()));
            act.startActivityForResult(intent, REQUEST_CODE_ASK_WRITE_SETTINGS);
            return false;
        }else{
            return true;
        }
    }

    /**
     * 是否有 Manifest.permission.SYSTEM_ALERT_WINDOW 权限
     * @param act
     * @param REQUEST_CODE_ASK_SYSTEM_ALERT_WINDOW
     * @return
     */
   @RequiresApi(api = Build.VERSION_CODES.M)
   public static boolean SpecialSystemAlertWindowWPermission(Activity act, int REQUEST_CODE_ASK_SYSTEM_ALERT_WINDOW){
       if (!Settings.canDrawOverlays(act)) {
           Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + act.getPackageName()));
           act.startActivityForResult(intent,REQUEST_CODE_ASK_SYSTEM_ALERT_WINDOW);
           return false;
       }else{
           return true;
       }
   }

    /**
     * 验证权限是否都已经授权
     */
    public static boolean verifyPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 显示提示对话框
     * msg:权限说明
     */
    public static void showTipsDialog(Context context,String msg) {
        try {
            String detailed_msg;
            if(msg==null || msg.length()<1){
                detailed_msg=context.getString(R.string.permission_detailed_tips_2);
            }else{
                String peizhi = context.getString(R.string.permission_detailed_tips);
                detailed_msg = String.format(peizhi,msg);
            }

             AlertDialog.Builder builder= new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.tips_title))
                    .setMessage(detailed_msg)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                 if(Build.VERSION.SDK_INT > 24){
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
                     if(Build.VERSION.SDK_INT >= 26){
                         dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                     }
                    }else {
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                 }
            } else {
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
            }
            dialog.show();
        }catch (Exception e){
            Log.e("","showTipsDialog--"+e.toString());
        }
    }

    /**
     * 权限对应的中文说明
     * @return
     */
    public static String permissionExplain(Context c,String permission){
        if(c==null && TextUtils.isEmpty(permission)){
            return "";
        }
        String explain="";
        switch (permission){
            case Manifest.permission.READ_CALENDAR:
            case Manifest.permission.WRITE_CALENDAR:
                explain=c.getString(R.string.permission_hint_calendar);
                break;
            case Manifest.permission.CAMERA:
                explain=c.getString(R.string.permission_hint_camera);
                break;
            case Manifest.permission.READ_CONTACTS:
            case Manifest.permission.WRITE_CONTACTS:
            case Manifest.permission.GET_ACCOUNTS:
                explain=c.getString(R.string.permission_hint_contacts);
                break;
            case Manifest.permission.ACCESS_FINE_LOCATION:
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                explain=c.getString(R.string.permission_hint_location);
                break;
            case Manifest.permission.RECORD_AUDIO:
                explain=c.getString(R.string.permission_hint_microphone);
                break;
            case Manifest.permission.READ_PHONE_STATE:
            case Manifest.permission.CALL_PHONE:
            case Manifest.permission.READ_CALL_LOG:
            case Manifest.permission.WRITE_CALL_LOG:
            case Manifest.permission.ADD_VOICEMAIL:
            case Manifest.permission.USE_SIP:
            case Manifest.permission.PROCESS_OUTGOING_CALLS:
                explain=c.getString(R.string.permission_hint_phone);
                break;
            case Manifest.permission.BODY_SENSORS:
                explain=c.getString(R.string.permission_hint_sensors);
                break;
            case Manifest.permission.SEND_SMS:
            case Manifest.permission.READ_SMS:
            case Manifest.permission.RECEIVE_WAP_PUSH:
            case Manifest.permission.RECEIVE_SMS:
            case Manifest.permission.RECEIVE_MMS:
                explain=c.getString(R.string.permission_hint_sms);
                break;
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                explain=c.getString(R.string.permission_hint_storage);
                break;
            case Manifest.permission.WRITE_SETTINGS:
                explain=c.getString(R.string.permission_hint_write_setting);
                break;
            case Manifest.permission.SYSTEM_ALERT_WINDOW:
                explain=c.getString(R.string.permission_hint_sys_alert_window);
                break;
        }
        return explain;

    }

}
