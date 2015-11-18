package com.flipkart.layoutengine.toolbox;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
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

import com.flipkart.layoutengine.parser.ParseHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines common utilities for working with animations.
 */
public class AnimationUtils {

    private static final String TAG = AnimationUtils.class.getSimpleName();
    private Logger mLogger = LoggerFactory.getLogger(AnimationUtils.class);

    private abstract static class AnimationProperties {
        Integer duration;

        abstract Animation createAnimation(Context c);
    }

    private static class AnimationSetProperties extends AnimationProperties {
        JsonElement interpolator;
        Boolean shareInterpolator;
        JsonElement children;

        @Override
        Animation createAnimation(Context c) {
            AnimationSet animationSet = new AnimationSet(shareInterpolator == null ? true : shareInterpolator);
            if (null != interpolator) {
                Interpolator i = loadInterpolator(c, interpolator);
                if (null != i) {
                    animationSet.setInterpolator(i);
                }
            }

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
        public Float fromAlpha;
        public Float toAlpha;

        @Override
        Animation createAnimation(Context c) {
            return new AlphaAnimation(fromAlpha, toAlpha);
        }
    }

    private static class ScaleAnimProperties extends AnimationProperties {
        public Float fromXScale;
        public Float toXScale;
        public Float fromYScale;
        public Float toYScale;
        public Float pivotX;
        public Float pivotY;

        @Override
        Animation createAnimation(Context c) {
            if (pivotX != null && pivotY != null) {
                return new ScaleAnimation(fromXScale, toXScale, fromYScale, toYScale, pivotX, pivotY);
            } else {
                return new ScaleAnimation(fromXScale, toXScale, fromYScale, toYScale);
            }
        }
    }

    private static class TranslateAnimProperties extends AnimationProperties {
        public Float fromXDelta;
        public Float toXDelta;
        public Float fromYDelta;
        public Float toYDelta;

        @Override
        Animation createAnimation(Context c) {
            return new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        }
    }

    private static class RotateAnimProperties extends AnimationProperties {
        public Float fromDegrees;
        public Float toDegrees;
        public Float pivotX;
        public Float pivotY;

        @Override
        Animation createAnimation(Context c) {
            return new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        }
    }

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

        }
        return anim;
    }

    private static Animation handleString(Context c, String value) {
        Animation anim = null;
        if (ParseHelper.isTweenAnimationResource(value)) {
            try {
                Resources r = c.getResources();
                int animationId = r.getIdentifier(value, "anim", c.getPackageName());
                anim = android.view.animation.AnimationUtils.loadAnimation(c,animationId);
            } catch (Exception ex) {
                System.out.println("Could not load local resource " + value);
            }
        }
        return anim;
    }

    private static Animation handleElement(Context c, JsonObject value) {
        Animation anim = null;
        JsonElement type = value.get("type");
        String animationType = type.getAsString();
        AnimationProperties animationProperties = null;
        if ("set".equalsIgnoreCase(animationType)) {
            animationProperties = sGson.fromJson(value, AnimationSetProperties.class);
        } else if ("alpha".equalsIgnoreCase(animationType)) {
            animationProperties = sGson.fromJson(value, AlphaAnimProperties.class);
        } else if ("scale".equalsIgnoreCase(animationType)) {
            animationProperties = sGson.fromJson(value, ScaleAnimProperties.class);
        } else if ("rotate".equalsIgnoreCase(animationType)) {
            animationProperties = sGson.fromJson(value, RotateAnimProperties.class);
        } else if ("translate".equalsIgnoreCase(animationType)) {
            animationProperties = sGson.fromJson(value, TranslateAnimProperties.class);
        }

        if (null != animationProperties) {
            anim = animationProperties.createAnimation(c);

            if (null != anim && null != animationProperties.duration) {
                anim.setDuration(animationProperties.duration);
            }
        }

        return anim;
    }

    private abstract static class InterpolatorProperties {
        abstract Interpolator createInterpolator(Context c);
    }


    private abstract static class PathInterpolatorProperties extends InterpolatorProperties {
        public Float controlX1;
        public Float controlY1;
        public Float controlX2;
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

    private abstract static class AnticipateInterpolatorProperties extends InterpolatorProperties {
        public Float tension;

        Interpolator createInterpolator(Context c) {
            return new AnticipateInterpolator(tension);
        }
    }

    private abstract static class OvershootInterpolatorProperties extends InterpolatorProperties {
        public Float tension;

        Interpolator createInterpolator(Context c) {
            return tension == null ? new OvershootInterpolator() : new OvershootInterpolator(tension);
        }
    }

    private abstract static class AnticipateOvershootInterpolatorProperties extends InterpolatorProperties {
        public Float tension;
        public Float extraTension;

        Interpolator createInterpolator(Context c) {
            return null == tension ? new AnticipateOvershootInterpolator() : (null == extraTension ? new AnticipateOvershootInterpolator(tension) : new AnticipateOvershootInterpolator(tension, extraTension));
        }
    }

    private abstract static class CycleInterpolatorProperties extends InterpolatorProperties {
        public Float cycles;

        Interpolator createInterpolator(Context c) {
            return new CycleInterpolator(cycles);
        }
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
        if ("linearInterpolator".equalsIgnoreCase(interpolatorType)) {
            interpolator = new LinearInterpolator();
        } else if ("accelerateInterpolator".equalsIgnoreCase(interpolatorType)) {
            interpolator = new AccelerateInterpolator();
        } else if ("decelerateInterpolator".equalsIgnoreCase(interpolatorType)) {
            interpolator = new DecelerateInterpolator();
        } else if ("accelerateDecelerateInterpolator".equalsIgnoreCase(interpolatorType)) {
            interpolator = new AccelerateDecelerateInterpolator();
        } else if ("cycleInterpolator".equalsIgnoreCase(interpolatorType)) {
            interpolatorProperties = sGson.fromJson(value,CycleInterpolatorProperties.class);
        } else if ("anticipateInterpolator".equalsIgnoreCase(interpolatorType)) {
            interpolatorProperties = sGson.fromJson(value,AnticipateInterpolatorProperties.class);
        } else if ("overshootInterpolator".equalsIgnoreCase(interpolatorType)) {
            interpolatorProperties = sGson.fromJson(value,OvershootInterpolatorProperties.class);
        } else if ("anticipateOvershootInterpolator".equalsIgnoreCase(interpolatorType)) {
            interpolatorProperties = sGson.fromJson(value,AnticipateOvershootInterpolatorProperties.class);
        } else if ("bounceInterpolator".equalsIgnoreCase(interpolatorType)) {
            interpolator = new BounceInterpolator();
        } else if ("pathInterpolator".equalsIgnoreCase(interpolatorType)) {
            interpolatorProperties = sGson.fromJson(value,PathInterpolatorProperties.class);
        } else {
            throw new RuntimeException("Unknown interpolator name: " + interpolatorType);
        }

        if(null != interpolatorProperties)
        {
            interpolator = interpolatorProperties.createInterpolator(c);
        }

        return interpolator;
    }
}
