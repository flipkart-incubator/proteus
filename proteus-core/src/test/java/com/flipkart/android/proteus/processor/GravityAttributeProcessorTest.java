/*
 * Copyright 2019 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.android.proteus.processor;

import android.view.Gravity;
import android.view.View;

import com.flipkart.android.proteus.value.Primitive;
import com.flipkart.android.proteus.value.Value;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * GravityAttributeProcessorTest
 *
 * @author aditya.sharat
 */
public class GravityAttributeProcessorTest {

  @Test
  public void parse_simple() throws Exception {
    String CENTER = "center";
    String CENTER_HORIZONTAL = "center_horizontal";
    String CENTER_VERTICAL = "center_vertical";
    String LEFT = "left";
    String RIGHT = "right";
    String TOP = "top";
    String BOTTOM = "bottom";
    String START = "start";
    String END = "end";

    Map<String, Integer> sGravityMap = new HashMap<>();

    sGravityMap.put(CENTER, Gravity.CENTER);
    sGravityMap.put(CENTER_HORIZONTAL, Gravity.CENTER_HORIZONTAL);
    sGravityMap.put(CENTER_VERTICAL, Gravity.CENTER_VERTICAL);
    sGravityMap.put(LEFT, Gravity.LEFT);
    sGravityMap.put(RIGHT, Gravity.RIGHT);
    sGravityMap.put(TOP, Gravity.TOP);
    sGravityMap.put(BOTTOM, Gravity.BOTTOM);
    sGravityMap.put(START, Gravity.START);
    sGravityMap.put(END, Gravity.END);

    AttributeProcessor processor = new GravityAttributeProcessor() {
      @Override
      public void setGravity(View view, @Gravity int gravity) {

      }
    };

    for (Map.Entry<String, Integer> entry : sGravityMap.entrySet()) {
      System.out.print("\nGravityAttributeProcessorTest:parse_simple() when gravity : '" + entry.getKey() + "'\n");
      Value value = processor.compile(new Primitive(entry.getKey()), null);
      assertThat(value.getAsInt(), is(entry.getValue()));
    }
  }

  @Test
  public void parse_complex() throws Exception {
    AttributeProcessor processor = new GravityAttributeProcessor() {
      @Override
      public void setGravity(View view, @Gravity int gravity) {

      }
    };

    Value value = processor.compile(new Primitive("top|left"), null);
    int gravity = Gravity.TOP | Gravity.LEFT;
    assertThat(value.getAsInt(), is(gravity));
  }

  @Test
  public void handle_string() throws Exception {
    String CENTER = "center";
    String CENTER_HORIZONTAL = "center_horizontal";
    String CENTER_VERTICAL = "center_vertical";
    String LEFT = "left";
    String RIGHT = "right";
    String TOP = "top";
    String BOTTOM = "bottom";
    String START = "start";
    String END = "end";

    Map<String, Integer> sGravityMap = new HashMap<>();

    sGravityMap.put(CENTER, Gravity.CENTER);
    sGravityMap.put(CENTER_HORIZONTAL, Gravity.CENTER_HORIZONTAL);
    sGravityMap.put(CENTER_VERTICAL, Gravity.CENTER_VERTICAL);
    sGravityMap.put(LEFT, Gravity.LEFT);
    sGravityMap.put(RIGHT, Gravity.RIGHT);
    sGravityMap.put(TOP, Gravity.TOP);
    sGravityMap.put(BOTTOM, Gravity.BOTTOM);
    sGravityMap.put(START, Gravity.START);
    sGravityMap.put(END, Gravity.END);

    for (final Map.Entry<String, Integer> entry : sGravityMap.entrySet()) {
      System.out.print("\nGravityAttributeProcessorTest:handle_string() when gravity : '" + entry.getKey() + "'\n");
      AttributeProcessor processor = new GravityAttributeProcessor() {
        @Override
        public void setGravity(View view, @Gravity int gravity) {
          assertThat(gravity, is(entry.getValue()));
        }
      };
      processor.handleValue(null, new Primitive(entry.getKey()));
    }
  }

  @Test
  public void handle_gravity() throws Exception {
    String CENTER = "center";
    String CENTER_HORIZONTAL = "center_horizontal";
    String CENTER_VERTICAL = "center_vertical";
    String LEFT = "left";
    String RIGHT = "right";
    String TOP = "top";
    String BOTTOM = "bottom";
    String START = "start";
    String END = "end";

    Map<String, Integer> sGravityMap = new HashMap<>();

    sGravityMap.put(CENTER, Gravity.CENTER);
    sGravityMap.put(CENTER_HORIZONTAL, Gravity.CENTER_HORIZONTAL);
    sGravityMap.put(CENTER_VERTICAL, Gravity.CENTER_VERTICAL);
    sGravityMap.put(LEFT, Gravity.LEFT);
    sGravityMap.put(RIGHT, Gravity.RIGHT);
    sGravityMap.put(TOP, Gravity.TOP);
    sGravityMap.put(BOTTOM, Gravity.BOTTOM);
    sGravityMap.put(START, Gravity.START);
    sGravityMap.put(END, Gravity.END);

    for (final Map.Entry<String, Integer> entry : sGravityMap.entrySet()) {
      System.out.print("\nGravityAttributeProcessorTest:handle_gravity() when gravity : '" + entry.getKey() + "'\n");
      AttributeProcessor processor = new GravityAttributeProcessor() {
        @Override
        public void setGravity(View view, @Gravity int gravity) {
          assertThat(gravity, is(entry.getValue()));
        }
      };
      Value value = processor.compile(new Primitive(entry.getKey()), null);
      processor.handleValue(null, value);
    }
  }

}