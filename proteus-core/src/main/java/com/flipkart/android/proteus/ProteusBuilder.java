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
import com.flipkart.android.proteus.parser.custom.WebViewParser;
import com.flipkart.android.proteus.toolbox.Formatter;

import java.util.HashMap;
import java.util.Map;

/**
 * ProteusBuilder
 *
 * @author aditya.sharat
 */

public class ProteusBuilder {

    private static final int ID = -1;

    /**
     *
     */
    public static final Collection DEFAULT_COLLECTION = new Collection() {

        @Override
        public void registerWith(ProteusBuilder builder) {

            // register the default parsers
            builder.register("View", new ViewParser());
            builder.register("include", new IncludeParser());
            builder.register("ViewGroup", new ViewGroupParser(), "View");
            builder.register("RelativeLayout", new RelativeLayoutParser(), "ViewGroup");
            builder.register("LinearLayout", new LinearLayoutParser(), "ViewGroup");
            builder.register("FrameLayout", new FrameLayoutParser(), "ViewGroup");
            builder.register("ScrollView", new ScrollViewParser(), "FrameLayout");
            builder.register("HorizontalScrollView", new HorizontalScrollViewParser(), "FrameLayout");
            builder.register("ImageView", new ImageViewParser(), "View");
            builder.register("TextView", new TextViewParser(), "View");
            builder.register("EditText", new EditTextParser(), "TextView");
            builder.register("Button", new ButtonParser(), "TextView");
            builder.register("ImageButton", new ImageButtonParser(), "ImageView");
            builder.register("WebView", new WebViewParser(), "View");
            builder.register("RatingBar", new RatingBarParser(), "View");
            builder.register("CheckBox", new CheckBoxParser(), "Button");
            builder.register("ProgressBar", new ProgressBarParser(), "View");
            builder.register("HorizontalProgressBar", new HorizontalProgressBarParser(), "ProgressBar");

            // register the default formatters
            builder.register(Formatter.DATE);
            builder.register(Formatter.INDEX);
            builder.register(Formatter.JOIN);
            builder.register(Formatter.NUMBER);
        }
    };

    private Map<String, Proteus.Type> types = new HashMap<>();
    private HashMap<String, Formatter> formatters = new HashMap<>();

    public ProteusBuilder() {
        DEFAULT_COLLECTION.registerWith(this);
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

    public ProteusBuilder register(Collection collection) {
        collection.registerWith(this);
        return this;
    }

    public Proteus build() {
        return new Proteus(types, formatters);
    }

    public interface Collection {

        /**
         * @param builder
         */
        void registerWith(ProteusBuilder builder);

    }
}
