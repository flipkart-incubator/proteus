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

/**
 * @author kirankumar
 * @author aditya.sharat
 */
public class Binding {

    private String bindingName;
    private int attributeId;
    private String attributeValue;
    private boolean hasRegEx;

    public Binding(String bindingName, int attributeId, String attributeValue, boolean hasRegEx) {
        this.bindingName = bindingName;
        this.attributeId = attributeId;
        this.attributeValue = attributeValue;
        this.hasRegEx = hasRegEx;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getBindingName() {
        return bindingName;
    }

    public boolean hasRegEx() {
        return hasRegEx;
    }

    public void hasRegEx(boolean hasRegEx) {
        this.hasRegEx = hasRegEx;
    }
}
