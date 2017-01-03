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

package com.flipkart.android.proteus.parser;

import org.junit.Assert;
import org.junit.Test;

/**
 * ParseHelperTest
 *
 * @author aditya.sharat
 */
public class ParseHelperTest {

    @Test
    public void testParseInt() {
        String input;

        // check 0
        input = "0";
        Assert.assertEquals(ParseHelper.parseInt(input), 0);

        // check negative
        input = "-1";
        Assert.assertEquals(ParseHelper.parseInt(input), -1);

        // check positive
        input = "1";
        Assert.assertEquals(ParseHelper.parseInt(input), 1);

        // check null
        Assert.assertEquals(ParseHelper.parseInt(null), 0);

        // check "null"
        input = "null";
        Assert.assertEquals(ParseHelper.parseInt(input), 0);

        // check random string
        input = "blah blah";
        Assert.assertEquals(ParseHelper.parseInt(input), 0);
    }
}