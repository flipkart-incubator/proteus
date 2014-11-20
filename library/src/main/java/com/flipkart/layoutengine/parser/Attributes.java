package com.flipkart.layoutengine.parser;

/**
 * Created by kirankumar on 20/11/14.
 */
public class Attributes {

    public static class View {
        public static Attribute Weight = new Attribute("weight");
        public static Attribute Width = new Attribute("width");
        public static Attribute Background = new Attribute("background");
        public static Attribute Height = new Attribute("height");
        public static Attribute LayoutGravity = new Attribute("layout_gravity");
        public static Attribute Gravity = new Attribute("gravity");
        public static Attribute Padding = new Attribute("padding");
        public static Attribute PaddingLeft = new Attribute("paddingLeft");
        public static Attribute PaddingTop = new Attribute("paddingTop");
        public static Attribute PaddingRight = new Attribute("paddingRight");
        public static Attribute PaddingBottom = new Attribute("paddingBottom");
        public static Attribute Margin = new Attribute("margin");
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
        public static Attribute AlignParentBottom = new Attribute("alignParentBottom");
        public static Attribute AlignParentEnd = new Attribute("alignParentEnd");
        public static Attribute AlignParentLeft = new Attribute("alignParentLeft");
        public static Attribute AlignParentRight = new Attribute("alignParentRight");
        public static Attribute AlignParentStart = new Attribute("alignParentStart");
        public static Attribute AlignParentTop = new Attribute("alignParentTop");
        public static Attribute CenterHorizontal = new Attribute("centerHorizontal");
        public static Attribute CenterInParent = new Attribute("centerInParent");
        public static Attribute CenterVertical = new Attribute("centerVertical");
    }

    public static class TextView {
        public static Attribute Gravity = new Attribute("gravity");
        public static Attribute Text = new Attribute("text");
        public static Attribute TextSize = new Attribute("textSize");
        public static Attribute TextColor = new Attribute("textColor");
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