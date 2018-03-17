package com.gl.annaop.aopinject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

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
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        PermissionAnnotation annotation =method.getAnnotation(PermissionAnnotation.class);
        String[] permission = annotation.CheckPermission();
        if(permission == null || permission.length<=0){
            joinPoint.proceed();
            return  ;
        }
        Log.e("","获取待需要申请的权限数组");
        final Context context= Utils.GetActContext(joinPoint.getThis());
        if(context==null){
            Log.e("","PermissionInject--获取Context失败");
          return ;
        }
       List<String> requests= PermissionUtils.DeniedPermission(context,permission);
        if(requests==null || requests.size()<=0){
            Log.e("","已全部授权，不用申请");
            joinPoint.proceed();
            return ;
        }
        Log.e("","开始授权");
        PermissionUtils.DoRequestPermission(context,requests,new PermissionUtils.OnPermissionListener(){
            @Override
            public void onPermissionSuccess() {
                try{
                    Log.e("","授权成功");
                    joinPoint.proceed();
                }catch (Throwable e){

                }

            }

            @Override
            public void onPermissionFail(String str) {
                Log.e("","授权失败");
                PermissionUtils.showTipsDialog(context,str);
            }
        });
    }
}
