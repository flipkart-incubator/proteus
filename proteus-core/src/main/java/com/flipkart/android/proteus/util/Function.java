package com.flipkart.android.proteus.util;

/**
 * Created by Prasad Rao on 02-03-2020 18:10
 **/
public interface Function<T, R> {
    R execute(T t);
}
