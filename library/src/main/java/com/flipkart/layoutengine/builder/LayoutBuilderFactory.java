package com.flipkart.layoutengine.builder;

import android.content.Context;

import com.flipkart.layoutengine.provider.Provider;

/**
 * Created by kirankumar on 06/01/15.
 */
public interface LayoutBuilderFactory {
    LayoutBuilder createDataAndViewParsingLayoutBuilder(Context context, Provider dataProvider, Provider viewProvider);

    LayoutBuilder createDataParsingLayoutBuilder(Context context, Provider dataProvider);

    LayoutBuilder createSimpleLayoutBuilder(Context context);
}
