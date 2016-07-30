/*
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.android.proteus.parser;

import com.flipkart.android.proteus.parser.Attributes.Attribute.Priority;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kirankumar
 * @author aditya.sharat
 */
public class Attributes {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {

        JsonObject output = new JsonObject();
        JsonObject priorities = new JsonObject();
        Map<Integer, String> map = new HashMap<>();

        Field[] fields = Priority.class.getFields();
        for (Field field : fields) {
            if (field.getName().equals("value")) {
                continue;
            }
            Priority priority = (Priority) Priority.class.getField(field.getName()).get(new Priority(0));
            priorities.addProperty(field.getName(), priority.value);
            map.put(priority.value, field.getName());
        }

        JsonObject attributes = new JsonObject();
        Class<?>[] list = Attributes.class.getDeclaredClasses();
        for (Class type : list) {
            if (type.equals(Attribute.class)) {
                continue;
            }
            for (Field field : type.getFields()) {
                Attribute attribute = (Attribute) type.getField(field.getName()).get(null);
                JsonObject value = new JsonObject();
                value.addProperty("priority", map.get(attribute.getPriority().value));
                attributes.add(attribute.getName(), value);
            }
        }

        output.add("all", attributes);
        output.add("priority", priorities);

        System.out.println(output.toString());
    }

    public static class View {
        public static Attribute Weight = new Attribute("layout_weight");
        public static Attribute Width = new Attribute("layout_width");
        public static Attribute Background = new Attribute("background");
        public static Attribute Height = new Attribute("layout_height");
        public static Attribute LayoutGravity = new Attribute("layout_gravity");
        public static Attribute Gravity = new Attribute("gravity");
        public static Attribute Padding = new Attribute("padding");
        public static Attribute PaddingLeft = new Attribute("paddingLeft");
        public static Attribute PaddingTop = new Attribute("paddingTop");
        public static Attribute PaddingRight = new Attribute("paddingRight");
        public static Attribute PaddingBottom = new Attribute("paddingBottom");
        public static Attribute Margin = new Attribute("layout_margin");
        public static Attribute MarginLeft = new Attribute("layout_marginLeft");
        public static Attribute MarginTop = new Attribute("layout_marginTop");
        public static Attribute MarginRight = new Attribute("layout_marginRight");
        public static Attribute MarginBottom = new Attribute("layout_marginBottom");
        public static Attribute MinHeight = new Attribute("minHeight");
        public static Attribute MinWidth = new Attribute("minWidth");
        public static Attribute Elevation = new Attribute("elevation");
        public static Attribute Alpha = new Attribute("alpha");
        public static Attribute Visibility = new Attribute("visibility");
        public static Attribute Invisibility = new Attribute("invisibility");
        public static Attribute Id = new Attribute("id");
        public static Attribute Tag = new Attribute("tag");
        public static Attribute Above = new Attribute("layout_above");
        public static Attribute AlignBaseline = new Attribute("layout_alignBaseline");
        public static Attribute AlignBottom = new Attribute("layout_alignBottom");
        public static Attribute AlignEnd = new Attribute("layout_alignEnd");
        public static Attribute AlignLeft = new Attribute("layout_alignLeft");
        public static Attribute AlignRight = new Attribute("layout_alignRight");
        public static Attribute AlignStart = new Attribute("layout_alignStart");
        public static Attribute AlignTop = new Attribute("layout_alignTop");
        public static Attribute Below = new Attribute("layout_below");
        public static Attribute ToEndOf = new Attribute("layout_toEndOf");
        public static Attribute ToLeftOf = new Attribute("layout_toLeftOf");
        public static Attribute ToRightOf = new Attribute("layout_toRightOf");
        public static Attribute ToStartOf = new Attribute("layout_toStartOf");
        public static Attribute AlignParentBottom = new Attribute("layout_alignParentBottom");
        public static Attribute AlignParentEnd = new Attribute("layout_alignParentEnd");
        public static Attribute AlignParentLeft = new Attribute("layout_alignParentLeft");
        public static Attribute AlignParentRight = new Attribute("layout_alignParentRight");
        public static Attribute AlignParentStart = new Attribute("layout_alignParentStart");
        public static Attribute AlignParentTop = new Attribute("layout_alignParentTop");
        public static Attribute CenterHorizontal = new Attribute("layout_centerHorizontal");
        public static Attribute CenterInParent = new Attribute("layout_centerInParent");
        public static Attribute CenterVertical = new Attribute("layout_centerVertical");
        public static Attribute ContentDescription = new Attribute("contentDescription");
        public static Attribute Clickable = new Attribute("clickable");
        public static Attribute OnClick = new Attribute("onClick");
        public static Attribute Border = new Attribute("border");
        public static Attribute TransitionName = new Attribute("transitionName");
        public static Attribute Animation = new Attribute("animation");
        public static Attribute RequiresFadingEdge = new Attribute("requiresFadingEdge");
        public static Attribute FadingEdgeLength = new Attribute("fadingEdgeLength");
        public static Attribute TextAlignment = new Attribute("textAlignment");

        /**
         * Meta Attributes
         */
        public static Attribute Type = new Attribute("type", Priority.HIGHEST);
        public static Attribute DataContext = new Attribute("dataContext", Priority.HIGHEST);
        public static Attribute Children = new Attribute("children", Priority.LOWEST);
        public static Attribute Enabled = new Attribute("enabled", Priority.LOW);
        public static Attribute Style = new Attribute("style", Priority.MEDIUM);
    }

    public static class WebView {
        public static Attribute Url = new Attribute("url");
        public static Attribute HTML = new Attribute("html");
    }

    public static class RatingBar {
        public static Attribute NumStars = new Attribute("numStars");
        public static Attribute Rating = new Attribute("rating");
        public static Attribute IsIndicator = new Attribute("isIndicator");
        public static Attribute StepSize = new Attribute("stepSize");
        public static Attribute ProgressDrawable = new Attribute("progressDrawable");
        public static Attribute MinHeight = new Attribute("minHeight");
    }

    public static class TextView {
        public static Attribute Gravity = new Attribute("gravity");
        public static Attribute Text = new Attribute("text");
        public static Attribute HTML = new Attribute("html");
        public static Attribute TextSize = new Attribute("textSize");
        public static Attribute TextColor = new Attribute("textColor");
        public static Attribute TextColorHint = new Attribute("textColorHint");
        public static Attribute TextColorLink = new Attribute("textColorLink");
        public static Attribute TextColorHighLight = new Attribute("textColorHighlight");
        public static Attribute DrawableLeft = new Attribute("drawableLeft");
        public static Attribute DrawableRight = new Attribute("drawableRight");
        public static Attribute DrawableTop = new Attribute("drawableTop");
        public static Attribute DrawableBottom = new Attribute("drawableBottom");
        public static Attribute DrawablePadding = new Attribute("drawablePadding");
        public static Attribute MaxLines = new Attribute("maxLines");
        public static Attribute Ellipsize = new Attribute("ellipsize");
        public static Attribute PaintFlags = new Attribute("paintFlags");
        public static Attribute Prefix = new Attribute("prefix");
        public static Attribute Suffix = new Attribute("suffix");
        public static Attribute TextStyle = new Attribute("textStyle");
        public static Attribute SingleLine = new Attribute("singleLine");
        public static Attribute TextAllCaps = new Attribute("textAllCaps");
        public static Attribute Hint = new Attribute("hint");
    }

    public static class CheckBox {
        public static Attribute Checked = new Attribute("checked");
        public static Attribute Button = new Attribute("button");
    }

    public static class FrameLayout {
        public static Attribute HeightRatio = new Attribute("heightRatio");
        public static Attribute WidthRatio = new Attribute("widthRatio");
    }

    public static class ImageView {
        public static Attribute Src = new Attribute("src");
        public static Attribute ScaleType = new Attribute("scaleType");
        public static Attribute AdjustViewBounds = new Attribute("adjustViewBounds");
    }

    public static class ViewGroup {
        public static Attribute ClipChildren = new Attribute("clipChildren");
        public static Attribute ClipToPadding = new Attribute("clipToPadding");
        public static Attribute LayoutMode = new Attribute("layoutMode");
        public static Attribute SplitMotionEvents = new Attribute("splitMotionEvents");
    }

    public static class LinearLayout {
        public static Attribute Orientation = new Attribute("orientation");
        public static Attribute Divider = new Attribute("divider");
        public static Attribute DividerPadding = new Attribute("dividerPadding");
        public static Attribute ShowDividers = new Attribute("showDividers");
        public static Attribute WeightSum = new Attribute("weightSum");
    }

    public static class NetworkImageView {
        public static Attribute ImageUrl = new Attribute("imageUrl");
    }

    public static class ScrollView {
        public static Attribute Scrollbars = new Attribute("scrollbars");
    }

    public static class HorizontalScrollView {
        public static Attribute FillViewPort = new Attribute("fillViewPort");
    }

    public static class ProgressBar {
        public static Attribute Progress = new Attribute("progress");
        public static Attribute Max = new Attribute("max");
        public static Attribute ProgressTint = new Attribute("progressTint");
        public static Attribute IndeterminateTint   = new Attribute("indeterminateTint");
        public static Attribute SecondaryProgressTint   = new Attribute("secondaryProgressTint");
    }

    public static class Attribute {

        private final String name;
        private final Priority priority;

        public Attribute(String name) {
            this.name = name;
            this.priority = Priority.HIGH;
        }

        public Attribute(String name, Priority priority) {
            this.name = name;
            this.priority = priority;
        }

        public String getName() {
            return name;
        }

        public Priority getPriority() {
            return this.priority;
        }

        public static class Priority {
            public static Priority HIGHEST = new Priority(0);
            public static Priority HIGH = new Priority(1000);
            public static Priority MEDIUM = new Priority(2000);
            public static Priority LOW = new Priority(3000);
            public static Priority LOWEST = new Priority(4000);

            public final int value;

            public Priority(int i) {
                value = i;
            }
        }
    }
}