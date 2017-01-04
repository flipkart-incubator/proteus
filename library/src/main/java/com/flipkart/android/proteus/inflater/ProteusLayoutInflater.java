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

package com.flipkart.android.proteus.inflater;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.android.proteus.Layout;
import com.flipkart.android.proteus.Value;
import com.flipkart.android.proteus.parser.TypeParser;
import com.flipkart.android.proteus.toolbox.BitmapLoader;
import com.flipkart.android.proteus.toolbox.IdGenerator;
import com.flipkart.android.proteus.toolbox.LayoutInflaterCallback;
import com.flipkart.android.proteus.toolbox.Styles;
import com.flipkart.android.proteus.view.ProteusView;
import com.google.gson.JsonObject;

/**
 * @author kirankumar
 * @author aditya.sharat
 */
public interface ProteusLayoutInflater {

    /**
     * Register {@link TypeParser}s for custom view types.
     *
     * @param type   The name of the view type.
     * @param parser The {@link TypeParser} to use while building this view type.
     */
    void registerParser(String type, TypeParser parser);

    /**
     * Un-Register the {@link TypeParser} registered using {@link ProteusLayoutInflater#registerParser}.
     *
     * @param type remove {@link TypeParser} for the specified view type.
     */
    void unregisterParser(String type);

    /**
     * Returns the {@link TypeParser} for the specified view type.
     *
     * @param type The name of the view type.
     * @return The {@link TypeParser} associated to the specified view type
     */
    TypeParser getParser(String type);

    /**
     * This method is used to process the attributes from the layout and set them on the {@link View}
     * that is being built.
     *
     * @param handler
     * @param view      The view to handle the attribute on.
     * @param attribute
     * @param value     @return true if the attribute is processed false otherwise.
     */
    boolean handleAttribute(TypeParser handler, ProteusView view, int attribute, Value value);

    /**
     * This methods builds a {@link ProteusView} from a layout {@link JsonObject} and data {@link JsonObject}.
     *
     * @param parent The intended parent view for the {@link View} that will be built.
     * @param layout The {@link Layout} which defines the layout for the {@link View} to be built.
     * @param data   The {@link JsonObject} which will be used to replace bindings with values in the {@link View}.
     * @param styles The styles to be applied to the view.
     * @param index  The index of this view in its parent. Pass 0 if it has no parent.
     * @return A {@link ProteusView} with the built view, an array of its children and optionally its bindings.
     */
    ProteusView inflate(ViewGroup parent, Layout layout, JsonObject data, Styles styles, int index);

    /**
     * Give the View ID for this string. This will generally be given by the instance of ID Generator
     * which will be available with the Layout Builder.
     * This is similar to R.id auto generated
     *
     * @return int value for this id. This will never be -1.
     */
    int getUniqueViewId(String id);

    /**
     * @param attribute
     * @param type
     * @return
     */
    int getAttributeId(String attribute, String type);

    /**
     * @param type
     * @param include
     * @return
     */
    @Nullable
    Layout onIncludeLayout(String type, Layout include);

    /**
     * All consumers of this should ensure that they save the instance state of the ID generator along with the activity/
     * fragment and resume it when the Layout Builder is being re-initialized
     *
     * @return Returns the Id Generator for this Layout Builder
     */
    IdGenerator getIdGenerator();

    /**
     * @return The callback object used by this {@link ProteusLayoutInflater}
     */
    LayoutInflaterCallback getCallback();

    /**
     * Used to set a callback object to handle unknown view types and unknown attributes and other
     * exceptions. This callback is also used for requesting {@link android.support.v4.view.PagerAdapter}s
     * and {@link android.widget.Adapter}s
     *
     * @param listener The callback object.
     */
    void setCallback(LayoutInflaterCallback listener);

    /**
     * @return The helper object that is being used to handle drawables that need to fetched from a
     * network.
     */
    BitmapLoader getBitmapLoader();

    /**
     * All network bitmap calls will be handed over to this loader. This method is used to
     * set the {@link com.flipkart.android.proteus.toolbox.BitmapLoader} for the
     * {@link ProteusLayoutInflater}
     *
     * @param bitmapLoader {@link com.flipkart.android.proteus.toolbox.BitmapLoader} to use for
     *                     loading images.
     */
    void setBitmapLoader(BitmapLoader bitmapLoader);

    /**
     * @return true when rendering preview immediately by this {@link ProteusLayoutInflater} synchronously
     * otherwise false.
     */
    boolean isSynchronousRendering();

    /**
     * Set this to true for rendering preview immediately. This is to be used to decide whether
     * remote resources like remote images are to be downloaded synchronously or not
     */
    void setSynchronousRendering(boolean isSynchronousRendering);
}
