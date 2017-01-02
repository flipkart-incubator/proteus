/*
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.android.proteus.parser;

import android.content.res.XmlResourceParser;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.android.proteus.Layout;
import com.flipkart.android.proteus.R;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.processor.AttributeProcessor;
import com.flipkart.android.proteus.toolbox.ProteusConstants;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.view.ProteusView;
import com.google.gson.JsonObject;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class will help parsing by introducing processorNameMap. Any subclass can use addAttributeProcessor()
 * method  to specify a callback for an attribute.
 * This class also creates an instance of the view with the first constructor.
 *
 * @author kiran.kumar
 * @author aditya.sharat
 */
public abstract class BaseTypeParser<V extends View> implements TypeParser<V> {

    private static final String TAG = "BaseTypeParser";

    private static XmlResourceParser sParser = null;
    protected AttributeProcessor[] processors = new AttributeProcessor[0];
    protected int offset = 0;
    private boolean prepared;
    private Map<String, AttributeProcessor> processorNameMap = new HashMap<>();
    private Map<String, Integer> nameToIdMap = new HashMap<>();

    @Override
    public void onBeforeCreateView(ViewGroup parent, Layout layout, JsonObject data, Styles styles, int index) {
        // nothing to do here
    }

    @Override
    public void onAfterCreateView(ViewGroup parent, V view, Layout layout, JsonObject data, Styles styles, int index) {
        try {
            ViewGroup.LayoutParams layoutParams = generateDefaultLayoutParams(parent);
            view.setLayoutParams(layoutParams);
        } catch (Exception e) {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, "#createView() : " + e.getMessage());
            }
        }
    }

    protected void registerAttributeProcessors() {

    }

    @Override
    public boolean handleAttribute(V view, int attributeId, Value value) {
        int position = getPosition(attributeId);
        if (position < 0) {
            return false;
        }
        AttributeProcessor attributeProcessor = processors[position];
        //noinspection unchecked
        attributeProcessor.handle(view, value);
        return true;
    }

    @Override
    public boolean handleChildren(ProteusView view, Value children) {
        return false;
    }

    @Override
    public boolean addView(ProteusView parent, ProteusView view) {
        return false;
    }

    @Override
    public void prepare() {
        if (!isPrepared()) {
            registerAttributeProcessors();
            prepared = true;
        }
    }

    @Override
    public boolean isPrepared() {
        return prepared;
    }

    @Override
    public void addAttributeProcessor(Attributes.Attribute attribute, AttributeProcessor<V> handler) {
        processorNameMap.put(attribute.getName(), handler);
        addAttributeProcessor(handler);
        nameToIdMap.put(attribute.getName(), processors.length - 1);
    }

    private void addAttributeProcessor(AttributeProcessor<V> handler) {
        processors = Arrays.copyOf(processors, processors.length + 1);
        processors[processors.length - 1] = handler;
    }

    protected int getOffset() {
        return offset;
    }

    protected int getAttributeProcessorCount() {
        return processors.length;
    }

    protected int getPosition(int attributeId) {
        return attributeId + getOffset();
    }

    protected int getAttributeId(int position) {
        return position - getOffset();
    }

    @Override
    public int getAttributeId(String attribute) {
        Integer position = nameToIdMap.get(attribute);
        if (null == position) {
            return -1;
        }
        return getAttributeId(position);
    }

    protected ViewGroup.LayoutParams generateDefaultLayoutParams(ViewGroup parent) throws IOException, XmlPullParserException {

        /**
         * This whole method is a hack! To generate layout params, since no other way exists.
         * Refer : http://stackoverflow.com/questions/7018267/generating-a-layoutparams-based-on-the-type-of-parent
         */
        if (null == sParser) {
            synchronized (BaseTypeParser.class) {
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

