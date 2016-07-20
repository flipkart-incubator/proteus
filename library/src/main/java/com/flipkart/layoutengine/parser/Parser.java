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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
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

    protected static final Map<Class<?>, Constructor<? extends View>> constructorCache = new HashMap<>();
    private static XmlResourceParser sParser = null;
    protected final Class<V> viewClass;
    private Map<String, AttributeProcessor> handlers = new HashMap<>();
    private boolean prepared;

    public Parser(Class<V> viewClass) {
        this.viewClass = viewClass;
    }

    @Override
    public void prepare(Context context) {
        if (!isPrepared()) {
            prepareHandlers(context);
            prepared = true;
        }
    }

    @Override
    public boolean isPrepared() {
        return prepared;
    }

    public void addHandler(Attributes.Attribute key, AttributeProcessor<V> handler) {
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
            e.printStackTrace();
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
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
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

    @Override
    public boolean handleAttribute(ParserContext context, String attribute, JsonElement element,
                                   JsonObject layout, V view, ProteusView proteusView, ProteusView parent,
                                   int childIndex) {
        AttributeProcessor attributeProcessor = handlers.get(attribute);
        if (attributeProcessor != null && view != null) {
            //noinspection unchecked
            attributeProcessor.handle(context, attribute, element, view, proteusView, parent, layout, childIndex);
            return true;
        }
        return false;
    }

    protected void prepareHandlers(Context context) {
        /**
         * Meant to be overridden so that new {@link AttributeProcessor} can be added to new {@link Parser}
         */
    }

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
