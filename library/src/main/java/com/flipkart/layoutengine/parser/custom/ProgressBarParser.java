package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.ParseHelper;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;
import com.google.gson.JsonObject;

import java.lang.reflect.Constructor;

/**
 * @author Aditya Sharat
 */
public class ProgressBarParser<T extends ProgressBar> extends WrappableParser<T> {
    private static final String TAG = ProgressBarParser.class.getSimpleName();

    public ProgressBarParser(Parser<T> wrappedParser) {
        super(ProgressBar.class, wrappedParser);
    }

    @Override
    public T createView(ParserContext parserContext, Context context, ViewGroup parent, JsonObject object) {
        View v = null;
        try {
            Constructor<? extends ProgressBar> constructor = getContextConstructor(viewClass);
            if (constructor != null) {
                v = constructor.newInstance(context, null, android.R.attr.progressBarStyleHorizontal);
                ViewGroup.LayoutParams layoutParams = generateDefaultLayoutParams(parent, object);
                v.setLayoutParams(layoutParams);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (T) v;
    }

    @Override
    protected void prepareHandlers(Context context) {
        super.prepareHandlers(context);
        addHandler(Attributes.ProgressBar.Max, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setMax((int) Double.parseDouble(attributeValue));
            }
        });
        addHandler(Attributes.ProgressBar.Progress, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setProgress((int) Double.parseDouble(attributeValue));
            }
        });
        addHandler(Attributes.ProgressBar.ProgressTint, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.getProgressDrawable().setColorFilter(ParseHelper.parseColor(attributeValue), PorterDuff.Mode.SRC_IN);
            }
        });
    }

    @Override
    protected Constructor<? extends T> getContextConstructor(Class<T> viewClass) {
        Constructor<? extends T> constructor = (Constructor<? extends T>) constructorCache.get(viewClass);
        if (constructor == null) {
            try {
                constructor = viewClass.getDeclaredConstructor(Context.class, AttributeSet.class, int.class);
                constructorCache.put(viewClass, constructor);
                Log.d(TAG, "constructor for " + viewClass + " was created and put into cache");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return constructor;
    }

    public static class AttributeSetImpl implements AttributeSet {

        @Override
        public int getAttributeCount() {
            return 0;
        }

        @Override
        public String getAttributeName(int index) {
            return null;
        }

        @Override
        public String getAttributeValue(int index) {
            return null;
        }

        @Override
        public String getAttributeValue(String namespace, String name) {
            return null;
        }

        @Override
        public String getPositionDescription() {
            return null;
        }

        @Override
        public int getAttributeNameResource(int index) {
            return 0;
        }

        @Override
        public int getAttributeListValue(String namespace, String attribute, String[] options, int defaultValue) {
            return 0;
        }

        @Override
        public boolean getAttributeBooleanValue(String namespace, String attribute, boolean defaultValue) {
            return false;
        }

        @Override
        public int getAttributeResourceValue(String namespace, String attribute, int defaultValue) {
            return 0;
        }

        @Override
        public int getAttributeIntValue(String namespace, String attribute, int defaultValue) {
            return 0;
        }

        @Override
        public int getAttributeUnsignedIntValue(String namespace, String attribute, int defaultValue) {
            return 0;
        }

        @Override
        public float getAttributeFloatValue(String namespace, String attribute, float defaultValue) {
            return 0;
        }

        @Override
        public int getAttributeListValue(int index, String[] options, int defaultValue) {
            return 0;
        }

        @Override
        public boolean getAttributeBooleanValue(int index, boolean defaultValue) {
            return false;
        }

        @Override
        public int getAttributeResourceValue(int index, int defaultValue) {
            return 0;
        }

        @Override
        public int getAttributeIntValue(int index, int defaultValue) {
            return 0;
        }

        @Override
        public int getAttributeUnsignedIntValue(int index, int defaultValue) {
            return 0;
        }

        @Override
        public float getAttributeFloatValue(int index, float defaultValue) {
            return 0;
        }

        @Override
        public String getIdAttribute() {
            return null;
        }

        @Override
        public String getClassAttribute() {
            return null;
        }

        @Override
        public int getIdAttributeResourceValue(int defaultValue) {
            return 0;
        }

        @Override
        public int getStyleAttribute() {
            return 0;
        }
    }
}
