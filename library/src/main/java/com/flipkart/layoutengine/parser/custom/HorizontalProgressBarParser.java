package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Parser;
import com.google.gson.JsonObject;

import java.lang.reflect.Constructor;

/**
 * HorizontalProgressBarParser
 *
 * @author Aditya Sharat
 */
public class HorizontalProgressBarParser<T extends ProgressBar> extends ProgressBarParser {

    private static final String TAG = ProgressBarParser.class.getSimpleName();

    public HorizontalProgressBarParser(Parser wrappedParser) {
        super(wrappedParser);
    }

    @Override
    public ProgressBar createView(ParserContext parserContext, Context context, ViewGroup parent, JsonObject object) {
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
    protected Constructor getContextConstructor(Class viewClass) {
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
}
