package com.flipkart.layoutengine.testapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.flipkart.layoutengine.EventType;
import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.builder.DefaultLayoutBuilderFactory;
import com.flipkart.layoutengine.builder.LayoutBuilder;
import com.flipkart.layoutengine.builder.LayoutBuilderCallback;
import com.flipkart.layoutengine.provider.GsonProvider;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


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

        //JsonObject layoutData = gson.fromJson("{\"type\":\"LinearLayout\",\"android\":\"http://schemas.android.com/apk/res/android\",\"layout_width\":\"match_parent\",\"layout_height\":\"match_parent\",\"paddingLeft\":\"16dp\",\"paddingRight\":\"16dp\",\"paddingTop\":\"16dp\",\"paddingBottom\":\"16dp\",\"orientation\":\"vertical\",\"children\":[{\"type\":\"TextView\",\"layout_width\":\"200dp\",\"layout_height\":\"50dp\",\"text\":\"$product.name\"},{\"type\":\"TextView\",\"layout_width\":\"200dp\",\"layout_height\":\"50dp\",\"text\":\"$product.price\"},{\"type\":\"TextView\",\"layout_width\":\"200dp\",\"layout_height\":\"50dp\",\"text\":\"$product.rating\"}]}", JsonObject.class);
        //JsonObject layoutData = gson.fromJson("{\"type\":\"LinearLayout\",\"android\":\"http://schemas.android.com/apk/res/android\",\"layout_width\":\"match_parent\",\"layout_height\":\"match_parent\",\"paddingLeft\":\"16dp\",\"paddingRight\":\"16dp\",\"paddingTop\":\"16dp\",\"paddingBottom\":\"16dp\",\"orientation\":\"vertical\",\"children\":[{\"type\":\"ListView\",\"layout_width\":\"match_parent\",\"layout_height\":\"match_parent\",\"listViewLayout\":\"testting value\"}]}", JsonObject.class);
        JsonObject layoutData = gson.fromJson("{\"type\":\"LinearLayout\",\"android\":\"http://schemas.android.com/apk/res/android\",\"layout_width\":\"match_parent\",\"layout_height\":\"match_parent\",\"paddingLeft\":\"16dp\",\"paddingRight\":\"16dp\",\"paddingTop\":\"16dp\",\"paddingBottom\":\"16dp\",\"orientation\":\"vertical\",\"children\":[{\"type\":\"ListView\",\"layout_width\":\"match_parent\",\"layout_height\":\"match_parent\",\"listViewData\":{\"dataContext\":\"$products\",\"layout\":{\"type\":\"LinearLayout\",\"android\":\"http://schemas.android.com/apk/res/android\",\"layout_width\":\"match_parent\",\"layout_height\":\"match_parent\",\"orientation\":\"vertical\",\"children\":[{\"type\":\"TextView\",\"layout_width\":\"200dp\",\"layout_height\":\"50dp\",\"text\":\"$products.name\"}]}}}]}", JsonObject.class);

        JsonElement data;
        if (newData != null) {
            data = gson.fromJson(newData, JsonElement.class);
        } else {
            //data = gson.fromJson("{\"product\":{\"name\":\"Gaming Mouse\",\"price\":\"1350\",\"rating\":\"****\"}}", JsonElement.class);
            data = gson.fromJson("{\"products\":[{\"name\":\"qwe0\"},{\"name\":\"qwe1\"},{\"name\":\"qwe2\"},{\"name\":\"qwe3\"},{\"name\":\"qwe4\"},{\"name\":\"qwe5\"},{\"name\":\"qwe6\"},{\"name\":\"qwe7\"},{\"name\":\"qwe8\"},{\"name\":\"qwe9\"}]}", JsonElement.class);
        }

        LayoutBuilder builder = new DefaultLayoutBuilderFactory().createDataAndViewParsingLayoutBuilder(this, new GsonProvider(data), new GsonProvider(layoutData));

        builder.setListener(createCallback());

        FrameLayout container = new FrameLayout(MainActivity.this);

        this.proteusView = builder.build(container, layoutData);
        View view = proteusView.getView();

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

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
            public View onEvent(ParserContext context, View view, JsonElement attributeValue, EventType eventType) {
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
            Gson gson = new Gson();
            JsonObject data = gson.fromJson("{\"product\":{\"name\":\"Intel Core i7\",\"price\":\"19500\",\"rating\":\"*****\"}}", JsonObject.class);
            this.proteusView.updateView(data);
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
