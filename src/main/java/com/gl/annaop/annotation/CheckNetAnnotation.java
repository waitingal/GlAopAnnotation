package com.gl.annaop.annotation;

import com.gl.annaop.Config;
import com.gl.annaop.NetType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by waiting on 2018/3/5.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckNetAnnotation {
    String netTips() default "";
    NetType NetType() default NetType.All;
}
