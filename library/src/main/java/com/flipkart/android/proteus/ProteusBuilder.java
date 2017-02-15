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

import android.annotation.SuppressLint;
import android.os.Build;

import com.flipkart.android.proteus.parser.IncludeParser;
import com.flipkart.android.proteus.parser.ViewParser;
import com.flipkart.android.proteus.parser.custom.ButtonParser;
import com.flipkart.android.proteus.parser.custom.CheckBoxParser;
import com.flipkart.android.proteus.parser.custom.EditTextParser;
import com.flipkart.android.proteus.parser.custom.FrameLayoutParser;
import com.flipkart.android.proteus.parser.custom.HorizontalProgressBarParser;
import com.flipkart.android.proteus.parser.custom.HorizontalScrollViewParser;
import com.flipkart.android.proteus.parser.custom.ImageButtonParser;
import com.flipkart.android.proteus.parser.custom.ImageViewParser;
import com.flipkart.android.proteus.parser.custom.LinearLayoutParser;
import com.flipkart.android.proteus.parser.custom.ProgressBarParser;
import com.flipkart.android.proteus.parser.custom.RatingBarParser;
import com.flipkart.android.proteus.parser.custom.RelativeLayoutParser;
import com.flipkart.android.proteus.parser.custom.ScrollViewParser;
import com.flipkart.android.proteus.parser.custom.TextViewParser;
import com.flipkart.android.proteus.parser.custom.ViewGroupParser;
import com.flipkart.android.proteus.parser.custom.ViewPagerParser;
import com.flipkart.android.proteus.parser.custom.WebViewParser;
import com.flipkart.android.proteus.toolbox.Formatter;
import com.flipkart.android.proteus.toolbox.Utils;
import com.google.gson.JsonElement;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ProteusBuilder
 *
 * @author aditya.sharat
 */

public class ProteusBuilder {

    private static final int ID = -1;

    private Map<String, Proteus.Type> types = new HashMap<>();
    private HashMap<String, Formatter> formatters = new HashMap<>();

    public ProteusBuilder() {
        registerDefaults();
    }

    public ProteusBuilder register(String type, ViewTypeParser parser) {
        ViewTypeParser.AttributeSet attributeSet = parser.prepare(null);
        Proteus.Type t = new Proteus.Type(ID, type, parser, attributeSet);
        types.put(type, t);
        return this;
    }

    public ProteusBuilder register(String type, ViewTypeParser parser, String parent) {
        Proteus.Type p = types.get(parent);
        if (null == p) {
            throw new IllegalStateException(parent + " is not a registered type parser");
        }
        Proteus.Type t = new Proteus.Type(ID, type, parser, parser.prepare(p.parser));
        types.put(type, t);
        return this;
    }

    public ProteusBuilder register(Formatter formatter) {
        formatters.put(formatter.getName(), formatter);
        return this;
    }

    public Proteus build() {
        return new Proteus(types, formatters);
    }

    private void registerDefaults() {

        register("View", new ViewParser());
        register("include", new IncludeParser());
        register("ViewGroup", new ViewGroupParser(), "View");
        register("RelativeLayout", new RelativeLayoutParser(), "ViewGroup");
        register("LinearLayout", new LinearLayoutParser(), "ViewGroup");
        register("FrameLayout", new FrameLayoutParser(), "ViewGroup");
        register("ScrollView", new ScrollViewParser(), "FrameLayout");
        register("HorizontalScrollView", new HorizontalScrollViewParser(), "FrameLayout");
        register("ImageView", new ImageViewParser(), "View");
        register("TextView", new TextViewParser(), "View");
        register("EditText", new EditTextParser(), "TextView");
        register("Button", new ButtonParser(), "TextView");
        register("ImageButton", new ImageButtonParser(), "ImageView");
        register("ViewPager", new ViewPagerParser(), "ViewGroup");
        register("WebView", new WebViewParser(), "View");
        register("RatingBar", new RatingBarParser(), "View");
        register("CheckBox", new CheckBoxParser(), "Button");
        register("ProgressBar", new ProgressBarParser(), "View");
        register("HorizontalProgressBar", new HorizontalProgressBarParser(), "ProgressBar");


        Formatter NumberFormatter = new Formatter() {

            private DecimalFormat formatter;

            @Override
            public String format(JsonElement elementValue) {
                double valueAsNumber;
                try {
                    valueAsNumber = Double.parseDouble(elementValue.getAsString());
                } catch (NumberFormatException e) {
                    return elementValue.toString();
                }
                formatter = new DecimalFormat("#,###");
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
                    formatter.setRoundingMode(RoundingMode.FLOOR);
                }
                formatter.setMinimumFractionDigits(0);
                formatter.setMaximumFractionDigits(2);
                return formatter.format(valueAsNumber);
            }

            @Override
            public String getName() {
                return "number";
            }
        };

        Formatter DateFormatter = new Formatter() {

            @SuppressLint("SimpleDateFormat")
            private SimpleDateFormat from = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            @SuppressLint("SimpleDateFormat")
            private SimpleDateFormat to = new SimpleDateFormat("d MMM, E");

            @Override
            public String format(JsonElement elementValue) {
                try {
                    // 2015-06-18 12:01:37
                    Date date = from.parse(elementValue.getAsString());
                    return to.format(date);
                } catch (Exception e) {
                    return elementValue.toString();
                }
            }

            @Override
            public String getName() {
                return "date";
            }
        };

        Formatter IndexFormatter = new Formatter() {
            @Override
            public String format(JsonElement elementValue) {
                int valueAsNumber;
                try {
                    valueAsNumber = Integer.parseInt(elementValue.getAsString());
                } catch (NumberFormatException e) {
                    return elementValue.toString();
                }
                return String.valueOf(valueAsNumber + 1);
            }

            @Override
            public String getName() {
                return "index";
            }
        };

        Formatter joinFormatter = new Formatter() {
            @Override
            public String format(JsonElement elementValue) {
                if (elementValue.isJsonArray()) {
                    return Utils.getStringFromArray(elementValue.getAsJsonArray(), ",");
                } else {
                    return elementValue.toString();
                }
            }

            @Override
            public String getName() {
                return "join";
            }
        };

        register(NumberFormatter);
        register(DateFormatter);
        register(IndexFormatter);
        register(joinFormatter);
    }
}
