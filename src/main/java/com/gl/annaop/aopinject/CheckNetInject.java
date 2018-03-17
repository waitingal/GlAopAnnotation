package com.gl.annaop.aopinject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.gl.annaop.AnnAopManage;
import com.gl.annaop.Config;
import com.gl.annaop.NetType;
import com.gl.annaop.annotation.CheckNetAnnotation;
import com.gl.annaop.utils.Utils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Created by waiting on 2018/3/5.
 */
@Aspect
public class CheckNetInject {

    @Pointcut("execution(@com.gl.annaop.annotation.CheckNetAnnotation * *(..))")
    public void executionCheckNet(){
    }

    @Around("executionCheckNet()")
    public Object doCheckNetInject(ProceedingJoinPoint joinPoint) throws Throwable{
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CheckNetAnnotation annotation = method.getAnnotation(CheckNetAnnotation.class);
        NetType mNetType = annotation.NetType();
        Context context = AnnAopManage.context != null ? AnnAopManage.context :Utils.GetContext(joinPoint.getThis());
//        Object object = joinPoint.getThis();
//        Context context= Utils.GetContext(object);//如果当前方法在act,view,fragment,service里可以获取到context
        if(context!=null){
          if(mNetType == NetType.All && Utils.isNetworkConnected(context)){
              return  joinPoint.proceed();
          }  else if(mNetType ==  Utils.getConnectedType(context)){
              return  joinPoint.proceed();
          } else{
              String tips = annotation.netTips()!=null &&  annotation.netTips().length() >0 ? annotation.netTips(): Config.defaultNetTips;
                if(tips!=null && tips.length()>0)
                    Toast.makeText(context,tips,Toast.LENGTH_SHORT).show();
                return  null;
          }
        }else{
            Log.e("","context=空,无法判断，不执行原方法");
         return  null;
        }
    }
}
