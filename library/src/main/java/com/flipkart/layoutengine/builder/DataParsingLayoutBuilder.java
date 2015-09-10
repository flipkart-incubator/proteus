package com.flipkart.layoutengine.builder;

import android.content.Context;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * A layout builder which can parse data bindings before passing it on to {@link SimpleLayoutBuilder}
 */
public class DataParsingLayoutBuilder extends SimpleLayoutBuilder {

    public static final String TAG = Utils.getTagPrefix() + DataParsingLayoutBuilder.class.getSimpleName();
    private Map<String, Formatter> formatter = new HashMap<>();

    protected DataParsingLayoutBuilder(Context context) {
        super(context);
    }

    @Override
    protected List<ProteusView> parseChildren(LayoutHandler handler, ParserContext context,
                                              ProteusView view, JsonObject parentViewJson, int childIndex, Styles styles) {

        JsonElement childrenElement = parentViewJson.get(ProteusConstants.CHILDREN);

        if (childrenElement != null &&
                childrenElement.isJsonPrimitive() &&
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
                Log.e(TAG + "#parseChildren()", e.getMessage());
                length = 0;
            } catch (NumberFormatException e) {
                Log.e(TAG + "#parseChildren()", childrenElement.getAsString() +
                        " is not a number. layout: " +
                        parentViewJson.toString());
                length = 0;
            }

            // get the child type
            JsonElement childTypeElement = parentViewJson.get(ProteusConstants.CHILD_TYPE);

            if (childTypeElement != null) {
                List<ProteusView> childrenView = new ArrayList<>();
                JsonObject childLayout = getChildLayout(childTypeElement, context, parentViewJson, view);
                JsonElement childDataContext = childLayout.get(ProteusConstants.CHILD_DATA_CONTEXT);
                JsonElement childDataContextFromParent = parentViewJson.get(ProteusConstants.CHILD_DATA_CONTEXT);

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
                    ProteusView childView = buildImpl(context, view, childLayout, null, i, styles);
                    if (childView != null && childView.getView() != null) {
                        childrenView.add(childView);
                    }
                }

                return childrenView;
            }
        }

        return super.parseChildren(handler, context, view, parentViewJson, childIndex, styles);
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
    protected ProteusView buildImpl(ParserContext context, final ProteusView parent,
                                    final JsonObject layout, View existingView,
                                    final int childIndex, Styles styles) {
        context = getNewParserContext(context, layout, childIndex);
        return super.buildImpl(context, parent, layout, existingView, childIndex, styles);
    }

    @Override
    public boolean handleAttribute(LayoutHandler handler, ParserContext context,
                                   String attributeName, JsonElement jsonDataValue, JsonObject layout,
                                   ProteusView associatedProteusView,
                                   ProteusView parent, int childIndex) {
        if (jsonDataValue.isJsonPrimitive()) {
            if (ProteusConstants.DATA_CONTEXT.equals(attributeName)) {
                return true;
            }
            jsonDataValue = this.findAndReplaceValues(jsonDataValue,
                    context,
                    handler,
                    attributeName,
                    associatedProteusView,
                    layout,
                    childIndex);
        }
        return super.handleAttribute(handler,
                context,
                attributeName,
                jsonDataValue, layout,
                associatedProteusView,
                parent,
                childIndex);
    }

    private JsonElement findAndReplaceValues(JsonElement jsonDataValue, ParserContext parserContext,
                                             LayoutHandler handler, String attributeName,
                                             ProteusView proteusView, JsonObject viewJsonObject,
                                             int childIndex) {

        boolean failed = false;
        String attributeValue = jsonDataValue.getAsString();
        DataProteusView dataProteusView = (DataProteusView) proteusView;

        if (attributeValue != null && !"".equals(attributeValue) &&
                (attributeValue.charAt(0) == ProteusConstants.DATA_PREFIX ||
                        attributeValue.charAt(0) == ProteusConstants.REGEX_PREFIX)) {

            if (attributeValue.charAt(0) == ProteusConstants.REGEX_PREFIX) {
                Matcher regexMatcher = ProteusConstants.REGEX_PATTERN.matcher(attributeValue);
                String finalValue = attributeValue;

                while (regexMatcher.find()) {
                    String matchedString = regexMatcher.group(0);
                    String bindingName;
                    if (regexMatcher.group(3) != null) {

                        // has NO formatter
                        String dataPath = regexMatcher.group(3);
                        try {
                            finalValue = finalValue.replace(matchedString, Utils.getElementFromData(
                                    dataPath,
                                    parserContext.getDataContext().getDataProvider(),
                                    parserContext.getDataContext().getIndex()).getAsString());
                        } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException e) {
                            Log.e(TAG + "#findAndReplaceValues()", e.getMessage());
                            finalValue = dataPath;
                            failed = true;
                        }
                        bindingName = dataPath;
                    } else {

                        // has formatter
                        String dataPath = regexMatcher.group(1);
                        String formatterName = regexMatcher.group(2);

                        String formattedValue;
                        try {
                            formattedValue = format(Utils.getElementFromData(dataPath,
                                            parserContext.getDataContext().getDataProvider(),
                                            parserContext.getDataContext().getIndex()),
                                    formatterName);
                        } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException e) {
                            Log.e(TAG + "#findAndReplaceValues()", e.getMessage());
                            formattedValue = dataPath;
                            failed = true;
                        }
                        finalValue = finalValue.replace(matchedString, formattedValue);
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
                jsonDataValue = new JsonPrimitive(finalValue);

            } else if (attributeValue.charAt(0) == ProteusConstants.DATA_PREFIX) {
                JsonElement elementFromData;
                try {
                    elementFromData = Utils.getElementFromData(attributeValue.substring(1),
                            parserContext.getDataContext().getDataProvider(),
                            childIndex);
                } catch (JsonNullException | NoSuchDataPathException | InvalidDataPathException e) {
                    Log.e(TAG + "#findAndReplaceValues()", e.getMessage());
                    failed = true;
                    elementFromData = new JsonPrimitive(ProteusConstants.DATA_NULL);
                }

                if (elementFromData != null) {
                    jsonDataValue = elementFromData;
                }
                addBinding(dataProteusView,
                        attributeValue.substring(1),
                        attributeName,
                        attributeValue,
                        handler,
                        false);
            }
        }

        if (dataProteusView.getView() != null) {
            if (failed) {
                if (viewJsonObject != null && !viewJsonObject.isJsonNull()
                        && viewJsonObject.get(Attributes.View.Visibility.getName()) != null) {
                    String visibility = viewJsonObject.get(Attributes.View.Visibility.getName()).getAsString();
                    if (ProteusConstants.DATA_VISIBILITY.equals(visibility)) {
                        dataProteusView.getView().setVisibility(View.INVISIBLE);
                    }
                } else {
                    dataProteusView.getView().setVisibility(View.GONE);
                }
            } else if (dataProteusView.isViewUpdating()) {
                dataProteusView.getView().setVisibility(View.VISIBLE);
            }
        }

        return jsonDataValue;
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

        JsonElement scope = currentViewJsonObject.get(ProteusConstants.DATA_CONTEXT);
        if (scope == null || scope.isJsonNull()) {
            return oldParserContext;
        }

        DataContext oldDataContext = oldParserContext.getDataContext();
        if (oldDataContext.getDataProvider() == null) {
            Log.e(TAG + "#getNewParserContext()", "When scope is specified, data provider cannot be null");
            return oldParserContext;
        }

        DataContext newDataContext = oldDataContext.createChildDataContext(scope.getAsJsonObject(), childIndex);
        ParserContext newParserContext = oldParserContext.clone();
        newParserContext.setDataContext(newDataContext);

        return newParserContext;
    }

    @Override
    protected ProteusView createProteusViewToReturn(View createdView, JsonObject layout, int index, ProteusView parent) {
        return new DataProteusView(new SimpleProteusView(createdView, layout, index, parent));
    }

    @Override
    protected void prepareView(ProteusView proteusView, ParserContext parserContext) {
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

    public void getFormatter(String formatterName) {
        this.formatter.get(formatterName);
    }
}
