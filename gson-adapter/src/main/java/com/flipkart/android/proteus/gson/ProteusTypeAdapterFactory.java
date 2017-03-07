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

package com.flipkart.android.proteus.gson;

import android.content.Context;
import android.support.annotation.Nullable;

import com.flipkart.android.proteus.FormatterManager;
import com.flipkart.android.proteus.Proteus;
import com.flipkart.android.proteus.ProteusConstants;
import com.flipkart.android.proteus.ViewTypeParser;
import com.flipkart.android.proteus.value.Array;
import com.flipkart.android.proteus.value.AttributeResource;
import com.flipkart.android.proteus.value.Binding;
import com.flipkart.android.proteus.value.Color;
import com.flipkart.android.proteus.value.Dimension;
import com.flipkart.android.proteus.value.DrawableValue;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.NestedBinding;
import com.flipkart.android.proteus.value.Null;
import com.flipkart.android.proteus.value.ObjectValue;
import com.flipkart.android.proteus.value.Primitive;
import com.flipkart.android.proteus.value.Resource;
import com.flipkart.android.proteus.value.StyleResource;
import com.flipkart.android.proteus.value.Value;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * ProteusTypeAdapterFactory
 *
 * @author aditya.sharat
 */
public class ProteusTypeAdapterFactory implements TypeAdapterFactory {

    public static final ProteusInstanceHolder PROTEUS_INSTANCE_HOLDER = new ProteusInstanceHolder();

    private Context context;

    /**
     *
     */
    public final TypeAdapter<Value> VALUE_TYPE_ADAPTER = new TypeAdapter<Value>() {
        @Override
        public void write(JsonWriter out, Value value) throws IOException {
            throw new UnsupportedOperationException("Use ProteusTypeAdapterFactory.COMPILED_VALUE_TYPE_ADAPTER instead");
        }

        @Override
        public Value read(JsonReader in) throws IOException {
            switch (in.peek()) {
                case STRING:
                    return compileString(in.nextString());
                case NUMBER:
                    String number = in.nextString();
                    return new Primitive(new LazilyParsedNumber(number));
                case BOOLEAN:
                    return new Primitive(in.nextBoolean());
                case NULL:
                    in.nextNull();
                    return Null.INSTANCE;
                case BEGIN_ARRAY:
                    Array array = new Array();
                    in.beginArray();
                    while (in.hasNext()) {
                        array.add(read(in));
                    }
                    in.endArray();
                    return array;
                case BEGIN_OBJECT:
                    ObjectValue object = new ObjectValue();
                    in.beginObject();
                    if (in.hasNext()) {
                        String name = in.nextName();
                        if (ProteusConstants.TYPE.equals(name) && JsonToken.STRING.equals(in.peek())) {
                            String type = in.nextString();
                            if (PROTEUS_INSTANCE_HOLDER.isLayout(type)) {
                                Layout layout = LAYOUT_TYPE_ADAPTER.read(type, PROTEUS_INSTANCE_HOLDER.getProteus(), in);
                                in.endObject();
                                return layout;
                            } else {
                                object.add(name, compileString(type));
                            }
                        } else {
                            object.add(name, read(in));
                        }
                    }
                    while (in.hasNext()) {
                        object.add(in.nextName(), read(in));
                    }
                    in.endObject();
                    return object;
                case END_DOCUMENT:
                case NAME:
                case END_OBJECT:
                case END_ARRAY:
                default:
                    throw new IllegalArgumentException();
            }
        }
    }.nullSafe();

    /**
     *
     */
    public final TypeAdapter<Primitive> PRIMITIVE_TYPE_ADAPTER = new TypeAdapter<Primitive>() {

        @Override
        public void write(JsonWriter out, Primitive value) throws IOException {
            VALUE_TYPE_ADAPTER.write(out, value);
        }

        @Override
        public Primitive read(JsonReader in) throws IOException {
            Value value = VALUE_TYPE_ADAPTER.read(in);
            return value != null && value.isPrimitive() ? value.getAsPrimitive() : null;
        }
    }.nullSafe();

    /**
     *
     */
    public final TypeAdapter<ObjectValue> OBJECT_TYPE_ADAPTER = new TypeAdapter<ObjectValue>() {
        @Override
        public void write(JsonWriter out, ObjectValue value) throws IOException {
            VALUE_TYPE_ADAPTER.write(out, value);
        }

        @Override
        public ObjectValue read(JsonReader in) throws IOException {
            Value value = VALUE_TYPE_ADAPTER.read(in);
            return value != null && value.isObject() ? value.getAsObject() : null;
        }
    }.nullSafe();

    /**
     *
     */
    public final TypeAdapter<Array> ARRAY_TYPE_ADAPTER = new TypeAdapter<Array>() {
        @Override
        public void write(JsonWriter out, Array value) throws IOException {
            VALUE_TYPE_ADAPTER.write(out, value);
        }

        @Override
        public Array read(JsonReader in) throws IOException {
            Value value = VALUE_TYPE_ADAPTER.read(in);
            return value != null && value.isArray() ? value.getAsArray() : null;
        }
    }.nullSafe();

    /**
     *
     */
    public final TypeAdapter<Null> NULL_TYPE_ADAPTER = new TypeAdapter<Null>() {

        @Override
        public void write(JsonWriter out, Null value) throws IOException {
            VALUE_TYPE_ADAPTER.write(out, value);
        }

        @Override
        public Null read(JsonReader in) throws IOException {
            Value value = VALUE_TYPE_ADAPTER.read(in);
            return value != null && value.isNull() ? value.getAsNull() : null;
        }
    }.nullSafe();

    /**
     *
     */
    public final LayoutTypeAdapter LAYOUT_TYPE_ADAPTER = new LayoutTypeAdapter();

    /**
     *
     */
    public final TypeAdapter<Value> COMPILED_VALUE_TYPE_ADAPTER = new TypeAdapter<Value>() {

        public static final String TYPE = "$t";
        public static final String VALUE = "$v";

        @Override
        public void write(JsonWriter out, Value value) throws IOException {
            if (value == null || value.isNull()) {
                out.nullValue();
            } else if (value.isPrimitive()) {
                Primitive primitive = value.getAsPrimitive();
                if (primitive.isNumber()) {
                    out.value(primitive.getAsNumber());
                } else if (primitive.isBoolean()) {
                    out.value(primitive.getAsBoolean());
                } else {
                    out.value(primitive.getAsString());
                }
            } else if (value.isObject()) {
                out.beginObject();
                for (Map.Entry<String, Value> e : value.getAsObject().entrySet()) {
                    out.name(e.getKey());
                    write(out, e.getValue());
                }
                out.endObject();
            } else if (value.isArray()) {
                out.beginArray();
                Iterator<Value> iterator = value.getAsArray().iterator();
                while (iterator.hasNext()) {
                    write(out, iterator.next());
                }
                out.endArray();
            } else {
                CustomValueTypeAdapter adapter = getCustomValueTypeAdapter(value.getClass());

                out.beginObject();

                out.name(TYPE);
                out.value(adapter.type);

                out.name(VALUE);
                //noinspection unchecked
                adapter.write(out, value);

                out.endObject();
            }
        }

        @Override
        public Value read(JsonReader in) throws IOException {
            switch (in.peek()) {
                case STRING:
                    return compileString(in.nextString());
                case NUMBER:
                    String number = in.nextString();
                    return new Primitive(new LazilyParsedNumber(number));
                case BOOLEAN:
                    return new Primitive(in.nextBoolean());
                case NULL:
                    in.nextNull();
                    return Null.INSTANCE;
                case BEGIN_ARRAY:
                    Array array = new Array();
                    in.beginArray();
                    while (in.hasNext()) {
                        array.add(read(in));
                    }
                    in.endArray();
                    return array;
                case BEGIN_OBJECT:
                    ObjectValue object = new ObjectValue();
                    in.beginObject();
                    if (in.hasNext()) {
                        String name = in.nextName();
                        if (TYPE.equals(name) && JsonToken.NUMBER.equals(in.peek())) {
                            int type = Integer.parseInt(in.nextString());
                            CustomValueTypeAdapter<? extends Value> adapter = getCustomValueTypeAdapter(type);
                            in.nextName();
                            Value value = adapter.read(in);
                            in.endObject();
                            return value;
                        } else {
                            object.add(name, read(in));
                        }
                    }
                    while (in.hasNext()) {
                        object.add(in.nextName(), read(in));
                    }
                    in.endObject();
                    return object;
                case END_DOCUMENT:
                case NAME:
                case END_OBJECT:
                case END_ARRAY:
                default:
                    throw new IllegalArgumentException();
            }
        }

    };

    /**
     *
     */
    public final CustomValueTypeAdapterCreator<AttributeResource> ATTRIBUTE_RESOURCE = new CustomValueTypeAdapterCreator<AttributeResource>() {
        @Override
        public CustomValueTypeAdapter<AttributeResource> create(int type) {
            return new CustomValueTypeAdapter<AttributeResource>(type) {
                @Override
                public void write(JsonWriter out, AttributeResource value) throws IOException {
                    out.value(value.attributeId);
                }

                @Override
                public AttributeResource read(JsonReader in) throws IOException {
                    return AttributeResource.valueOf(Integer.parseInt(in.nextString()));
                }
            };
        }
    };

    /**
     *
     */
    public final CustomValueTypeAdapterCreator<Binding> BINDING = new CustomValueTypeAdapterCreator<Binding>() {
        @Override
        public CustomValueTypeAdapter<Binding> create(int type) {
            return new CustomValueTypeAdapter<Binding>(type) {

                @Override
                public void write(JsonWriter out, Binding value) throws IOException {
                    out.value(value.toString());
                }

                @Override
                public Binding read(JsonReader in) throws IOException {
                    return Binding.valueOf(in.nextString(), PROTEUS_INSTANCE_HOLDER.getProteus().formatterManager);
                }
            };
        }
    };

    /**
     *
     */
    public final CustomValueTypeAdapterCreator<Color.Int> COLOR_INT = new CustomValueTypeAdapterCreator<Color.Int>() {
        @Override
        public CustomValueTypeAdapter<Color.Int> create(int type) {
            return new CustomValueTypeAdapter<Color.Int>(type) {
                @Override
                public void write(JsonWriter out, Color.Int color) throws IOException {
                    out.value(color.value);
                }

                @Override
                public Color.Int read(JsonReader in) throws IOException {
                    return Color.Int.valueOf(Integer.parseInt(in.nextString()));
                }
            };
        }
    };

    /**
     *
     */
    public final CustomValueTypeAdapterCreator<Color.StateList> COLOR_STATE_LIST = new CustomValueTypeAdapterCreator<Color.StateList>() {
        @Override
        public CustomValueTypeAdapter<Color.StateList> create(int type) {
            return new CustomValueTypeAdapter<Color.StateList>(type) {

                private final String KEY_STATES = "s";
                private final String KEY_COLORS = "c";
                private final String STATE_DELIMITER = "|";
                private final String COLOR_DELIMITER = ",";

                @Override
                public void write(JsonWriter out, Color.StateList value) throws IOException {
                    out.beginObject();

                    out.name(KEY_STATES);
                    out.value(writeStates(value.states));

                    out.name(KEY_COLORS);
                    out.value(writeColors(value.colors));

                    out.endObject();
                }

                @Override
                public Color.StateList read(JsonReader in) throws IOException {
                    in.beginObject();

                    in.nextName();
                    int[][] states = readStates(in.nextString());

                    in.nextName();
                    int colors[] = readColors(in.nextString());

                    Color.StateList color = Color.StateList.valueOf(states, colors);

                    in.endObject();
                    return color;
                }

                private String writeStates(int[][] states) {
                    StringBuilder builder = new StringBuilder();
                    for (int state = 0; state < states.length; state++) {
                        builder.append(writeColors(states[state]));
                        if (state < states.length - 1) {
                            builder.append(STATE_DELIMITER);
                        }
                    }
                    return builder.toString();
                }

                private String writeColors(int[] colors) {
                    StringBuilder builder = new StringBuilder();
                    for (int color = 0; color < colors.length; color++) {
                        builder.append(colors[color]);
                        if (color < colors.length - 1) {
                            builder.append(COLOR_DELIMITER);
                        }
                    }
                    return builder.toString();
                }

                private int[][] readStates(String string) {
                    int[][] states = new int[0][];
                    StringTokenizer tokenizer = new StringTokenizer(string, STATE_DELIMITER);
                    int index = 0;
                    while (tokenizer.hasMoreTokens()) {
                        states = Arrays.copyOf(states, states.length + 1);
                        states[index] = readColors(tokenizer.nextToken());
                        index++;
                    }
                    return states;
                }

                private int[] readColors(String string) {
                    int[] colors = new int[0];
                    StringTokenizer tokenizer = new StringTokenizer(string, COLOR_DELIMITER);
                    int index = 0;
                    while (tokenizer.hasMoreTokens()) {
                        colors = Arrays.copyOf(colors, colors.length + 1);
                        colors[index] = Integer.parseInt(tokenizer.nextToken());
                        index++;
                    }
                    return colors;
                }
            };
        }
    };

    /**
     *
     */
    public final CustomValueTypeAdapterCreator<Dimension> DIMENSION = new CustomValueTypeAdapterCreator<Dimension>() {
        @Override
        public CustomValueTypeAdapter<Dimension> create(int type) {
            return new CustomValueTypeAdapter<Dimension>(type) {
                @Override
                public void write(JsonWriter out, Dimension value) throws IOException {
                    out.value(value.toString());
                }

                @Override
                public Dimension read(JsonReader in) throws IOException {
                    return Dimension.valueOf(in.nextString());
                }
            };
        }
    };

    /**
     *
     */
    public final CustomValueTypeAdapterCreator<DrawableValue> DRAWABLE_VALUE = new CustomValueTypeAdapterCreator<DrawableValue>() {
        @Override
        public CustomValueTypeAdapter<DrawableValue> create(int type) {
            return new CustomValueTypeAdapter<DrawableValue>(type) {
                @Override
                public void write(JsonWriter out, DrawableValue value) throws IOException {
                    out.value("#ffff00");
                }

                @Override
                public DrawableValue read(JsonReader in) throws IOException {
                    return DrawableValue.valueOf(in.nextString(), getContext());
                }
            };
        }
    };

    /**
     *
     */
    public final CustomValueTypeAdapterCreator<Layout> LAYOUT = new CustomValueTypeAdapterCreator<Layout>() {
        @Override
        public CustomValueTypeAdapter<Layout> create(int type) {
            return new CustomValueTypeAdapter<Layout>(type) {

                private static final String KEY_TYPE = "t";
                private static final String KEY_DATA = "d";
                private static final String KEY_ATTRIBUTES = "a";
                private static final String KEY_EXTRAS = "e";
                private static final String KEY_ATTRIBUTE_ID = "i";
                private static final String KEY_ATTRIBUTE_VALUE = "v";

                @Override
                public void write(JsonWriter out, Layout value) throws IOException {
                    out.beginObject();

                    out.name(KEY_TYPE);
                    out.value(value.type);

                    if (null != value.data) {
                        out.name(KEY_DATA);
                        out.beginObject();
                        for (Map.Entry<String, Value> entry : value.data.entrySet()) {
                            out.name(entry.getKey());
                            COMPILED_VALUE_TYPE_ADAPTER.write(out, entry.getValue());
                        }
                        out.endObject();
                    }


                    if (null != value.attributes) {
                        out.name(KEY_ATTRIBUTES);
                        out.beginArray();
                        for (Layout.Attribute attribute : value.attributes) {
                            out.beginObject();

                            out.name(KEY_ATTRIBUTE_ID);
                            out.value(attribute.id);

                            out.name(KEY_ATTRIBUTE_VALUE);
                            COMPILED_VALUE_TYPE_ADAPTER.write(out, attribute.value);

                            out.endObject();
                        }
                        out.endArray();
                    }

                    if (null != value.extras) {
                        out.name(KEY_EXTRAS);
                        COMPILED_VALUE_TYPE_ADAPTER.write(out, value.extras);
                    }

                    out.endObject();
                }

                @Override
                public Layout read(JsonReader in) throws IOException {
                    in.beginObject();

                    String name;
                    String type = null;
                    Map<String, Value> data = null;
                    List<Layout.Attribute> attributes = null;
                    ObjectValue extras = null;

                    while (in.hasNext()) {
                        name = in.nextName();
                        switch (name) {
                            case KEY_TYPE:
                                type = in.nextString();
                                break;
                            case KEY_DATA:
                                data = readData(in);
                                break;
                            case KEY_ATTRIBUTES:
                                attributes = readAttributes(in);
                                break;
                            case KEY_EXTRAS:
                                extras = readExtras(in);
                                break;
                            default:
                                throw new IllegalStateException("Bad attribute '" + name + "'");
                        }
                    }

                    in.endObject();

                    if (null == type) {
                        throw new IllegalStateException("Layout must have type attribute!");
                    }

                    //noinspection ConstantConditions
                    return new Layout(type, attributes, data, extras);
                }

                @Nullable
                private Map<String, Value> readData(JsonReader in) throws IOException {
                    Map<String, Value> data = LAYOUT_TYPE_ADAPTER.readData(in);
                    return data;
                }

                @Nullable
                private ObjectValue readExtras(JsonReader in) throws IOException {
                    return COMPILED_VALUE_TYPE_ADAPTER.read(in).getAsObject();
                }

                @Nullable
                private List<Layout.Attribute> readAttributes(JsonReader in) throws IOException {
                    List<Layout.Attribute> attributes = new ArrayList<>();

                    in.beginArray();
                    int id;
                    Value value;
                    while (in.hasNext()) {
                        in.nextName();
                        id = Integer.parseInt(in.nextString());
                        in.nextName();
                        value = COMPILED_VALUE_TYPE_ADAPTER.read(in);
                        attributes.add(new Layout.Attribute(id, value));
                    }
                    in.endArray();

                    return attributes;
                }
            };
        }
    };

    /**
     *
     */
    public final CustomValueTypeAdapterCreator<NestedBinding> NESTED_BINDING = new CustomValueTypeAdapterCreator<NestedBinding>() {
        @Override
        public CustomValueTypeAdapter<NestedBinding> create(int type) {
            return new CustomValueTypeAdapter<NestedBinding>(type) {
                @Override
                public void write(JsonWriter out, NestedBinding value) throws IOException {
                    COMPILED_VALUE_TYPE_ADAPTER.write(out, value.getValue());
                }

                @Override
                public NestedBinding read(JsonReader in) throws IOException {
                    return NestedBinding.valueOf(COMPILED_VALUE_TYPE_ADAPTER.read(in));
                }
            };
        }
    };

    /**
     *
     */
    public final CustomValueTypeAdapterCreator<Resource> RESOURCE = new CustomValueTypeAdapterCreator<Resource>() {
        @Override
        public CustomValueTypeAdapter<Resource> create(int type) {
            return new CustomValueTypeAdapter<Resource>(type) {
                @Override
                public void write(JsonWriter out, Resource value) throws IOException {
                    out.value(value.resId);
                }

                @Override
                public Resource read(JsonReader in) throws IOException {
                    return Resource.valueOf(Integer.parseInt(in.nextString()));
                }
            };
        }
    };

    /**
     *
     */
    public final CustomValueTypeAdapterCreator<StyleResource> STYLE_RESOURCE = new CustomValueTypeAdapterCreator<StyleResource>() {
        @Override
        public CustomValueTypeAdapter<StyleResource> create(int type) {
            return new CustomValueTypeAdapter<StyleResource>(type) {

                private static final String KEY_ATTRIBUTE_ID = "a";
                private static final String KEY_STYLE_ID = "s";

                @Override
                public void write(JsonWriter out, StyleResource value) throws IOException {
                    out.beginObject();

                    out.name(KEY_ATTRIBUTE_ID);
                    out.value(value.attributeId);

                    out.name(KEY_STYLE_ID);
                    out.value(value.styleId);

                    out.endObject();
                }

                @Override
                public StyleResource read(JsonReader in) throws IOException {
                    in.beginObject();
                    in.nextName();
                    String attributeId = in.nextString();
                    in.nextName();
                    String styleId = in.nextString();
                    in.endObject();
                    return StyleResource.valueOf(Integer.parseInt(styleId), Integer.parseInt(attributeId));
                }
            };
        }
    };

    /**
     *
     */
    private CustomValueTypeAdapterMap map = new CustomValueTypeAdapterMap();

    /**
     * @param context
     */
    public ProteusTypeAdapterFactory(Context context) {
        this.context = context;
        register(AttributeResource.class, ATTRIBUTE_RESOURCE);
        register(Binding.class, BINDING);
        register(Color.Int.class, COLOR_INT);
        register(Color.StateList.class, COLOR_STATE_LIST);
        register(Dimension.class, DIMENSION);
        register(DrawableValue.class, DRAWABLE_VALUE);
        register(Layout.class, LAYOUT);
        register(NestedBinding.class, NESTED_BINDING);
        register(Resource.class, RESOURCE);
        register(StyleResource.class, STYLE_RESOURCE);
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class clazz = type.getRawType();

        if (clazz == Primitive.class) {
            //noinspection unchecked
            return (TypeAdapter<T>) PRIMITIVE_TYPE_ADAPTER;
        } else if (clazz == ObjectValue.class) {
            //noinspection unchecked
            return (TypeAdapter<T>) OBJECT_TYPE_ADAPTER;
        } else if (clazz == Array.class) {
            //noinspection unchecked
            return (TypeAdapter<T>) ARRAY_TYPE_ADAPTER;
        } else if (clazz == Null.class) {
            //noinspection unchecked
            return (TypeAdapter<T>) NULL_TYPE_ADAPTER;
        } else if (clazz == Layout.class) {
            //noinspection unchecked
            return (TypeAdapter<T>) LAYOUT_TYPE_ADAPTER;
        } else if (clazz == Value.class) {
            //noinspection unchecked
            return (TypeAdapter<T>) VALUE_TYPE_ADAPTER;
        }

        return null;
    }

    public void register(Class<? extends Value> clazz, CustomValueTypeAdapterCreator<? extends Value> creator) {
        map.register(clazz, creator);
    }

    public CustomValueTypeAdapter<? extends Value> getCustomValueTypeAdapter(Class<? extends Value> clazz) {
        return map.get(clazz);
    }

    public CustomValueTypeAdapter<? extends Value> getCustomValueTypeAdapter(int type) {
        return map.get(type);
    }

    private Context getContext() {
        return context;
    }

    private static Value compileString(String string) {
        if (Binding.isBindingValue(string)) {
            return Binding.valueOf(string.substring(1), PROTEUS_INSTANCE_HOLDER.getProteus().formatterManager);
        } else {
            return new Primitive(string);
        }
    }

    public static class ProteusInstanceHolder {

        private Proteus proteus;

        private ProteusInstanceHolder() {
        }

        public Proteus getProteus() {
            return proteus;
        }

        public void setProteus(Proteus proteus) {
            this.proteus = proteus;
        }

        public boolean isLayout(String type) {
            return null != proteus && proteus.has(type);
        }
    }

    public static abstract class CustomValueTypeAdapter<V extends Value> extends TypeAdapter<V> {

        public final int type;

        protected CustomValueTypeAdapter(int type) {
            this.type = type;
        }

    }

    public static abstract class CustomValueTypeAdapterCreator<V extends Value> {

        public abstract CustomValueTypeAdapter<V> create(int type);

    }

    private class LayoutTypeAdapter extends TypeAdapter<Layout> {

        @Override
        public void write(JsonWriter out, Layout value) throws IOException {
            VALUE_TYPE_ADAPTER.write(out, value);
        }

        @Override
        public Layout read(JsonReader in) throws IOException {
            Value value = VALUE_TYPE_ADAPTER.read(in);
            return value != null && value.isLayout() ? value.getAsLayout() : null;
        }

        public Layout read(String type, Proteus proteus, JsonReader in) throws IOException {
            List<Layout.Attribute> attributes = new ArrayList<>();
            Map<String, Value> data = null;
            ObjectValue extras = new ObjectValue();
            String name;
            while (in.hasNext()) {
                name = in.nextName();
                if (ProteusConstants.DATA.equals(name)) {
                    data = readData(in);
                } else {
                    ViewTypeParser.AttributeSet.Attribute attribute = proteus.getAttributeId(name, type);
                    if (null != attribute) {
                        FormatterManager manager = PROTEUS_INSTANCE_HOLDER.getProteus().formatterManager;
                        Value value = attribute.processor.precompile(VALUE_TYPE_ADAPTER.read(in), getContext(), manager);
                        attributes.add(new Layout.Attribute(attribute.id, value));
                    } else {
                        extras.add(name, VALUE_TYPE_ADAPTER.read(in));
                    }
                }
            }
            return new Layout(type, attributes.size() > 0 ? attributes : null, data, extras.entrySet().size() > 0 ? extras : null);
        }

        public Map<String, Value> readData(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return new HashMap<>();
            }

            if (peek != JsonToken.BEGIN_OBJECT) {
                throw new JsonSyntaxException("data must be a Map<String, String>.");
            }

            Map<String, Value> data = new HashMap<>();

            in.beginObject();
            while (in.hasNext()) {
                JsonReaderInternalAccess.INSTANCE.promoteNameToValue(in);
                String key = in.nextString();
                Value value = VALUE_TYPE_ADAPTER.read(in);
                Value replaced = data.put(key, value);
                if (replaced != null) {
                    throw new JsonSyntaxException("duplicate key: " + key);
                }
            }
            in.endObject();

            return data;
        }
    }

    private class CustomValueTypeAdapterMap {

        private final Map<Class<? extends Value>, CustomValueTypeAdapter<? extends Value>> types = new HashMap<>();

        private CustomValueTypeAdapter<? extends Value>[] adapters = new CustomValueTypeAdapter[0];

        public CustomValueTypeAdapter<? extends Value> register(Class<? extends Value> clazz, CustomValueTypeAdapterCreator creator) {
            CustomValueTypeAdapter<? extends Value> adapter = types.get(clazz);
            if (null != adapter) {
                return adapter;
            }
            //noinspection unchecked
            adapter = creator.create(adapters.length);
            adapters = Arrays.copyOf(adapters, adapters.length + 1);
            adapters[adapters.length - 1] = adapter;
            return types.put(clazz, adapter);
        }

        public CustomValueTypeAdapter get(Class<? extends Value> clazz) {
            CustomValueTypeAdapter i = types.get(clazz);
            if (null == i) {
                throw new IllegalArgumentException(clazz.getName() + " is not a known value type! Remember to register the class first");
            }
            return types.get(clazz);
        }

        public CustomValueTypeAdapter<? extends Value> get(int i) {
            if (i < adapters.length) {
                return adapters[i];
            }
            throw new IllegalArgumentException(i + " is not a known value type! Did you conjure up this int?");
        }
    }
}
