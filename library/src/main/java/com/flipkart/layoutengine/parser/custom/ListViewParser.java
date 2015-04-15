package com.flipkart.layoutengine.parser.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.AbsListView;
import android.widget.ListView;

import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.parser.Attributes;
import com.flipkart.layoutengine.parser.Parser;
import com.flipkart.layoutengine.parser.WrappableParser;
import com.flipkart.layoutengine.processor.JsonDataProcessor;
import com.flipkart.layoutengine.processor.ResourceReferenceProcessor;
import com.flipkart.layoutengine.processor.StringAttributeProcessor;
import com.flipkart.layoutengine.widgets.ListViewAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * The layout handler for {@link android.widget.ListView}
 *
 * @author Aditya Sharat {@literal <aditya.sharat@flipkart.com>}
 */
public class ListViewParser<T extends ListView> extends WrappableParser<T> {

    public ListViewParser(Parser<T> wrappedParser) {
        super(ListView.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers(final Context context) {
        super.prepareHandlers(context);

        addHandler(Attributes.ListView.ListViewData, new JsonDataProcessor<T>() {
            @Override
            public void handleData(ParserContext parserContext, String attributeKey, JsonObject attributeData, T view) {
                JsonObject listViewLayout = attributeData.getAsJsonObject("layout");
                JsonElement dataContext = attributeData.get("dataContext");
                ListViewAdapter listViewAdapter = new ListViewAdapter(context, parserContext, listViewLayout, dataContext);
            }
        });

        addHandler(Attributes.ListView.CacheColorHint, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setCacheColorHint(Color.parseColor(attributeValue));
            }
        });

        addHandler(Attributes.ListView.ChoiceMode, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                    switch (attributeKey) {
                        case "0":
                            view.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
                            break;
                        case "1":
                            view.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                            break;
                        case "2":
                            view.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                            break;
                        case "3":
                            view.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
                            break;
                        default:
                            view.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
                            break;
                    }
                }
            }
        });

        addHandler(Attributes.ListView.Divider, new ResourceReferenceProcessor<T>(context) {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                    view.setDivider(drawable);
                }
            }
        });

        addHandler(Attributes.ListView.DividerHeight, new StringAttributeProcessor<T>() {
            @Override
            public void handle(ParserContext parserContext, String attributeKey, String attributeValue, T view) {
                view.setDividerHeight(Integer.parseInt(attributeValue));
            }
        });

        /*addHandler(Attributes.ListView.DrawSelectorOnTop, new StringAttributeProcessor<T>() {
                    @Override
                    public void handle(ParserContext parserContext, String attributeKey, String
                            attributeValue, T view) {
                        view.setCacheColorHint(Color.parseColor(attributeValue));
                    }
                });

        addHandler(Attributes.ListView.Entries, new StringAttributeProcessor<T>() {
                    @Override
                    public void handle(ParserContext parserContext, String attributeKey, String
                            attributeValue, T view) {
                        view.setCacheColorHint(Color.parseColor(attributeValue));
                    }
                }

        );

        addHandler(Attributes.ListView.FastScrollEnabled, new StringAttributeProcessor<T>() {
                    @Override
                    public void handle(ParserContext parserContext, String attributeKey, String
                            attributeValue, T view) {
                        view.setCacheColorHint(Color.parseColor(attributeValue));
                    }
                }

        );

        addHandler(Attributes.ListView.FooterDividersEnabled, new StringAttributeProcessor<T>() {
                    @Override
                    public void handle(ParserContext parserContext, String attributeKey, String
                            attributeValue, T view) {
                        view.setCacheColorHint(Color.parseColor(attributeValue));
                    }
                }

        );

        addHandler(Attributes.ListView.HeaderDividersEnabled, new StringAttributeProcessor<T>() {
                    @Override
                    public void handle(ParserContext parserContext, String attributeKey, String
                            attributeValue, T view) {
                        view.setCacheColorHint(Color.parseColor(attributeValue));
                    }
                }

        );

        addHandler(Attributes.ListView.ListSelector, new StringAttributeProcessor<T>() {
                    @Override
                    public void handle(ParserContext parserContext, String attributeKey, String
                            attributeValue, T view) {
                        view.setCacheColorHint(Color.parseColor(attributeValue));
                    }
                }

        );

        addHandler(Attributes.ListView.ScrollingCache, new StringAttributeProcessor<T>() {
                    @Override
                    public void handle(ParserContext parserContext, String attributeKey, String
                            attributeValue, T view) {
                        view.setCacheColorHint(Color.parseColor(attributeValue));
                    }
                }

        );

        addHandler(Attributes.ListView.SmoothScrollbar, new StringAttributeProcessor<T>() {
                    @Override
                    public void handle(ParserContext parserContext, String attributeKey, String
                            attributeValue, T view) {
                        view.setCacheColorHint(Color.parseColor(attributeValue));
                    }
                }

        );

        addHandler(Attributes.ListView.StackFromBottom, new StringAttributeProcessor<T>() {
                    @Override
                    public void handle(ParserContext parserContext, String attributeKey, String
                            attributeValue, T view) {
                        view.setCacheColorHint(Color.parseColor(attributeValue));
                    }
                }

        );

        addHandler(Attributes.ListView.TextFilterEnabled, new StringAttributeProcessor<T>() {
                    @Override
                    public void handle(ParserContext parserContext, String attributeKey, String
                            attributeValue, T view) {
                        view.setCacheColorHint(Color.parseColor(attributeValue));
                    }
                }

        );

        addHandler(Attributes.ListView.TranscriptMode, new StringAttributeProcessor<T>() {
                    @Override
                    public void handle(ParserContext parserContext, String attributeKey, String
                            attributeValue, T view) {
                        view.setCacheColorHint(Color.parseColor(attributeValue));
                    }
                }

        );*/
    }
}
