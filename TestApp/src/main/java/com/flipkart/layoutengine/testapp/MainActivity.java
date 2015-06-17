package com.flipkart.layoutengine.testapp;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.flipkart.layoutengine.EventType;
import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.builder.DataAndViewParsingLayoutBuilder;
import com.flipkart.layoutengine.builder.LayoutBuilderCallback;
import com.flipkart.layoutengine.builder.LayoutBuilderFactory;
import com.flipkart.layoutengine.provider.GsonProvider;
import com.flipkart.layoutengine.view.DataProteusView;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private DataProteusView proteusView;
    private Gson gson;
    private DataAndViewParsingLayoutBuilder builder;
    private FrameLayout container;
    private JsonObject layout;
    private JsonObject data;
    private ViewGroup.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            createView();
        }
    }

    private void createView() {
        this.gson = new Gson();

        JsonObject layoutData = getJsonFromFile(R.raw.layout).getAsJsonObject();
        layout = layoutData;
        JsonObject productData = getJsonFromFile(R.raw.data_0).getAsJsonObject();
        data = productData;

        JsonElement sellerWidget = getJsonFromFile(R.raw.layout_seller_widget_0);
        JsonObject layoutProvider = new JsonObject();
        layoutProvider.add("SellerWidget", sellerWidget);

        this.builder = new LayoutBuilderFactory().getDataAndViewParsingLayoutBuilder(this, new GsonProvider(layoutProvider));

        builder.setListener(createCallback());

        this.container = new FrameLayout(MainActivity.this);
        layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        long startTime = System.currentTimeMillis();

        this.proteusView = (DataProteusView) builder.build(container, layoutData, productData, 0);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        Toast.makeText(this, "render time: " + elapsedTime, Toast.LENGTH_LONG).show();

        View view = proteusView.getView();
        if (view != null) {
            container.addView(view, layoutParams);
        }
        MainActivity.this.setContentView(container);
    }

    private LayoutBuilderCallback createCallback() {
        return new LayoutBuilderCallback() {

            @Override
            public void onUnknownAttribute(ParserContext context, String attribute, JsonElement element,
                                           JsonObject object, View view, int childIndex) {
            }

            @Override
            public ProteusView onUnknownViewType(ParserContext context, String viewType, JsonObject object,
                                                 ProteusView parent, int childIndex) {
                return null;
            }

            @Override
            public JsonObject onChildTypeLayoutRequired(ParserContext context, String viewType,
                                                        JsonObject parentViewJsonObject, ProteusView parent) {
                return null;
            }

            @Override
            public void onViewBuiltFromViewProvider(ProteusView createdView, String viewType,
                                                    ParserContext context, JsonObject viewJsonObject,
                                                    ProteusView parent, int childIndex) {
            }

            @Override
            public View onEvent(ParserContext context, View view, JsonElement attributeValue, EventType eventType) {
                Log.d("event", attributeValue.toString());
                return view;
            }

            @Override
            public PagerAdapter onPagerAdapterRequired(ParserContext parserContext, ProteusView<View> parent,
                                                       List<ProteusView> children, JsonObject viewLayout) {
                return null;
            }

            @Override
            public Adapter onAdapterRequired(ParserContext parserContext, ProteusView<View> parent,
                                             List<ProteusView> children, JsonObject viewLayout) {
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
        long startTime, stopTime, elapsedTime;
        switch (id) {
            case R.id.action_refresh_data:
                startTime = System.currentTimeMillis();

                this.proteusView.set("product.title", "Intel Core i7 5400K", 0);
                this.proteusView.set("product.rating.averageRating", 2.854, 0);
                this.proteusView.set("product.rating.ratingCount", 126, 0);
                this.proteusView.set("sellers_1[0].price", 18800, 0);
                this.proteusView.set("sellers_1[1].price", 17100, 0);

                JsonElement je = this.proteusView.get("sellers_1[0].price", -1);

                stopTime = System.currentTimeMillis();
                elapsedTime = stopTime - startTime;

                Log.e("data", je != null ? je.getAsString() : "fuck");

                Toast.makeText(this, "render time: " + elapsedTime, Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_refresh_layout:
                JsonElement sellerWidget = getJsonFromFile(R.raw.layout_seller_widget_1);
                JsonObject layoutProvider = new JsonObject();
                layoutProvider.add("SellerWidget", sellerWidget);
                builder.updateLayoutProvider(layoutProvider);

                startTime = System.currentTimeMillis();

                this.proteusView = (DataProteusView) builder.build(container, layout, data, 0);

                stopTime = System.currentTimeMillis();
                elapsedTime = stopTime - startTime;

                Toast.makeText(this, "render time: " + elapsedTime, Toast.LENGTH_LONG).show();

                View view = proteusView.getView();
                if (view != null) {
                    container.addView(view, layoutParams);
                }
                MainActivity.this.setContentView(container);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private JsonElement getJsonFromFile(int resId) {
        InputStream inputStream = getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return gson.fromJson(reader, JsonElement.class);
    }
}
