package com.gl.annaop.aopinject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Keep;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.gl.annaop.AnnAopManage;
import com.gl.annaop.Config;
import com.gl.annaop.annotation.PermissionAnnotation;
import com.gl.annaop.utils.PermissionUtils;
import com.gl.annaop.utils.Utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by waiting on 2018/3/9.
 */
@Aspect
public class PermissionInject {

    @Pointcut("execution(@com.gl.annaop.annotation.PermissionAnnotation * *(..))")
    public void executionCheckPermission(){
    }

    @Around("executionCheckPermission()")
    public void doCheckPermissionInject(final ProceedingJoinPoint joinPoint) throws Throwable{
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            joinPoint.proceed();
            return  ;
        }else{
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        PermissionAnnotation annotation =method.getAnnotation(PermissionAnnotation.class);
        final String[] permission = annotation.CheckPermission();
        final int requestCode = annotation.RequestCode();
        if(permission == null || permission.length<=0){
            joinPoint.proceed();
            return  ;
        }
        final Object object = joinPoint.getThis();
        final Context context =Utils.GetActContext(object) != null ?
                Utils.GetActContext(joinPoint.getThis()):AnnAopManage.context;

        Log.e("","获取待需要申请的权限数组");
        if(context==null){
            Log.e("","PermissionInject--获取Context失败");
            noticeBusiness(requestCode,false,permission,object);
          return ;
        }
      List<String> speciaPermissionList = PermissionUtils.IsHaveSpecialPermission(permission);

       List<String> requests= PermissionUtils.DeniedPermission(context,permission);
        if((requests==null || requests.size()<=0) &&
                speciaPermissionList.size()<=0 ){
            Log.e("","已全部授权，不用申请");
            noticeBusiness(requestCode,true,permission,object);
            joinPoint.proceed();
            return ;
        }
        Log.e("","开始授权");
        PermissionUtils.DoRequestPermission(context,requests,speciaPermissionList,new PermissionUtils.OnPermissionListener(){
            @Override
            public void onPermissionSuccess() {
                try{
                    Log.e("","授权成功");
                    noticeBusiness(requestCode,true,permission,object);
                    joinPoint.proceed();
                }catch (Throwable e){
                }
            }

            @Override
            public void onPermissionFail(String str,String[] deniedPermission) {
                Log.e("","授权失败");
                noticeBusiness(requestCode,false,deniedPermission,object);
               // PermissionUtils.showTipsDialog(context,str);
            }
        });
        }
    }

    private void noticeBusiness(int code,boolean result,String[] deniedPermission,Object object){
            if(code != Config.defaultPermissionRequestCode && object!=null
                    && object instanceof PermissionUtils.PermissionRequestCallBack){
                ((PermissionUtils.PermissionRequestCallBack)object).PermissionResult(code,result,deniedPermission);
            }
    }
}
