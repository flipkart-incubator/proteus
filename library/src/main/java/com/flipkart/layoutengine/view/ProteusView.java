package com.flipkart.layoutengine.view;

import com.flipkart.layoutengine.view.manager.ProteusViewManager;

/**
 *
 */
public interface ProteusView {

    ProteusViewManager getViewManager();

    void setViewManager(ProteusViewManager proteusViewManager);

}
