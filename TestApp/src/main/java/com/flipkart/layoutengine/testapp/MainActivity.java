package com.flipkart.layoutengine.testapp;

import android.os.Bundle;
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

        JsonObject layoutData = gson.fromJson("{\"type\":\"LinearLayout\",\"android\":\"http://schemas.android.com/apk/res/android\",\"layout_width\":\"match_parent\",\"layout_height\":\"match_parent\",\"paddingLeft\":\"16dp\",\"paddingRight\":\"16dp\",\"paddingTop\":\"16dp\",\"paddingBottom\":\"16dp\",\"orientation\":\"vertical\",\"children\":[{\"type\":\"TextView\",\"layout_width\":\"200dp\",\"layout_height\":\"50dp\",\"text\":\"$product.name\"},{\"type\":\"TextView\",\"layout_width\":\"200dp\",\"layout_height\":\"50dp\",\"text\":\"$product.price\"},{\"type\":\"TextView\",\"layout_width\":\"200dp\",\"layout_height\":\"50dp\",\"dataContext\":\"$product\",\"text\":\"$rating\"},{\"type\":\"TextView\",\"layout_width\":\"200dp\",\"layout_height\":\"50dp\",\"text\":\"~{{product.name}} is awesome {{product.price}}$(number)\"},{\"type\":\"RatingBar\",\"layout_width\":\"wrap_content\",\"layout_height\":\"wrap_content\",\"layout_marginRight\":\"2dp\",\"layout_marginTop\":\"2dp\",\"minHeight\":\"11dp\",\"rating\":\"$product.avgrating\"}]}", JsonObject.class);

        JsonObject data;
        if (newData != null) {
            data = gson.fromJson(newData, JsonObject.class);
        } else {
            data = gson.fromJson("{\"product\":{\"name\":\"Gaming Mouse\",\"price\":\"1350\",\"rating\":\"****\",\"avgrating\":\"4.5\"}}", JsonObject.class);
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
            public PagerAdapter onPagerAdapterRequired(ParserContext parserContext, ProteusView<View> parent, List<ProteusView> children, JsonObject viewLayout) {
                return null;
            }

            @Override
            public Adapter onAdapterRequired(ParserContext parserContext, ProteusView<View> parent, List<ProteusView> children, JsonObject viewLayout) {
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
            ((DataProteusView) this.proteusView).set("product.price", "17000", 0);
            ((DataProteusView) this.proteusView).set("product.name", "Intel Core i7", 0);
            ((DataProteusView) this.proteusView).set("product.rating", "***", 0);
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
