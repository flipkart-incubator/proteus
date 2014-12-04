package com.flipkart.layoutengine.parser;

/**
 * Created by kirankumar on 20/11/14.
 */
public class Attributes {


    public static class Webview
    {
        public static Attribute Url = new Attribute("url");

    }

    public static class View {

        /**
         * @origAttr weight
         * @newAttr weight
         */
        public static Attribute Weight = new Attribute("layout_weight");

        /**
         * @origAttr width
         * @newAttr width
         */
        public static Attribute Width = new Attribute("layout_width");

        /**
         * @origAttr background
         * @newAttr background
         */
        public static Attribute Background = new Attribute("background");

        /**
         * @origAttr height
         * @newAttr height
         */
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
        public static Attribute Alpha = new Attribute("alpha");
        public static Attribute Visibility = new Attribute("visibility");
        public static Attribute Id = new Attribute("id");
        public static Attribute Tag = new Attribute("tag");
        public static Attribute Above = new Attribute("above");
        public static Attribute AlignBaseline = new Attribute("alignBaseline");
        public static Attribute AlignBottom = new Attribute("alignBottom");
        public static Attribute AlignEnd = new Attribute("alignEnd");
        public static Attribute AlignLeft = new Attribute("alignLeft");
        public static Attribute AlignRight = new Attribute("alignRight");
        public static Attribute AlignStart = new Attribute("alignStart");
        public static Attribute AlignTop = new Attribute("alignTop");
        public static Attribute Below = new Attribute("below");
        public static Attribute ToEndOf = new Attribute("toEndOf");
        public static Attribute ToLeftOf = new Attribute("toLeftOf");
        public static Attribute ToRightOf = new Attribute("toRightOf");
        public static Attribute ToStartOf = new Attribute("toStartOf");
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

    }

    public static class RatingBar {
        public static Attribute NumStars = new Attribute("numStars");
        public static Attribute Rating = new Attribute("rating");
        public static Attribute IsIndicator = new Attribute("isIndicator");
        public static Attribute StepSize = new Attribute("stepSize");
        public static Attribute ProgressDrawable = new Attribute("progressDrawable");
        public static Attribute MinHeight = new Attribute("minHeight");
        public static Attribute MaxHeight = new Attribute("maxHeight");
    }
        public static class TextView {
        public static Attribute Gravity = new Attribute("gravity");
        public static Attribute Text = new Attribute("text");
        public static Attribute TextSize = new Attribute("textSize");
        public static Attribute TextColor = new Attribute("textColor");
        public static Attribute DrawableLeft = new Attribute("drawableLeft");
        public static Attribute DrawableRight = new Attribute("drawableRight");
        public static Attribute DrawableTop = new Attribute("drawableTop");
        public static Attribute DrawableBottom = new Attribute("drawableBottom");
        public static Attribute DrawablePadding = new Attribute("drawablePadding");
    }

    public static class FrameLayout {
        public static Attribute Gravity = new Attribute("gravity");
        public static Attribute HeightRatio = new Attribute("heightRatio");
        public static Attribute WidthRatio = new Attribute("widthRatio");
    }

    public static class ImageView {
        public static Attribute Src = new Attribute("src");
        public static Attribute ScaleType = new Attribute("scaleType");
        public static Attribute AdjustViewBounds = new Attribute("adjustViewBounds");
    }

    public static class LinearLayout {
        public static Attribute Orientation = new Attribute("orientation");
    }

    public static class NetworkImageView {
        public static Attribute ImageUrl = new Attribute("imageUrl");
    }


    public static class Attribute {
        private String name;

        public Attribute(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}