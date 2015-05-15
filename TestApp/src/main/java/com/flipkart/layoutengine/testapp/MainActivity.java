package com.flipkart.layoutengine.testapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;

import com.flipkart.layoutengine.EventType;
import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.builder.DefaultLayoutBuilderFactory;
import com.flipkart.layoutengine.builder.LayoutBuilder;
import com.flipkart.layoutengine.builder.LayoutBuilderCallback;
import com.flipkart.layoutengine.provider.GsonProvider;
import com.flipkart.layoutengine.view.DataProteusView;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ProteusView proteusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            setupView(null);
        }
    }

    private void setupView(String newData) {
        createView(newData);
    }

    private void createView(String newData) {
        Gson gson = new Gson();

        JsonObject layoutData = gson.fromJson("{\"type\":\"LinearLayout\",\"android\":\"http://schemas.android.com/apk/res/android\",\"layout_width\":\"match_parent\",\"layout_height\":\"match_parent\",\"paddingLeft\":\"16dp\",\"paddingRight\":\"16dp\",\"paddingTop\":\"16dp\",\"paddingBottom\":\"16dp\",\"orientation\":\"vertical\",\"children\":[{\"type\":\"TextView\",\"dataContext\":\"$product\",\"layout_width\":\"200dp\",\"layout_height\":\"50dp\",\"text\":\"$name\"},{\"type\":\"TextView\",\"dataContext\":\"$product\",\"layout_width\":\"200dp\",\"layout_height\":\"50dp\",\"text\":\"$price\"},{\"type\":\"TextView\",\"dataContext\":\"$product\",\"layout_width\":\"200dp\",\"layout_height\":\"50dp\",\"text\":\"$rating\"}]}", JsonObject.class);
        //JsonObject layoutData = gson.fromJson("{\"type\":\"LinearLayout\",\"android\":\"http://schemas.android.com/apk/res/android\",\"layout_width\":\"match_parent\",\"layout_height\":\"match_parent\",\"paddingLeft\":\"16dp\",\"paddingRight\":\"16dp\",\"paddingTop\":\"16dp\",\"paddingBottom\":\"16dp\",\"orientation\":\"vertical\",\"children\":[{\"type\":\"ListView\",\"layout_width\":\"match_parent\",\"layout_height\":\"match_parent\",\"listViewLayout\":\"testting value\"}]}", JsonObject.class);
        //JsonObject layoutData = gson.fromJson("{\"type\":\"LinearLayout\",\"android\":\"http://schemas.android.com/apk/res/android\",\"layout_width\":\"match_parent\",\"layout_height\":\"match_parent\",\"paddingLeft\":\"16dp\",\"paddingRight\":\"16dp\",\"paddingTop\":\"16dp\",\"paddingBottom\":\"16dp\",\"orientation\":\"vertical\",\"children\":[{\"type\":\"ListView\",\"layout_width\":\"match_parent\",\"layout_height\":\"match_parent\",\"listViewData\":{\"dataContext\":\"$products\",\"layout\":{\"type\":\"LinearLayout\",\"android\":\"http://schemas.android.com/apk/res/android\",\"layout_width\":\"match_parent\",\"layout_height\":\"match_parent\",\"orientation\":\"vertical\",\"onClick\":\"itemClicked\",\"children\":[{\"type\":\"TextView\",\"layout_width\":\"200dp\",\"layout_height\":\"50dp\",\"text\":\"$products.name\"}]}}}]}", JsonObject.class);

        JsonObject data;
        if (newData != null) {
            data = gson.fromJson(newData, JsonObject.class);
        } else {
            data = gson.fromJson("{\"product\":{\"name\":\"Gaming Mouse\",\"price\":\"1350\",\"rating\":\"****\"}}", JsonObject.class);
            //data = gson.fromJson("{\"products\":[{\"name\":\"qwe0\"},{\"name\":\"qwe1\"},{\"name\":\"qwe2\"},{\"name\":\"qwe3\"},{\"name\":\"qwe4\"},{\"name\":\"qwe5\"},{\"name\":\"qwe6\"},{\"name\":\"qwe7\"},{\"name\":\"qwe8\"},{\"name\":\"qwe9\"},{\"name\":\"qwe10\"},{\"name\":\"qwe11\"},{\"name\":\"qwe12\"},{\"name\":\"qwe13\"},{\"name\":\"qwe14\"},{\"name\":\"qwe15\"},{\"name\":\"qwe16\"},{\"name\":\"qwe17\"},{\"name\":\"qwe18\"},{\"name\":\"qwe19\"}]}", JsonObject.class);
        }

        LayoutBuilder builder = new DefaultLayoutBuilderFactory()
                .createDataAndViewParsingLayoutBuilder(this, new GsonProvider(layoutData));

        builder.setListener(createCallback());

        FrameLayout container = new FrameLayout(MainActivity.this);

        this.proteusView = builder.build(container, layoutData, data);
        View view = proteusView.getView();

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        if (view != null) {
            container.addView(view, layoutParams);
        }
        MainActivity.this.setContentView(container);
    }

    private LayoutBuilderCallback createCallback() {
        return new LayoutBuilderCallback() {

            @Override
            public void onUnknownAttribute(ParserContext context, String attribute, JsonElement element, JsonObject object, View view, int childIndex) {

            }

            @Override
            public ProteusView onUnknownViewType(ParserContext context, String viewType, JsonObject object, ViewGroup parent, int childIndex) {
                return null;
            }

            @Override
            public void onViewBuiltFromViewProvider(ProteusView createdView, String viewType, ParserContext context, JsonObject viewJsonObject, ViewGroup parent, int childIndex) {
                Log.e(TAG, "here");
            }

            @Override
            public View onEvent(ParserContext context, View view, JsonElement attributeValue, EventType eventType) {
                Log.d("event", attributeValue.toString());
                return null;
            }

            @Override
            public PagerAdapter onPagerAdapterRequired(ParserContext parserContext, ProteusView<View> parent, List<ProteusView> children) {
                return null;
            }

            @Override
            public Adapter onAdapterRequired(ParserContext parserContext, ProteusView<View> parent, List<ProteusView> children) {
                return null;
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            //createView();
            //Gson gson = new Gson();
            //JsonObject data = gson.fromJson("{\"product\":{\"name\":\"Intel Core i7\",\"price\":\"19500\",\"rating\":\"*****\"}}", JsonObject.class);
            //this.proteusView.updateView(data);
            ((DataProteusView)this.proteusView).set("$product.name", "blah", 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "key down " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_R) {
            MainActivity.this.setContentView(new FrameLayout(this));
        }
        return super.onKeyDown(keyCode, event);
    }
}
