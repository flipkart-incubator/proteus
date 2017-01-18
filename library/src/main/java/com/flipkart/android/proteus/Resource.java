/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 *
 * Copyright (c) 2017 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.flipkart.android.proteus;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.util.LruCache;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * ColorResource
 *
 * @author aditya.sharat
 */

public class Resource extends Value {

    public static final String ANIM = "anim";
    public static final String COLOR = "color";
    public static final String DRAWABLE = "drawable";
    public static final String DIMEN = "dimen";
    public static final String STRING = "string";

    public static final Resource NOT_FOUND = new Resource(0);

    public final int resId;

    private Resource(int resId) {
        this.resId = resId;
    }

    public Integer getColor(Context context) {
        return getColor(resId, context);
    }

    @Nullable
    public ColorStateList getColorStateList(Context context) {
        return getColorStateList(resId, context);
    }

    public String getString(Context context) {
        return getString(resId, context);
    }

    public static Resource valueOf(String value, @ResourceType String type, Context context) {
        if (null == value) {
            return NOT_FOUND;
        }
        Resource resource = ResourceCache.cache.get(value);
        if (null == resource) {
            int resId = context.getResources().getIdentifier(value, type, context.getPackageName());
            resource = resId == 0 ? NOT_FOUND : new Resource(resId);
            ResourceCache.cache.put(value, resource);
        }
        return resource;
    }

    public static Integer getColor(int resId, Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return context.getResources().getColor(resId, context.getTheme());
            } else {
                return context.getResources().getColor(resId);
            }
        } catch (Resources.NotFoundException e) {
            return null;
        }
    }

    @Nullable
    public static ColorStateList getColorStateList(int resId, Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return context.getColorStateList(resId);
            } else {
                return context.getResources().getColorStateList(resId);
            }

        } catch (Resources.NotFoundException nfe) {
            return null;
        }
    }

    @Nullable
    public static String getString(int resId, Context context) {
        try {
            return context.getString(resId);
        } catch (Resources.NotFoundException e) {
            return null;
        }
    }

    private static class ResourceCache {
        private static final LruCache<String, Resource> cache = new LruCache<>(64);
    }

    @Override
    Value copy() {
        return this;
    }

    @StringDef({ANIM, COLOR, DRAWABLE, DIMEN, STRING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ResourceType {
    }
}
