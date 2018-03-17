package com.gl.annaop.annotation;

import com.gl.annaop.Config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by waiting on 2018/3/2.
 */

//@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewOnclickAnnotation {
    long time() default 0;
    String tips() default  "";
}
