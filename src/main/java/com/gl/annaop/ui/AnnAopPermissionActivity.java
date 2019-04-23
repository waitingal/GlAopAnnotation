package com.gl.annaop.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.WindowManager;
import com.gl.annaop.R;
import com.gl.annaop.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;
@RequiresApi(api = Build.VERSION_CODES.M)
public class AnnAopPermissionActivity extends FragmentActivity {

    private static String PERMISSION_ARRAY="permission_array";
    private static String SPECIA_PERMISSION_ARRAY="specia_permission_array";

    private static PermissionUtils.OnPermissionListener listener;

    private static int REQ_CODE_PERMISSION_REQUEST = 101;

    private static int REQUEST_CODE_ASK_SYSTEM_ALERT_WINDOW = 102;
    private static int REQUEST_CODE_ASK_WRITE_SETTINGS = 103;

    private String[] permissionArray;//需要申请的权限
    private String[] speciaPermissionArray;//特殊处理权限
    private List<String> deniedPermissionList = new ArrayList<>();//记录没有授权通过的权限


    private static void SetOnPermissionListener(PermissionUtils.OnPermissionListener permissionListener){
        listener = permissionListener;
    }

    public static void Open(Context c,PermissionUtils.OnPermissionListener mOnPermissionListener, List<String> Permissionlist,
                            List<String>speciaPermissionList){
        SetOnPermissionListener(mOnPermissionListener);
        Intent intent=new Intent(c,AnnAopPermissionActivity.class);
        intent.putExtra(PERMISSION_ARRAY,Permissionlist.toArray(new String[Permissionlist.size()]));
        intent.putExtra(SPECIA_PERMISSION_ARRAY,speciaPermissionList.toArray(new String[speciaPermissionList.size()]));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(intent);
        //((Activity)c).overridePendingTransition(0, 0);
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
        speciaPermissionArray =intent.getStringArrayExtra(SPECIA_PERMISSION_ARRAY);


//        if(permissionArray == null || permissionArray.length <=0){
//            if(listener!=null){
//                listener.onPermissionFail("");
//            }
//            Exit();
//            return;
//        }

        if(speciaPermissionArray != null && speciaPermissionArray.length >0){
            boolean systemAlertWindowPermissions = checkHaveSystemAlertWindow(speciaPermissionArray);
            Log.e("","权限申请---systemAlertWindowPermissions="+systemAlertWindowPermissions);
            if(systemAlertWindowPermissions){
                boolean writeSettingsPermissions = checkHaveWriteSettings(speciaPermissionArray);
                Log.e("","权限申请---writeSettingsPermissions="+writeSettingsPermissions);
                if(writeSettingsPermissions){
                    if(permissionArray!=null && permissionArray.length >0){
                        requestOtherPermissions(permissionArray);
                    }else{
                        permissionSuccess();
                    }
                }
            }
        }else{
            Log.e("","权限申请---没有特殊权限=");
            requestOtherPermissions(permissionArray);
        }


//        if(permissionArray!=null && permissionArray.length>0){
//            ActivityCompat.requestPermissions(AnnAopPermissionActivity.this,permissionArray, REQ_CODE_PERMISSION_REQUEST );
//        }
//        else{
//            if(listener!=null){
//                listener.onPermissionFail("");
//            }
//            Exit();
//        }
    }

    private boolean checkHaveSystemAlertWindow(String[] array){
        for (String item : array){
            if(item.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)){
                boolean flag = PermissionUtils.SpecialSystemAlertWindowWPermission(this,REQUEST_CODE_ASK_SYSTEM_ALERT_WINDOW);
                return flag;
            }
        }
        return true;
    }


    private boolean checkHaveWriteSettings(String[] array){
        for (String item : array){
            if(item.equals(Manifest.permission.WRITE_SETTINGS)){
                boolean flag = PermissionUtils.SpecialWriteSettingsPermission(this,REQUEST_CODE_ASK_WRITE_SETTINGS);
                return flag;
            }
        }
        return true;
    }

    private void requestOtherPermissions(String[] array){
        if(array == null || array.length <= 0){
            Log.e("","权限申请---requestOtherPermissions === 空");

        }
        ActivityCompat.requestPermissions(AnnAopPermissionActivity.this,array, REQ_CODE_PERMISSION_REQUEST );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_ASK_SYSTEM_ALERT_WINDOW){
            Log.e("","权限申请---REQUEST_CODE_ASK_SYSTEM_ALERT_WINDOW");
            onSystemAlertWindowResult();
        }else if(requestCode == REQUEST_CODE_ASK_WRITE_SETTINGS){
            Log.e("","权限申请---REQUEST_CODE_ASK_WRITE_SETTINGS");
            onWriteSettingsResult();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQ_CODE_PERMISSION_REQUEST){
           if(PermissionUtils.verifyPermissions(grantResults)){
               if(deniedPermissionList == null || deniedPermissionList.size() <= 0){
                   permissionSuccess();
               }else{
                   denidePermission2ExplainText(deniedPermissionList.toArray(new String[deniedPermissionList.size()]));
               }
           }else{
//               StringBuffer sb=new StringBuffer();
//               for (int i=0;i<grantResults.length;i++){
//                   if(grantResults[i] != PackageManager.PERMISSION_DENIED){
//                       String str = PermissionUtils.permissionExplain(AnnAopPermissionActivity.this,permissions[i]);
//                       if(str!=null && str.length()>1){
//                           sb.append("'"+str+"'");
//                       }
//                   }
//               }
//               if(deniedPermissionArray !=null && deniedPermissionArray.size() >0){
//                   for (String item:deniedPermissionArray){
//                       String str = PermissionUtils.permissionExplain(AnnAopPermissionActivity.this,item);
//                       if(str!=null && str.length()>1){
//                           sb.append("'"+str+"'");
//                       }
//                   }
//               }
//               if(listener!=null){
//                   listener.onPermissionFail(sb.toString());
//               }
//
//              // permissionLackTips(sb.toString());


           //    List<String> list = new ArrayList<>();
               for (int i=0;i<grantResults.length;i++){
                   if(i != PackageManager.PERMISSION_DENIED){
                       deniedPermissionList.add(permissions[i]);
                   }
               }
//               if(deniedPermissionArray !=null && deniedPermissionArray.size() >0){
//                   for (String s:deniedPermissionArray){
//                       list.add(s);
//                   }
//               }
               denidePermission2ExplainText(deniedPermissionList.toArray(new String[deniedPermissionList.size()]));
           }
        }
    }

    /**
     *
     */
    private void onSystemAlertWindowResult(){
        if(!Settings.canDrawOverlays(this)){
            deniedPermissionList.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
        }
        boolean flag = checkHaveWriteSettings(speciaPermissionArray);
        if(flag){
            if(permissionArray != null && permissionArray.length >0){
                requestOtherPermissions(permissionArray);
            }else{
                if(deniedPermissionList!=null && deniedPermissionList.size() > 0){
                    denidePermission2ExplainText(deniedPermissionList.toArray(new String[deniedPermissionList.size()]));
                }else{
                    permissionSuccess();
                }
            }
        }
    }

    /**
     *
     */
    private void onWriteSettingsResult(){
        if(!Settings.System.canWrite(this)){
            deniedPermissionList.add(Manifest.permission.WRITE_SETTINGS);
        }
        if(permissionArray != null && permissionArray.length >0){
            requestOtherPermissions(permissionArray);
        }else{
            if(deniedPermissionList!=null && deniedPermissionList.size() > 0){
                denidePermission2ExplainText(deniedPermissionList.toArray(new String[deniedPermissionList.size()]));
            }else{
                permissionSuccess();
            }
        }
    }

    private void denidePermission2ExplainText(String[] array){
        StringBuffer sb=new StringBuffer();
        for (String s:array){
            String str = PermissionUtils.permissionExplain(AnnAopPermissionActivity.this,s);
            if(str!=null && str.length()>1){
                sb.append("'"+str+"'");
            }
        }
        if(listener!=null){
            listener.onPermissionFail(sb.toString(),array);
        }
        permissionLackTips(sb.toString());
    }

    private void permissionSuccess(){
        if(listener!=null){
            listener.onPermissionSuccess();
        }
        Exit();
    }

    private void Exit(){
        finish();
        overridePendingTransition(0, 0);
    }


    private void permissionLackTips(String msg){
        String detailed_msg;
        if(msg==null || msg.length()<1){
            detailed_msg=getString(R.string.permission_detailed_tips_2);
        }else{
            String peizhi = getString(R.string.permission_detailed_tips);
            detailed_msg = String.format(peizhi,msg);
        }
        AlertDialog.Builder builder= new AlertDialog.Builder(this)
                .setTitle(this.getString(R.string.tips_title))
                .setMessage(detailed_msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AnnAopPermissionActivity.this.Exit();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
