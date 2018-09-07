package com.gl.annaop.annotation;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Keep;

import com.gl.annaop.Config;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by waiting on 2018/3/9.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionAnnotation {
    String[] CheckPermission();
    int RequestCode() default -1;
}
