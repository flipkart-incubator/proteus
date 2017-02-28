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

import android.content.res.XmlResourceParser;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.android.proteus.processor.AttributeProcessor;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;
import com.flipkart.android.proteus.value.Value;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author adityasharat
 */
public abstract class ViewTypeParser<V extends View> {

    private static XmlResourceParser sParser = null;

    @Nullable
    private ViewTypeParser parent;
    private AttributeProcessor[] processors = new AttributeProcessor[0];

    private Map<String, AttributeSet.Attribute> attributes = new HashMap<>();

    private int offset;
    private AttributeSet attributeSet;

    /**
     * @return
     */
    @NonNull
    public abstract String getType();

    /**
     * @return
     */
    @Nullable
    public abstract String getParentType();

    public abstract ProteusView createView(@NonNull ProteusContext context, @NonNull Layout layout,
                                           @NonNull ObjectValue data, @Nullable ViewGroup parent, int dataIndex);

    public ProteusView.Manager createViewManager(@NonNull ProteusContext context, @NonNull ProteusView view,
                                                 @NonNull Layout layout, @NonNull ObjectValue data,
                                                 @Nullable ViewGroup parent, int dataIndex) {
        if (null == this.parent) {
            DataContext dataContext = createScope(layout, data, parent, dataIndex);
            return new ViewManager(context, this, view.getAsView(), layout, dataContext);
        } else {
            return this.parent.createViewManager(context, view, layout, data, parent, dataIndex);
        }
    }

    protected DataContext createScope(@NonNull Layout layout, @NonNull ObjectValue data,
                                      @Nullable ViewGroup parent, int dataIndex) {
        DataContext dataContext, parentDataContext = null;
        Map<String, Value> map = layout.data;

        if (parent instanceof ProteusView) {
            parentDataContext = ((ProteusView) parent).getViewManager().getDataContext();
        }

        if (map == null) {
            if (parentDataContext != null) {
                dataContext = new DataContext(parentDataContext);
            } else {
                dataContext = new DataContext();
                dataContext.setData(data);
                dataContext.setIndex(dataIndex);
            }
        } else {
            if (parentDataContext != null) {
                dataContext = parentDataContext.createChildScope(map, dataIndex);
            } else {
                dataContext = new DataContext();
                dataContext.setData(data);
                dataContext = dataContext.createChildScope(map, dataIndex);
            }
        }
        return dataContext;
    }

    public void onAfterCreateView(@NonNull ProteusView view, @Nullable ViewGroup parent, int dataIndex) {
        View v = view.getAsView();
        if (null == v.getLayoutParams()) {
            ViewGroup.LayoutParams layoutParams;
            if (parent != null) {
                layoutParams = generateDefaultLayoutParams(parent);
            } else {
                layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            v.setLayoutParams(layoutParams);
        }
    }

    protected abstract void addAttributeProcessors();

    public boolean handleAttribute(V view, int attributeId, Value value) {
        int position = getPosition(attributeId);
        if (position < 0) {
            //noinspection unchecked
            return null != parent && parent.handleAttribute(view, attributeId, value);
        }
        AttributeProcessor attributeProcessor = processors[position];
        //noinspection unchecked
        attributeProcessor.process(view, value);
        return true;
    }

    public boolean handleChildren(V view, Value children) {
        //noinspection unchecked
        return null != parent && parent.handleChildren(view, children);
    }

    public boolean addView(ProteusView parent, ProteusView view) {
        return null != this.parent && this.parent.addView(parent, view);
    }

    @NonNull
    public AttributeSet prepare(@Nullable ViewTypeParser parent) {
        this.parent = parent;
        this.processors = new AttributeProcessor[0];
        this.attributes = new HashMap<>();
        this.offset = null != parent ? parent.getAttributeSet().getOffset() : 0;
        addAttributeProcessors();
        this.attributeSet = new AttributeSet(attributes.size() > 0 ? attributes : null, null != parent ? parent.getAttributeSet() : null);
        return attributeSet;
    }

    public void addAttributeProcessor(String name, AttributeProcessor<V> processor) {
        addAttributeProcessor(processor);
        attributes.put(name, new AttributeSet.Attribute(getAttributeId(processors.length - 1), processor));
    }

    private void addAttributeProcessor(AttributeProcessor<V> handler) {
        processors = Arrays.copyOf(processors, processors.length + 1);
        processors[processors.length - 1] = handler;
    }

    private int getOffset() {
        return offset;
    }

    private int getPosition(int attributeId) {
        return attributeId + getOffset();
    }

    private int getAttributeId(int position) {
        return position - getOffset();
    }

    public int getAttributeId(String name) {
        AttributeSet.Attribute attribute = attributeSet.getAttribute(name);
        return null != attribute ? attribute.id : -1;
    }

    public AttributeSet getAttributeSet() {
        return this.attributeSet;
    }

    private ViewGroup.LayoutParams generateDefaultLayoutParams(@NonNull ViewGroup parent) {

        /**
         * This whole method is a hack! To generate layout params, since no other way exists.
         * Refer : http://stackoverflow.com/questions/7018267/generating-a-layoutparams-based-on-the-type-of-parent
         */
        if (null == sParser) {
            synchronized (ViewTypeParser.class) {
                if (null == sParser) {
                    initializeAttributeSet(parent);
                }
            }
        }

        return parent.generateLayoutParams(sParser);
    }

    private void initializeAttributeSet(@NonNull ViewGroup parent) {
        sParser = parent.getResources().getLayout(R.layout.layout_params_hack);
        //noinspection StatementWithEmptyBody
        try {
            //noinspection StatementWithEmptyBody
            while (sParser.nextToken() != XmlPullParser.START_TAG) {
                // Skip everything until the view tag.
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    public static class AttributeSet {

        @Nullable
        private final Map<String, Attribute> attributes;

        @Nullable
        private final AttributeSet parent;

        private final int offset;

        private AttributeSet(@Nullable Map<String, Attribute> attributes, @Nullable AttributeSet parent) {
            this.attributes = attributes;
            this.parent = parent;
            int parentOffset = null != parent ? parent.getOffset() : 0;
            int length = null != attributes ? attributes.size() : 0;
            this.offset = parentOffset - length;
        }

        @Nullable
        public Attribute getAttribute(String name) {
            Attribute attribute = null != attributes ? attributes.get(name) : null;
            if (null != attribute) {
                return attribute;
            } else if (null != parent) {
                return parent.getAttribute(name);
            } else {
                return null;
            }
        }

        int getOffset() {
            return offset;
        }

        public static class Attribute {

            public final int id;
            public final AttributeProcessor processor;

            public Attribute(int id, AttributeProcessor processor) {
                this.processor = processor;
                this.id = id;
            }
        }
    }

}
