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
    public static final Module DEFAULT_MODULE = new Module() {

        @Override
        public void registerWith(ProteusBuilder builder) {

            // register the default parsers
            builder.register(new ViewParser());
            builder.register(new IncludeParser());
            builder.register(new ViewGroupParser());
            builder.register(new RelativeLayoutParser());
            builder.register(new LinearLayoutParser());
            builder.register(new FrameLayoutParser());
            builder.register(new ScrollViewParser());
            builder.register(new HorizontalScrollViewParser());
            builder.register(new ImageViewParser());
            builder.register(new TextViewParser());
            builder.register(new EditTextParser());
            builder.register(new ButtonParser());
            builder.register(new ImageButtonParser());
            builder.register(new WebViewParser());
            builder.register(new RatingBarParser());
            builder.register(new CheckBoxParser());
            builder.register(new ProgressBarParser());
            builder.register(new HorizontalProgressBarParser());

            // register the default formatters
            builder.register(Formatter.DATE);
            builder.register(Formatter.INDEX);
            builder.register(Formatter.JOIN);
            builder.register(Formatter.NUMBER);
            builder.register(Formatter.ADD);
            builder.register(Formatter.SUBTRACT);
            builder.register(Formatter.MULTIPLY);
            builder.register(Formatter.DIVIDE);
            builder.register(Formatter.MODULO);
            builder.register(Formatter.AND);
            builder.register(Formatter.OR);
            builder.register(Formatter.NOT);
            builder.register(Formatter.EQUALS);
            builder.register(Formatter.LESS_THAN);
            builder.register(Formatter.GREATER_THAN);
            builder.register(Formatter.LESS_THAN_OR_EQUALS);
            builder.register(Formatter.GREATER_THAN_OR_EQUALS);
        }
    };

    private Map<String, Proteus.Type> types = new HashMap<>();
    private HashMap<String, Formatter> formatters = new HashMap<>();

    public ProteusBuilder() {
        DEFAULT_MODULE.registerWith(this);
    }

    public ProteusBuilder register(ViewTypeParser parser) {
        String parentType = parser.getParentType();
        ViewTypeParser parentParser = null;
        if (null != parentType) {
            Proteus.Type p = types.get(parentType);
            if (null == p) {
                throw new IllegalStateException(parentType + " is not a registered type parser");
            }
            parentParser = p.parser;
        }
        Proteus.Type t = new Proteus.Type(ID, parser.getType(), parser, parser.prepare(parentParser));
        types.put(parser.getType(), t);
        return this;
    }

    public ProteusBuilder register(Formatter formatter) {
        formatters.put(formatter.getName(), formatter);
        return this;
    }

    public ProteusBuilder register(Module module) {
        module.registerWith(this);
        return this;
    }

    public Proteus build() {
        return new Proteus(types, formatters);
    }

    public interface Module {

        /**
         * @param builder
         */
        void registerWith(ProteusBuilder builder);

    }
}
