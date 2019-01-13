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
package com.flipkart.android.proteus.demo.converter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
  private final Gson gson;
  private final Type type;
  private TypeAdapter<T> typeAdapter;

  GsonResponseBodyConverter(Gson gson, Type type) {
    this.gson = gson;
    this.type = type;
  }

  private TypeAdapter<T> getAdapter() {
    if (null == typeAdapter) {
      //noinspection unchecked
      typeAdapter = (TypeAdapter<T>) gson.getAdapter(TypeToken.get(type));
    }
    return typeAdapter;
  }

  @Override
  public T convert(ResponseBody value) throws IOException {
    TypeAdapter<T> adapter = getAdapter();
    JsonReader jsonReader = gson.newJsonReader(value.charStream());
    jsonReader.setLenient(true);
    try {
      return adapter.read(jsonReader);
    } finally {
      value.close();
    }
  }
}