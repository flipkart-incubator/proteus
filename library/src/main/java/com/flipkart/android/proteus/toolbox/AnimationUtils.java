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

package com.flipkart.android.proteus.toolbox;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.PathInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.flipkart.android.proteus.parser.ParseHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;

/**
 * Defines common utilities for working with animations.
 */
public class AnimationUtils {

    private static final String TAG = "AnimationUtils";

    private static final String LINEAR_INTERPOLATOR = "linearInterpolator";
    private static final String ACCELERATE_INTERPOLATOR = "accelerateInterpolator";
    private static final String DECELERATE_INTERPOLATOR = "decelerateInterpolator";
    private static final String ACCELERATE_DECELERATE_INTERPOLATOR = "accelerateDecelerateInterpolator";
    private static final String CYCLE_INTERPOLATOR = "cycleInterpolator";
    private static final String ANTICIPATE_INTERPOLATOR = "anticipateInterpolator";
    private static final String OVERSHOOT_INTERPOLATOR = "overshootInterpolator";
    private static final String ANTICIPATE_OVERSHOOT_INTERPOLATOR = "anticipateOvershootInterpolator";
    private static final String BOUNCE_INTERPOLATOR = "bounceInterpolator";
    private static final String PATH_INTERPOLATOR = "pathInterpolator";


    private static final String TYPE = "type";
    private static final String SET = "set";
    private static final String ALPHA = "alpha";
    private static final String SCALE = "scale";
    private static final String ROTATE = "rotate";
    private static final String TRANSLATE = "translate";

    private static final String PERCENT_SELF = "%";
    private static final String PERCENT_RELATIVE_PARENT = "%p";

    private static Gson sGson = new Gson();

    /**
     * Loads an {@link Animation} object from a resource
     *
     * @param context Application context used to access resources
     * @param value   JSON representation of the Animation
     * @return The animation object reference by the specified id
     * @throws android.content.res.Resources.NotFoundException when the animation cannot be loaded
     */
    public static Animation loadAnimation(Context context, JsonElement value) throws Resources.NotFoundException {
        Animation anim = null;
        if (value.isJsonPrimitive()) {
            anim = handleString(context, value.getAsString());
        } else if (value.isJsonObject()) {
            anim = handleElement(context, value.getAsJsonObject());
        } else {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, "Could not load animation for : " + value.toString());
            }
        }
        return anim;
    }

    private static Animation handleString(Context c, String value) {
        Animation anim = null;
        if (ParseHelper.isTweenAnimationResource(value)) {
            try {
                Resources r = c.getResources();
                int animationId = r.getIdentifier(value, "anim", c.getPackageName());
                anim = android.view.animation.AnimationUtils.loadAnimation(c, animationId);
            } catch (Exception ex) {
                System.out.println("Could not load local resource " + value);
            }
        }
        return anim;
    }

    private static Animation handleElement(Context c, JsonObject value) {
        Animation anim = null;
        JsonElement type = value.get(TYPE);
        String animationType = type.getAsString();
        AnimationProperties animationProperties = null;
        if (SET.equalsIgnoreCase(animationType)) {
            animationProperties = sGson.fromJson(value, AnimationSetProperties.class);
        } else if (ALPHA.equalsIgnoreCase(animationType)) {
            animationProperties = sGson.fromJson(value, AlphaAnimProperties.class);
        } else if (SCALE.equalsIgnoreCase(animationType)) {
            animationProperties = sGson.fromJson(value, ScaleAnimProperties.class);
        } else if (ROTATE.equalsIgnoreCase(animationType)) {
            animationProperties = sGson.fromJson(value, RotateAnimProperties.class);
        } else if (TRANSLATE.equalsIgnoreCase(animationType)) {
            animationProperties = sGson.fromJson(value, TranslateAnimProperties.class);
        }

        if (null != animationProperties) {
            anim = animationProperties.instantiate(c);
        }

        return anim;
    }

    /**
     * Loads an {@link Interpolator} object from a resource
     *
     * @param context Application context used to access resources
     * @param value   Json representation of the Interpolator
     * @return The animation object reference by the specified id
     * @throws android.content.res.Resources.NotFoundException
     */
    public static Interpolator loadInterpolator(Context context, JsonElement value)
            throws Resources.NotFoundException {
        Interpolator interpolator = null;
        if (value.isJsonPrimitive()) {
            interpolator = handleStringInterpolator(context, value.getAsString());
        } else if (value.isJsonObject()) {
            interpolator = handleElementInterpolator(context, value.getAsJsonObject());
        } else {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, "Could not load interpolator for : " + value.toString());
            }
        }
        return interpolator;
    }

    private static Interpolator handleStringInterpolator(Context c, String value) {
        Interpolator interpolator = null;
        if (ParseHelper.isTweenAnimationResource(value)) {
            try {
                Resources r = c.getResources();
                int interpolatorID = r.getIdentifier(value, "anim", c.getPackageName());
                interpolator = android.view.animation.AnimationUtils.loadInterpolator(c, interpolatorID);
            } catch (Exception ex) {
                System.out.println("Could not load local resource " + value);
            }
        }
        return interpolator;
    }

    private static Interpolator handleElementInterpolator(Context c, JsonObject value) {

        Interpolator interpolator = null;
        JsonElement type = value.get("type");
        String interpolatorType = type.getAsString();
        InterpolatorProperties interpolatorProperties = null;
        if (LINEAR_INTERPOLATOR.equalsIgnoreCase(interpolatorType)) {
            interpolator = new LinearInterpolator();
        } else if (ACCELERATE_INTERPOLATOR.equalsIgnoreCase(interpolatorType)) {
            interpolator = new AccelerateInterpolator();
        } else if (DECELERATE_INTERPOLATOR.equalsIgnoreCase(interpolatorType)) {
            interpolator = new DecelerateInterpolator();
        } else if (ACCELERATE_DECELERATE_INTERPOLATOR.equalsIgnoreCase(interpolatorType)) {
            interpolator = new AccelerateDecelerateInterpolator();
        } else if (CYCLE_INTERPOLATOR.equalsIgnoreCase(interpolatorType)) {
            interpolatorProperties = sGson.fromJson(value, CycleInterpolatorProperties.class);
        } else if (ANTICIPATE_INTERPOLATOR.equalsIgnoreCase(interpolatorType)) {
            interpolatorProperties = sGson.fromJson(value, AnticipateInterpolatorProperties.class);
        } else if (OVERSHOOT_INTERPOLATOR.equalsIgnoreCase(interpolatorType)) {
            interpolatorProperties = sGson.fromJson(value, OvershootInterpolatorProperties.class);
        } else if (ANTICIPATE_OVERSHOOT_INTERPOLATOR.equalsIgnoreCase(interpolatorType)) {
            interpolatorProperties = sGson.fromJson(value, AnticipateOvershootInterpolatorProperties.class);
        } else if (BOUNCE_INTERPOLATOR.equalsIgnoreCase(interpolatorType)) {
            interpolator = new BounceInterpolator();
        } else if (PATH_INTERPOLATOR.equalsIgnoreCase(interpolatorType)) {
            interpolatorProperties = sGson.fromJson(value, PathInterpolatorProperties.class);
        } else {
            if (ProteusConstants.isLoggingEnabled()) {
                Log.e(TAG, "Unknown interpolator name: " + interpolatorType);
            }
            throw new RuntimeException("Unknown interpolator name: " + interpolatorType);
        }

        if (null != interpolatorProperties) {
            interpolator = interpolatorProperties.createInterpolator(c);
        }

        return interpolator;
    }

    /**
     * Utility class to parse a string description of a size.
     */
    private static class Description {
        /**
         * One of Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, or
         * Animation.RELATIVE_TO_PARENT.
         */
        public int type;

        /**
         * The absolute or relative dimension for this Description.
         */
        public float value;

        /**
         * Size descriptions can appear in three forms:
         * <ol>
         * <li>An absolute size. This is represented by a number.</li>
         * <li>A size relative to the size of the object being animated. This
         * is represented by a number followed by "%".</li> *
         * <li>A size relative to the size of the parent of object being
         * animated. This is represented by a number followed by "%p".</li>
         * </ol>
         *
         * @param value The Json value to parse
         * @return The parsed version of the description
         */
        static Description parseValue(JsonPrimitive value) {
            Description d = new Description();
            d.type = Animation.ABSOLUTE;
            d.value = 0;
            if (value != null && (value.isNumber() || value.isString())) {
                if (value.isNumber()) {
                    d.type = Animation.ABSOLUTE;
                    d.value = value.getAsNumber().floatValue();
                } else {
                    String stringValue = value.getAsString();
                    if (stringValue.endsWith(PERCENT_SELF)) {
                        stringValue = stringValue.substring(0, stringValue.length() - PERCENT_SELF.length());
                        d.value = Float.parseFloat(stringValue) / 100;
                        d.type = Animation.RELATIVE_TO_SELF;
                    } else if (stringValue.endsWith(PERCENT_RELATIVE_PARENT)) {
                        stringValue = stringValue.substring(0, stringValue.length() - PERCENT_RELATIVE_PARENT.length());
                        d.value = Float.parseFloat(stringValue) / 100;
                        d.type = Animation.RELATIVE_TO_PARENT;
                    } else {
                        d.type = Animation.ABSOLUTE;
                        d.value = value.getAsNumber().floatValue();
                    }
                }
            }

            return d;
        }
    }

    private abstract static class AnimationProperties {

        @SerializedName("detachWallpaper")
        Boolean detachWallpaper;
        @SerializedName("duration")
        Long duration;
        @SerializedName("fillAfter")
        Boolean fillAfter;
        @SerializedName("fillBefore")
        Boolean fillBefore;
        @SerializedName("fillEnabled")
        Boolean fillEnabled;
        @SerializedName("interpolator")
        JsonElement interpolator;
        @SerializedName("repeatCount")
        Integer repeatCount;
        @SerializedName("repeatMode")
        Integer repeatMode;
        @SerializedName("startOffset")
        Long startOffset;
        @SerializedName("zAdjustment")
        Integer zAdjustment;

        public Animation instantiate(Context c) {
            Animation anim = createAnimation(c);
            if (null != anim) {
                if (null != detachWallpaper) {
                    anim.setDetachWallpaper(detachWallpaper);
                }

                if (null != duration) {
                    anim.setDuration(duration);
                }

                if (null != fillAfter) {
                    anim.setFillAfter(fillAfter);
                }

                if (null != fillBefore) {
                    anim.setFillBefore(fillBefore);
                }

                if (null != fillEnabled) {
                    anim.setFillEnabled(fillEnabled);
                }

                if (null != interpolator) {
                    Interpolator i = loadInterpolator(c, interpolator);
                    if (null != i) {
                        anim.setInterpolator(i);
                    }
                }

                if (null != repeatCount) {
                    anim.setRepeatCount(repeatCount);
                }

                if (null != repeatMode) {
                    anim.setRepeatMode(repeatMode);
                }

                if (null != startOffset) {
                    anim.setStartOffset(startOffset);
                }

                if (null != zAdjustment) {
                    anim.setZAdjustment(zAdjustment);
                }
            }
            return anim;
        }

        abstract Animation createAnimation(Context c);
    }

    private static class AnimationSetProperties extends AnimationProperties {

        @SerializedName("shareInterpolator")
        Boolean shareInterpolator;
        @SerializedName("children")
        JsonElement children;

        @Override
        Animation createAnimation(Context c) {
            AnimationSet animationSet = new AnimationSet(shareInterpolator == null ? true : shareInterpolator);


            if (null != children) {
                if (children.isJsonArray()) {
                    for (JsonElement element : children.getAsJsonArray()) {
                        Animation animation = loadAnimation(c, element);
                        if (null != animation) {
                            animationSet.addAnimation(animation);
                        }
                    }
                } else if (children.isJsonObject() || children.isJsonPrimitive()) {
                    Animation animation = loadAnimation(c, children);
                    if (null != animation) {
                        animationSet.addAnimation(animation);
                    }
                }
            }
            return animationSet;
        }
    }

    private static class AlphaAnimProperties extends AnimationProperties {

        @SerializedName("fromAlpha")
        public Float fromAlpha;
        @SerializedName("toAlpha")
        public Float toAlpha;

        @Override
        Animation createAnimation(Context c) {
            return null == fromAlpha || null == toAlpha ? null : new AlphaAnimation(fromAlpha, toAlpha);
        }
    }

    private static class ScaleAnimProperties extends AnimationProperties {

        @SerializedName("fromXScale")
        public Float fromXScale;
        @SerializedName("toXScale")
        public Float toXScale;
        @SerializedName("fromYScale")
        public Float fromYScale;
        @SerializedName("toYScale")
        public Float toYScale;
        @SerializedName("pivotX")
        public JsonPrimitive pivotX;
        @SerializedName("pivotY")
        public JsonPrimitive pivotY;

        @Override
        Animation createAnimation(Context c) {
            if (pivotX != null && pivotY != null) {
                Description pivotXDesc = Description.parseValue(pivotX);
                Description pivotYDesc = Description.parseValue(pivotY);
                return new ScaleAnimation(fromXScale, toXScale, fromYScale, toYScale, pivotXDesc.type, pivotXDesc.value, pivotYDesc.type, pivotYDesc.value);
            } else {
                return new ScaleAnimation(fromXScale, toXScale, fromYScale, toYScale);
            }
        }
    }

    private static class TranslateAnimProperties extends AnimationProperties {

        @SerializedName("fromXDelta")
        public JsonPrimitive fromXDelta;
        @SerializedName("toXDelta")
        public JsonPrimitive toXDelta;
        @SerializedName("fromYDelta")
        public JsonPrimitive fromYDelta;
        @SerializedName("toYDelta")
        public JsonPrimitive toYDelta;

        @Override
        Animation createAnimation(Context c) {
            Description fromXDeltaDescription = Description.parseValue(fromXDelta);
            Description toXDeltaDescription = Description.parseValue(toXDelta);
            Description fromYDeltaDescription = Description.parseValue(fromYDelta);
            Description toYDeltaDescription = Description.parseValue(toYDelta);

            return new TranslateAnimation(fromXDeltaDescription.type, fromXDeltaDescription.value, toXDeltaDescription.type, toXDeltaDescription.value, fromYDeltaDescription.type, fromYDeltaDescription.value, toYDeltaDescription.type, toYDeltaDescription.value);
        }
    }

    private static class RotateAnimProperties extends AnimationProperties {

        @SerializedName("fromDegrees")
        public Float fromDegrees;
        @SerializedName("toDegrees")
        public Float toDegrees;
        @SerializedName("pivotX")
        public JsonPrimitive pivotX;
        @SerializedName("pivotY")
        public JsonPrimitive pivotY;

        @Override
        Animation createAnimation(Context c) {
            if (null != pivotX && null != pivotY) {
                Description pivotXDesc = Description.parseValue(pivotX);
                Description pivotYDesc = Description.parseValue(pivotY);
                return new RotateAnimation(fromDegrees, toDegrees, pivotXDesc.type, pivotXDesc.value, pivotYDesc.type, pivotYDesc.value);
            } else {
                return new RotateAnimation(fromDegrees, toDegrees);
            }
        }
    }

    private abstract static class InterpolatorProperties {
        abstract Interpolator createInterpolator(Context c);
    }

    private static class PathInterpolatorProperties extends InterpolatorProperties {

        @SerializedName("controlX1")
        public Float controlX1;
        @SerializedName("controlY1")
        public Float controlY1;
        @SerializedName("controlX2")
        public Float controlX2;
        @SerializedName("controlY2")
        public Float controlY2;

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        Interpolator createInterpolator(Context c) {
            if (null != controlX2 && null != controlY2) {
                return new PathInterpolator(controlX1, controlY1, controlX2, controlY2);
            } else {
                return new PathInterpolator(controlX1, controlY1);
            }
        }
    }

    private static class AnticipateInterpolatorProperties extends InterpolatorProperties {

        @SerializedName("tension")
        public Float tension;

        Interpolator createInterpolator(Context c) {
            return new AnticipateInterpolator(tension);
        }
    }

    private static class OvershootInterpolatorProperties extends InterpolatorProperties {

        @SerializedName("tension")
        public Float tension;

        Interpolator createInterpolator(Context c) {
            return tension == null ? new OvershootInterpolator() : new OvershootInterpolator(tension);
        }
    }

    private static class AnticipateOvershootInterpolatorProperties extends InterpolatorProperties {

        @SerializedName("tension")
        public Float tension;
        @SerializedName("extraTension")
        public Float extraTension;

        Interpolator createInterpolator(Context c) {
            return null == tension ? new AnticipateOvershootInterpolator() : (null == extraTension ? new AnticipateOvershootInterpolator(tension) : new AnticipateOvershootInterpolator(tension, extraTension));
        }
    }

    private static class CycleInterpolatorProperties extends InterpolatorProperties {

        @SerializedName("cycles")
        public Float cycles;

        Interpolator createInterpolator(Context c) {
            return new CycleInterpolator(cycles);
        }
    }
}
