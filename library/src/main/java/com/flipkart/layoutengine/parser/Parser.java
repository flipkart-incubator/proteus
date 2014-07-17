package com.flipkart.layoutengine.parser;

import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.builder.LayoutHandler;
import com.flipkart.layoutengine.library.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class will help parsing by introducing handlers. Any subclass can use addHandler() method  to specify a callback for an attribute.
 * This class also creates an instance of the view with the first constructor.
 */
public abstract class Parser<T extends View> implements LayoutHandler<T> {

    private static final String TAG = Parser.class.getSimpleName();
    private final Class<T> viewClass;
    private Map<String, AttributeProcessor> handlers = new HashMap<String, AttributeProcessor>();

    public Parser(Class<T> viewClass) {
        this.viewClass = viewClass;
    }
    protected static final Map<Class<?>,Constructor<? extends View>> constructorCache = new HashMap<Class<?>, Constructor<? extends View>>();

    @Override
    public void prepare(Activity activity) {
        if(handlers.size() == 0) {
            prepareHandlers(activity);
        }
    }

    protected void addHandler(String key, AttributeProcessor<T> handler) {
        handlers.put(key, handler);
    }

    @Override
    public T createView(ParserContext context, Activity activity, ViewGroup parent, JsonObject object) {
        View v = null;
        try {
            Constructor<? extends View> constructor = getContextConstructor(viewClass);
            if(constructor!=null) {
                v = constructor.newInstance(activity);
                ViewGroup.LayoutParams layoutParams = generateDefaultLayoutParams(parent, object);
                v.setLayoutParams(layoutParams);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (T)v;
    }

    /**
     * Gets and caches the constructor
     * @param viewClass
     * @return
     */
    protected Constructor<? extends View> getContextConstructor(Class<T> viewClass)
    {
        Constructor<? extends View> constructor = constructorCache.get(viewClass);
        if(constructor == null) {
            try {
                constructor = viewClass.getDeclaredConstructor(Context.class);
                constructorCache.put(viewClass,constructor);
                Log.d(TAG,"constructor for "+viewClass+" was created and put into cache");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return constructor;
    }

    protected ViewGroup.LayoutParams generateDefaultLayoutParams(ViewGroup parent, JsonObject object) {

        /**
         * This whole method is a hack! ... to generate layout params, since no other way exists.
         * Refer : http://stackoverflow.com/questions/7018267/generating-a-layoutparams-based-on-the-type-of-parent
         */
        XmlResourceParser parser = parent.getResources().getLayout(R.layout.layout_params_hack);
        try {
            while (parser.nextToken() != XmlPullParser.START_TAG) {
                // Skip everything until the view tag.
            }
            ViewGroup.LayoutParams layoutParams = parent.generateLayoutParams(parser);
            return layoutParams;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public boolean handleAttribute(ParserContext context, String attribute, JsonElement element, T view) {
        AttributeProcessor attributeProcessor = handlers.get(attribute);
        if (attributeProcessor != null) {
            attributeProcessor.handle(attribute, element.getAsString(), view);
            return true;
        }
        return false;
    }
    @Override
    public boolean canAddChild() {
        return false;
    }

    protected abstract void prepareHandlers(Activity activity);


    protected static abstract class AttributeProcessor<E> {
        public abstract void handle(String attributeKey,String attributeValue, E view);

    }


    /**
     * This is a base implementation which calls addChild() on the parent.
     *
     * @param activity Android context
     * @param parent   The view group into which the child will be added.
     * @param children The List of child views which have to be added.
     */
    @Override
    public void addChildren(Activity activity, T parent, List<View> children) {
        for (View child : children) {
            ((ViewGroup) parent).addView(child);
        }
    }

    @Override
    public JsonArray parseChildren(ParserContext context, JsonElement element) {
        return element.getAsJsonArray();
    }


    @Override
    public void setupView(ViewGroup parent, T view) {
        // nothing to do here
    }


}
