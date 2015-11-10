package com.flipkart.layoutengine.parser;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.library.R;
import com.flipkart.layoutengine.processor.AttributeProcessor;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class will help parsing by introducing handlers. Any subclass can use addHandler()
 * method  to specify a callback for an attribute.
 * This class also creates an instance of the view with the first constructor.
 */
public abstract class Parser<V extends View> implements LayoutHandler<V> {

    protected final Class<V> viewClass;
    private Map<String, AttributeProcessor> handlers = new HashMap<>();
    private Logger logger = LoggerFactory.getLogger(Parser.class);

    public Parser(Class<V> viewClass) {
        this.viewClass = viewClass;
    }

    protected static final Map<Class<?>, Constructor<? extends View>> constructorCache = new HashMap<>();

    @Override
    public void prepare(Context context) {
        if (handlers.size() == 0) {
            prepareHandlers(context);
        }
    }

    protected void addHandler(Attributes.Attribute key, AttributeProcessor<V> handler) {
        handlers.put(key.getName(), handler);
    }

    @Override
    public V createView(ParserContext parserContext, Context context, ProteusView parent, JsonObject layout) {
        View v = null;
        try {
            Constructor<? extends View> constructor = getContextConstructor(viewClass);
            if (constructor != null) {
                v = constructor.newInstance(context);
                ViewGroup.LayoutParams layoutParams = generateDefaultLayoutParams((ViewGroup) parent.getView(), layout);
                v.setLayoutParams(layoutParams);
            }
        } catch (Exception e) {
            if(logger.isErrorEnabled()) {
                logger.error("#createView()", e.getMessage());
            }
        }

        //noinspection unchecked
        return (V) v;
    }

    /**
     * Gets and caches the constructor
     *
     * @param viewClass
     * @return Constructor of that class
     */
    protected Constructor<? extends V> getContextConstructor(Class<V> viewClass) {
        //noinspection unchecked
        Constructor<? extends V> constructor = (Constructor<? extends V>) constructorCache.get(viewClass);
        if (constructor == null) {
            try {
                constructor = viewClass.getDeclaredConstructor(Context.class);
                constructorCache.put(viewClass, constructor);
                if(logger.isDebugEnabled()) {
                    logger.debug("constructor for " + viewClass + " was created and put into cache");
                }
            } catch (NoSuchMethodException e) {
                if(logger.isErrorEnabled()) {
                    logger.error(e.getMessage());
                }
            }
        }
        return constructor;
    }

    protected ViewGroup.LayoutParams generateDefaultLayoutParams(ViewGroup parent, JsonObject layout) {

        /**
         * This whole method is a hack! To generate layout params, since no other way exists.
         * Refer : http://stackoverflow.com/questions/7018267/generating-a-layoutparams-based-on-the-type-of-parent
         */
        XmlResourceParser parser = parent.getResources().getLayout(R.layout.layout_params_hack);
        try {

            //noinspection StatementWithEmptyBody
            while (parser.nextToken() != XmlPullParser.START_TAG) {
                // Skip everything until the view tag.
            }
            return parent.generateLayoutParams(parser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean handleAttribute(ParserContext context, String attribute, JsonElement element,
                                   JsonObject layout, V view, ProteusView proteusView, ProteusView parent,
                                   int childIndex) {
        AttributeProcessor attributeProcessor = handlers.get(attribute);
        if (attributeProcessor != null) {
            //noinspection unchecked
            attributeProcessor.handle(context, attribute, element, view, proteusView, parent, layout, childIndex);
            return true;
        }
        return false;
    }

    protected abstract void prepareHandlers(Context context);

    @Override
    public void addChildren(ParserContext parserContext, ProteusView parent,
                            List<ProteusView> children, JsonObject viewLayout) {
        for (ProteusView child : children) {
            parent.addView(child);
        }
    }

    @Override
    public JsonArray parseChildren(ParserContext context, JsonElement element, int childIndex) {
        return element.getAsJsonArray();
    }

    @Override
    public void setupView(ParserContext context, ProteusView parent, V view, JsonObject layout) {
        // nothing to do here
    }
}
