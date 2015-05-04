package com.flipkart.layoutengine.builder;

import android.content.Context;

import com.flipkart.layoutengine.provider.Provider;

/**
 * @author kirankumar
 */
public interface LayoutBuilderFactory {

    LayoutBuilder createDataAndViewParsingLayoutBuilder(Context context, Provider viewProvider);
    LayoutBuilder createDataParsingLayoutBuilder(Context context);
    LayoutBuilder createSimpleLayoutBuilder(Context context);
}
