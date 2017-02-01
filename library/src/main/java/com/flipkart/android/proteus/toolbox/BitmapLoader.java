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

package com.flipkart.android.proteus.toolbox;

import com.flipkart.android.proteus.ProteusView;

/**
 * Used for loading bitmap from network. This is used by layoutengine whenever resources have to downloaded.
 */
public interface BitmapLoader {
    /**
     * Useful for asynchronous download of bitmap.
     *  @param url
     * @param callback
     */
    void getBitmap(ProteusView view, String url, ImageLoaderCallback callback);
}
