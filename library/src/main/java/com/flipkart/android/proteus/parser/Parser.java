/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Flipkart Internet Pvt. Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.flipkart.android.proteus.parser;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.view.View;
import android.view.ViewGroup;


import com.flipkart.android.proteus.R;
import com.flipkart.android.proteus.processor.AttributeProcessor;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * This class will help parsing by introducing handlers. Any subclass can use addHandler()
 * method  to specify a callback for an attribute.
 * This class also creates an instance of the view with the first constructor.
 *
 * @author kiran.kumar
 * @author aditya.sharat
 */
public abstract class Parser<V extends View> implements LayoutHandler<V> {

    protected static final Map<Class<?>, Constructor<? extends View>> constructorCache = new HashMap<>();
    private static XmlResourceParser sParser = null;
    protected final Class<V> viewClass;
    private Map<String, AttributeProcessor> handlers = new HashMap<>();
    private static Logger logger = LoggerFactory.getLogger(Parser.class);

    public Parser(Class<V> viewClass) {
        this.viewClass = viewClass;
    }

    @Override
    public void onBeforeCreateView(View parent, JsonObject layout, JsonObject data, int index, Styles styles) {
        // nothing to do here
    }

    @Override
    public V createView(View parent, JsonObject layout, JsonObject data, int index, Styles styles) {
        View v = null;
        try {
            Constructor<? extends View> constructor = getContextConstructor(viewClass);
            if (constructor != null) {
                v = constructor.newInstance(parent.getContext());
                ViewGroup.LayoutParams layoutParams = generateDefaultLayoutParams((ViewGroup) parent, layout);
                v.setLayoutParams(layoutParams);
            }
        } catch (Exception e) {
            if (ProteusConstants.isLoggingEnabled()) {
                logger.error("#createView()", e.getMessage() + "");
            }
        }
        //noinspection unchecked
        return (V) v;
    }

    @Override
    public void onAfterCreateView(V view, JsonObject layout, JsonObject data, int index, Styles styles) {
        // nothing to do here
    }

    protected abstract void prepareHandlers();

    @Override
    public boolean handleAttribute(V view, String attribute, JsonElement value) {
        AttributeProcessor attributeProcessor = handlers.get(attribute);
        if (attributeProcessor != null) {
            //noinspection unchecked
            attributeProcessor.handle(attribute, value, view);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleChildren(ProteusView view) {
        return false;
    }

    @Override
    public boolean addView(ProteusView parent, ProteusView view) {
        return false;
    }

    @Override
    public void prepareAttributeHandlers() {
        if (handlers.size() == 0) {
            prepareHandlers();
        }
    }

    @Override
    public void addHandler(Attributes.Attribute key, AttributeProcessor<V> handler) {
        handlers.put(key.getName(), handler);
    }

    protected Constructor<? extends V> getContextConstructor(Class<V> viewClass) {
        //noinspection unchecked
        Constructor<? extends V> constructor = (Constructor<? extends V>) constructorCache.get(viewClass);
        if (constructor == null) {
            try {
                constructor = viewClass.getDeclaredConstructor(Context.class);
                constructorCache.put(viewClass, constructor);
                if (ProteusConstants.isLoggingEnabled()) {
                    logger.debug("constructor for " + viewClass + " was created and put into cache");
                }
            } catch (NoSuchMethodException e) {
                if (ProteusConstants.isLoggingEnabled()) {
                    logger.error(e.getMessage() + "");
                }
            }
        }
        return constructor;
    }

    protected ViewGroup.LayoutParams generateDefaultLayoutParams(ViewGroup parent, JsonObject layout) throws IOException, XmlPullParserException {

        /**
         * This whole method is a hack! To generate layout params, since no other way exists.
         * Refer : http://stackoverflow.com/questions/7018267/generating-a-layoutparams-based-on-the-type-of-parent
         */
        if (null == sParser) {
            synchronized (Parser.class) {
                if (null == sParser) {
                    sParser = parent.getResources().getLayout(R.layout.layout_params_hack);
                    //noinspection StatementWithEmptyBody
                    while (sParser.nextToken() != XmlPullParser.START_TAG) {
                        // Skip everything until the view tag.
                    }
                }
            }
        }

        return parent.generateLayoutParams(sParser);
    }
}

