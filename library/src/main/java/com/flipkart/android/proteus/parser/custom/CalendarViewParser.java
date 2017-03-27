package com.flipkart.android.proteus.parser.custom;

/**
 * Created by mac on 3/24/17.
 */

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.flipkart.android.proteus.parser.Attributes;
import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.parser.Parser;
import com.flipkart.android.proteus.parser.WrappableParser;
import com.flipkart.android.proteus.processor.ColorResourceProcessor;
import com.flipkart.android.proteus.processor.DrawableResourceProcessor;
import com.flipkart.android.proteus.processor.JsonDataProcessor;
import com.flipkart.android.proteus.processor.StyleProcessor;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.toolbox.Utils;
import com.flipkart.android.proteus.view.ProteusCalendarView;
import com.flipkart.android.proteus.view.ProteusView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarViewParser<T extends CalendarViewParser> extends WrappableParser<View> {

    public CalendarViewParser(Parser<View> wrappedParser) {
        super(wrappedParser);
    }

    @Override
    public ProteusView createView(ViewGroup parent, JsonObject layout, JsonObject data, Styles styles, int index) {
        return new ProteusCalendarView(parent.getContext());
    }

    @Override
    protected void prepareHandlers() {
        super.prepareHandlers();
        addHandler(Attributes.CalendarView.DateTextAppearance, new StyleProcessor<View>() {
            @Override
            public void setResource(int resourceID, View view) {
                if (resourceID != -1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ((CalendarView) view).setDateTextAppearance(resourceID);
                }
            }
        });
        addHandler(Attributes.CalendarView.FirstDayOfWeek, new JsonDataProcessor<View>() {
            @Override
            public void handle(String key, JsonElement value, View view) {
                if (value != null) {
                    int firstDayOfWeek = Integer.parseInt(value.getAsString());
                    ((CalendarView) view).setFirstDayOfWeek(firstDayOfWeek);
                }
            }
        });
        addHandler(Attributes.CalendarView.FocusedMonthDateColor, new JsonDataProcessor<View>() {
            @Override
            public void handle(String key, JsonElement attributeValue, View view) {
                int focusedMonthColor = Color.TRANSPARENT;
                if (attributeValue != null) {
                    focusedMonthColor = ParseHelper.parseColor(attributeValue.getAsString());
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ((CalendarView) view).setFocusedMonthDateColor(focusedMonthColor);
                }
            }
        });

        addHandler(Attributes.CalendarView.MaxDate, new JsonDataProcessor<View>() {
            @Override
            public void handle(String key, JsonElement attributeValue, View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    Date maxDate = sdf.parse(attributeValue.getAsString());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((CalendarView) view).setMaxDate(maxDate.getTime());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        addHandler(Attributes.CalendarView.MinDate, new JsonDataProcessor<View>() {
            @Override
            public void handle(String key, JsonElement attributeValue, View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    Date minDate = sdf.parse(attributeValue.getAsString());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((CalendarView) view).setMinDate(minDate.getTime());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        addHandler(Attributes.CalendarView.SelectedDateVerticalBar, new DrawableResourceProcessor<View>() {
            @Override
            public void setDrawable(View view, Drawable drawable) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ((CalendarView) view).setSelectedDateVerticalBar(drawable);
                }
            }
        });

        addHandler(Attributes.CalendarView.SelectedWeekBackgroundColor, new ColorResourceProcessor<View>() {

            @Override
            public void setColor(View view, int color) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ((CalendarView) view).setSelectedWeekBackgroundColor(color);
                }
            }

            @Override
            public void setColor(View view, ColorStateList colors) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    //Does not support ColorStateList
                }
            }
        });

        addHandler(Attributes.CalendarView.ShowWeekNumber, new JsonDataProcessor<View>() {
            @Override
            public void handle(String key, JsonElement attributeValue, View view) {
                boolean showWeekNumber = false;
                if (attributeValue != null) {
                    showWeekNumber = ParseHelper.parseBoolean(attributeValue.getAsString());
                }
                if (showWeekNumber) {
                    ((CalendarView) view).setShowWeekNumber(showWeekNumber);
                }
            }
        });

        addHandler(Attributes.CalendarView.ShownWeekCount, new JsonDataProcessor<View>() {
            @Override
            public void handle(String key, JsonElement attributeValue, View view) {
                if (attributeValue != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ((CalendarView) view).setShownWeekCount(ParseHelper.parseInt(attributeValue.getAsString()));
                }
            }
        });
    }
}
