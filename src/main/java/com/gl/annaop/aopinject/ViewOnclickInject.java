package com.gl.annaop.aopinject;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.gl.annaop.Config;
import com.gl.annaop.annotation.ViewOnclickAnnotation;
import com.gl.annaop.utils.Utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**192.168.50.90
 * Created by waiting on 2018/3/2.
 */

@Aspect
public class ViewOnclickInject {

    public static Map<Integer,Long>viewsMap=new HashMap<>();
    public static void Clean(){
        if(viewsMap!=null){
            viewsMap.clear();
        }
    }

    @Pointcut("execution(@com.gl.annaop.annotation.ViewOnclickAnnotation * *(..))")
    public void executionViewOnclick(){

    }

    @Around("executionViewOnclick()")
    public Object doViewOnclickInject(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 通过Method对象得到切点上的注解
        ViewOnclickAnnotation annotation = method.getAnnotation(ViewOnclickAnnotation.class);
        long space_time = annotation.time() > 0 ? annotation.time():Config.ViewClickSpaceTime;
        if(space_time<=0){
            return joinPoint.proceed();
        }
        String tips = annotation.tips()!=null &&  annotation.tips().length() >0 ?  annotation.tips() : Config.ViewClickSpaceTips;
        Object[] para=joinPoint.getArgs();//获取函数的参数
        if(para == null || para.length<=0){
            return joinPoint.proceed();
        }
        if(para[0] instanceof View){
            int id= ((View)para[0]).getId();
            if(viewsMap.containsKey(id)){
                long lasttime = viewsMap.get(id);
                if(System.currentTimeMillis()-lasttime>space_time){
                    viewsMap.put(id,System.currentTimeMillis());
                    return joinPoint.proceed();
                }else{
                    if(tips!=null && tips.length()>0){
                      Context context= Utils.GetContext(para[0]);
                        if(context !=null){
                            Toast.makeText(context,tips,Toast.LENGTH_SHORT).show();
                        }
                    }
                    return null;
                }
            }else{
                viewsMap.put(id,System.currentTimeMillis());
                return joinPoint.proceed();
            }
        }else{
            return joinPoint.proceed();
        }
    }

}
