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
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.URLEncoder;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

class UrlEncodedGsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
  private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
  private final Gson gson;
  private final Type type;


  UrlEncodedGsonRequestBodyConverter(Gson gson, Type type) {
    this.gson = gson;
    this.type = type;
  }

  @Override
  public RequestBody convert(T value) throws IOException {
    //noinspection unchecked
    TypeAdapter<T> adapter = (TypeAdapter<T>) gson.getAdapter(TypeToken.get(type));
    Writer writer = new StringWriter();
    JsonWriter jsonWriter = gson.newJsonWriter(writer);
    adapter.write(jsonWriter, value);
    jsonWriter.close();
    return RequestBody.create(MEDIA_TYPE, URLEncoder.encode(writer.toString(), "UTF-8").getBytes());
  }
}
