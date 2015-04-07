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
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Handler handler = new Handler();
        if (savedInstanceState == null) {
            setupView();
        }
    }

    private void setupView() {
        String layout = "";
        String data = "";
        createView();
    }

    private void createView() {
        Gson gson = new Gson();

        JsonObject layoutData = gson.fromJson("{\"type\":\"LinearLayout\",\"android\":\"http://schemas.android.com/apk/res/android\",\"layout_width\":\"match_parent\",\"layout_height\":\"match_parent\",\"paddingLeft\":\"16dp\",\"paddingRight\":\"16dp\",\"paddingTop\":\"16dp\",\"paddingBottom\":\"16dp\",\"orientation\":\"vertical\",\"children\":[{\"type\":\"TextView\",\"layout_width\":\"200dp\",\"layout_height\":\"50dp\",\"text\":\"$product.name\"},{\"type\":\"TextView\",\"layout_width\":\"200dp\",\"layout_height\":\"50dp\",\"text\":\"$product.price\"},{\"type\":\"TextView\",\"layout_width\":\"200dp\",\"layout_height\":\"50dp\",\"text\":\"$product.rating\"}]}", JsonObject.class);
        JsonElement data = gson.fromJson("{\"product\":{\"name\":\"Gaming Mouse\",\"price\":\"1350\",\"rating\":\"****\"}}", JsonElement.class);

        LayoutBuilder builder = new DefaultLayoutBuilderFactory().createDataAndViewParsingLayoutBuilder(this, new GsonProvider(data), new GsonProvider(layoutData));

        builder.setListener(createCallback());

        FrameLayout container = new FrameLayout(MainActivity.this);

        View view = builder.build((ViewGroup) MainActivity.this.getWindow().getDecorView(), layoutData);

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
            public View onUnknownViewType(ParserContext context, String viewType, JsonObject object, ViewGroup parent, int childIndex) {
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
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
