package com.flipkart.layoutengine.builder;

import android.content.Context;

import com.flipkart.layoutengine.provider.Provider;

/**
 * @author kirankumar
 */
public interface LayoutBuilderFactory {

    DataAndViewParsingLayoutBuilder createDataAndViewParsingLayoutBuilder(Context context, Provider viewProvider);
    DataParsingLayoutBuilder createDataParsingLayoutBuilder(Context context);
    SimpleLayoutBuilder createSimpleLayoutBuilder(Context context);
}
