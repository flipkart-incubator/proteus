package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.flipkart.layoutengine.DataContext;
import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.binding.Binding;
import com.flipkart.layoutengine.exceptions.InvalidDataPathException;
import com.flipkart.layoutengine.exceptions.JsonNullException;
import com.flipkart.layoutengine.exceptions.NoSuchDataPathException;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.LayoutHandler;
import com.flipkart.layoutengine.provider.JsonProvider;
import com.flipkart.layoutengine.provider.ProteusConstants;
import com.flipkart.layoutengine.toolbox.Formatter;
import com.flipkart.layoutengine.toolbox.Styles;
import com.flipkart.layoutengine.toolbox.Utils;
import com.flipkart.layoutengine.view.DataProteusView;
import com.flipkart.layoutengine.view.ProteusView;
import com.flipkart.layoutengine.view.SimpleProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * A layout builder which can parse data bindings before passing it on to {@link SimpleLayoutBuilder}
 */
public class DataParsingLayoutBuilder extends SimpleLayoutBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DataAndViewParsingLayoutBuilder.class);
    private Map<String, Formatter> formatter = new HashMap<>();

    protected DataParsingLayoutBuilder(Context context, @Nullable IdGenerator idGenerator) {
        super(context, idGenerator);
    }

    @Override
    protected List<ProteusView> parseChildren(LayoutHandler handler, ParserContext context,
                                              ProteusView view, JsonObject parentLayout, int childIndex, Styles styles) {

        if (logger.isDebugEnabled()) {
            logger.debug("Parsing children for view with " + Utils.getLayoutIdentifier(parentLayout));
        }
        JsonElement childrenElement = parentLayout.get(ProteusConstants.CHILDREN);

        if (childrenElement != null &&
                childrenElement.isJsonPrimitive() &&
                ((JsonPrimitive) childrenElement).isString() &&
                !childrenElement.getAsString().isEmpty() &&
                childrenElement.getAsString().charAt(0) == ProteusConstants.DATA_PREFIX) {

            String dataPathLength = childrenElement.getAsString().substring(1);
            String dataPath = dataPathLength.substring(0, dataPathLength.lastIndexOf("."));
            int length;
            try {
                JsonElement elementFromData = Utils.getElementFromData(dataPathLength,
                        context.getDataContext().getDataProvider(),
                        childIndex);
                String attributeValue = elementFromData.getAsString();
                if (ProteusConstants.DATA_NULL.equals(attributeValue)) {
                    length = 0;
                } else {
                    length = Integer.parseInt(attributeValue);
                }
            } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException | IllegalStateException e) {
                logger.error(TAG_ERROR + "#parseChildren() " + e.getMessage());
                length = 0;
            } catch (NumberFormatException e) {
                logger.error(TAG_ERROR + "#parseChildren() " + childrenElement.getAsString() +
                        " is not a number. layout: " +
                        parentLayout.toString());
                length = 0;
            }

            // get the child type
            JsonElement childTypeElement = parentLayout.get(ProteusConstants.CHILD_TYPE);

            if (childTypeElement != null) {
                List<ProteusView> childrenView = new ArrayList<>();
                JsonObject childLayout = getChildLayout(childTypeElement, context, parentLayout, view);
                JsonElement childDataContext = childLayout.get(ProteusConstants.CHILD_DATA_CONTEXT);
                JsonElement childDataContextFromParent = parentLayout.get(ProteusConstants.CHILD_DATA_CONTEXT);

                if (childDataContextFromParent != null && childDataContext != null) {
                    Utils.addElements(childDataContext.getAsJsonObject(),
                            childDataContextFromParent.getAsJsonObject(),
                            true);
                } else if (childDataContextFromParent != null) {
                    childLayout.add(ProteusConstants.DATA_CONTEXT, childDataContextFromParent);
                }

                DataProteusView proteusView = (DataProteusView) view;
                proteusView.setChildLayout(childLayout);
                proteusView.setDataPathForChildren(dataPath);

                for (int i = 0; i < length; i++) {
                    ProteusView childView = buildImpl(context, view, childLayout, i, styles);
                    if (childView != null && childView.getView() != null) {
                        childrenView.add(childView);
                    }
                }

                return childrenView;
            }
        }

        return super.parseChildren(handler, context, view, parentLayout, childIndex, styles);
    }

    @Override
    public ProteusView build(View parent, JsonObject layout, JsonObject data, int childIndex, Styles styles) {
        return super.build(parent, layout, data, childIndex, styles);
    }

    @Override
    protected ParserContext createParserContext(JsonObject data, Styles styles) {
        ParserContext parserContext = super.createParserContext(data, styles);
        if (styles != null) {
            parserContext.setStyles(styles);
        }
        if (data != null) {
            parserContext.getDataContext().setDataProvider(new JsonProvider(data));
        }
        return parserContext;
    }

    @Override
    protected ProteusView buildImpl(ParserContext parserContext, final ProteusView parent,
                                    final JsonObject layout, final int childIndex, Styles styles) {
        parserContext = getNewParserContext(parserContext, layout, childIndex);
        return super.buildImpl(parserContext, parent, layout, childIndex, styles);
    }

    @Override
    public boolean handleAttribute(LayoutHandler handler, ParserContext context,
                                   String attributeName, JsonElement element,
                                   JsonObject layout, ProteusView proteusView,
                                   ProteusView parent, int childIndex) {

        if (ProteusConstants.DATA_CONTEXT.equals(attributeName)) {
            return true;
        }
        if (element.isJsonPrimitive()) {
            element = this.findAndReplaceValues(element, context, handler, attributeName,
                    proteusView, layout, childIndex);
        }
        return super.handleAttribute(handler, context, attributeName, element, layout,
                proteusView, parent, childIndex);
    }

    private JsonElement findAndReplaceValues(JsonElement element, ParserContext parserContext,
                                             LayoutHandler handler, String attributeName,
                                             ProteusView proteusView, JsonObject layout,
                                             int childIndex) {

        boolean failed = false;
        String attributeValue = element.getAsString();
        DataProteusView dataProteusView = (DataProteusView) proteusView;

        char firstChar = TextUtils.isEmpty(attributeValue) ? 0 : attributeValue.charAt(0);
        boolean setVisibility = false;
        String dataPath;
        switch (firstChar) {
            case ProteusConstants.DATA_PREFIX:
                setVisibility = true;
                dataPath = attributeValue.substring(1);
                JsonElement elementFromData;
                try {
                    elementFromData = Utils.getElementFromData(dataPath,
                            parserContext.getDataContext().getDataProvider(),
                            childIndex);
                } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException e) {
                    if (logger.isErrorEnabled()) {
                        logger.error(TAG_ERROR + "#findAndReplaceValues() " + e.getMessage());
                    }
                    failed = true;
                    elementFromData = new JsonPrimitive(ProteusConstants.DATA_NULL);
                }

                if (elementFromData != null) {
                    element = elementFromData;
                }
                addBinding(dataProteusView,
                        dataPath,
                        attributeName,
                        attributeValue,
                        handler,
                        false);
                break;
            case ProteusConstants.REGEX_PREFIX:
                setVisibility = true;
                Matcher regexMatcher = ProteusConstants.REGEX_PATTERN.matcher(attributeValue);
                String finalValue = attributeValue;

                while (regexMatcher.find()) {
                    String matchedString = regexMatcher.group(0);
                    String bindingName;
                    if (regexMatcher.group(3) != null) {

                        // has NO formatter
                        dataPath = regexMatcher.group(3);
                        try {
                            finalValue = finalValue.replace(matchedString, Utils.getElementFromData(
                                    dataPath,
                                    parserContext.getDataContext().getDataProvider(),
                                    parserContext.getDataContext().getIndex()).getAsString());
                        } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException e) {
                            if (logger.isErrorEnabled()) {
                                logger.error(TAG_ERROR + "#findAndReplaceValues() " + e.getMessage());
                            }
                            finalValue = dataPath;
                            failed = true;
                        }
                        bindingName = dataPath;
                    } else {

                        // has formatter
                        dataPath = regexMatcher.group(1);
                        String formatterName = regexMatcher.group(2);

                        String formattedValue;
                        try {
                            formattedValue = format(Utils.getElementFromData(dataPath,
                                    parserContext.getDataContext().getDataProvider(),
                                    parserContext.getDataContext().getIndex()),
                                    formatterName);
                        } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException e) {
                            if (logger.isErrorEnabled()) {
                                logger.error(TAG_ERROR + "#findAndReplaceValues() " + e.getMessage());
                            }
                            formattedValue = dataPath;
                            failed = true;
                        }
                        finalValue = finalValue.replace(matchedString, formattedValue != null ? formattedValue : "");
                        bindingName = dataPath;
                    }
                    addBinding(dataProteusView,
                            bindingName,
                            attributeName,
                            attributeValue,
                            handler,
                            true);
                }

                // remove the REGEX_PREFIX
                finalValue = finalValue.substring(1);

                // return as a JsonPrimitive
                element = new JsonPrimitive(finalValue);
                break;
        }

        if (setVisibility && dataProteusView.getView() != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Find '" + element.toString() + "' for " + attributeName
                        + " for view with " + Utils.getLayoutIdentifier(layout));
            }
            if (failed) {
                if (layout != null && !layout.isJsonNull()
                        && ProteusConstants.DATA_VISIBILITY
                        .equals(Utils.getPropertyAsString(layout,
                                Attributes.View.Visibility.getName()))) {
                    dataProteusView.getView().setVisibility(View.INVISIBLE);
                } else if (DataProteusView.shouldSetVisibility(attributeName, dataProteusView.getView())) {
                    dataProteusView.getView().setVisibility(View.GONE);
                }
            } else if (dataProteusView.isViewUpdating()
                    && DataProteusView.shouldSetVisibility(attributeName, dataProteusView.getView())) {
                dataProteusView.getView().setVisibility(View.VISIBLE);
            }
        }
        return element;
    }

    private void addBinding(DataProteusView dataProteusView, String bindingName, String attributeName,
                            String attributeValue, LayoutHandler handler, boolean hasRegEx) {
        // check if the view is in update mode if not that means that the update flow
        // is running and we must not add more bindings for they will be duplicates
        if (!dataProteusView.isViewUpdating()) {
            Binding binding = new Binding(handler,
                    bindingName,
                    attributeName,
                    attributeValue,
                    hasRegEx);

            dataProteusView.addBinding(binding);
        }
    }

    protected ParserContext getNewParserContext(final ParserContext oldParserContext,
                                                final JsonObject currentViewJsonObject,
                                                final int childIndex) {

        ParserContext newParserContext = oldParserContext.clone();
        DataContext newDataContext = oldParserContext.getDataContext();

        JsonElement scope = currentViewJsonObject.get(ProteusConstants.DATA_CONTEXT);
        if (scope == null || scope.isJsonNull()) {
            newParserContext.setDataContext(newDataContext);
            newParserContext.setHasDataContext(false);
            return newParserContext;
        }

        if (oldParserContext.getDataContext().getDataProvider() == null) {
            if (logger.isErrorEnabled()) {
                logger.error(TAG_ERROR + "#getNewParserContext() When scope is specified, data provider cannot be null");
            }
            newParserContext.setDataContext(newDataContext);
            newParserContext.setHasDataContext(false);
            return newParserContext;
        }

        newDataContext = oldParserContext.getDataContext().createChildDataContext(scope.getAsJsonObject(), childIndex);
        newParserContext.setDataContext(newDataContext);
        newParserContext.setHasDataContext(true);

        return newParserContext;
    }

    @Override
    protected ProteusView createProteusView(View createdView, JsonObject layout, int index, ProteusView parent) {
        return new DataProteusView(new SimpleProteusView(createdView, layout, index, parent));
    }

    @Override
    protected void prepareProteusView(ProteusView proteusView, ParserContext parserContext) {
        ((DataProteusView) proteusView).setParserContext(parserContext);
    }

    private String format(JsonElement toFormat, String formatterName) {
        Formatter formatter = this.formatter.get(formatterName);
        if (formatter == null) {
            formatter = Formatter.NOOP;
        }

        return formatter.format(toFormat);
    }

    public void registerFormatter(Formatter formatter) {
        this.formatter.put(formatter.getName(), formatter);
    }

    public void unregisterFormatter(String formatterName) {
        if (Formatter.NOOP.getName().equals(formatterName)) {
            return;
        }
        this.formatter.remove(formatterName);
    }

    public Formatter getFormatter(String formatterName) {
        return this.formatter.get(formatterName);
    }
}
