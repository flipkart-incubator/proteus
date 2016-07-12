package com.flipkart.proteus.view;

import com.flipkart.proteus.view.manager.ProteusViewManager;

/**
 *
 */
public interface ProteusView {

    ProteusViewManager getViewManager();

    void setViewManager(ProteusViewManager proteusViewManager);

}
