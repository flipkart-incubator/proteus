package com.flipkart.layoutengine.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.flipkart.layoutengine.EventType;
import com.flipkart.layoutengine.ImageLoaderCallback;
import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.builder.DataAndViewParsingLayoutBuilder;
import com.flipkart.layoutengine.builder.LayoutBuilderCallback;
import com.flipkart.layoutengine.builder.LayoutBuilderFactory;
import com.flipkart.layoutengine.testapp.R;
import com.flipkart.layoutengine.toolbox.BitmapLoader;
import com.flipkart.layoutengine.toolbox.Styles;
import com.flipkart.layoutengine.view.DataProteusView;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;


public class ProteusActivity extends AppCompatActivity {

    private DataProteusView proteusView;
    private Gson gson;
    private DataAndViewParsingLayoutBuilder builder;
    private FrameLayout container;
    private JsonObject pageLayout;
    private JsonObject data;
    private ViewGroup.LayoutParams layoutParams;
    private Styles styles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogbackConfigureHelper.configure();
        if (savedInstanceState == null) {
            gson = new Gson();
            styles = gson.fromJson(getJsonFromFile(R.raw.styles).getAsJsonObject(), Styles.class);
            createView();
        }
    }

    private void createView() {

        Map<String, JsonObject> layoutProvider = getProviderFromFile(R.raw.layout_provider);
        pageLayout = getJsonFromFile(R.raw.page_layout).getAsJsonObject();

        data = getJsonFromFile(R.raw.data_init).getAsJsonObject();

        builder = new LayoutBuilderFactory().getDataAndViewParsingLayoutBuilder(this, layoutProvider);
        builder.setListener(callback);
        builder.setBitmapLoader(bitmapLoader);

        container = new FrameLayout(ProteusActivity.this);
        layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        long startTime = System.currentTimeMillis();

        proteusView = (DataProteusView) builder.build(container, pageLayout, data, 0, styles);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        Toast.makeText(this, "render time: " + elapsedTime, Toast.LENGTH_LONG).show();

        container.addView(proteusView.getView(), layoutParams);
        setContentView(container);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private BitmapLoader bitmapLoader = new BitmapLoader() {
        @Override
        public Future<Bitmap> getBitmap(String imageUrl, View view) {
            return null;
        }

        @Override
        public void getBitmap(String imageUrl, final ImageLoaderCallback callback, View view, JsonObject layout) {
            URL url;
            try {
                url = new URL(imageUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return;
            }
            new AsyncTask<URL, Integer, Bitmap>() {

                @Override
                protected Bitmap doInBackground(URL... params) {
                    try {
                        return BitmapFactory.decodeStream(params[0].openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                protected void onPostExecute(Bitmap result) {
                    callback.onResponse(result);
                }
            }.execute(url);
        }
    };

    private LayoutBuilderCallback callback = new LayoutBuilderCallback() {

        @Override
        public void onUnknownAttribute(ParserContext context, String attribute, JsonElement element,
                                       JsonObject layout, View view, int childIndex) {
            Log.i("unknown-attribute", attribute + " in " + layout.toString());
        }

        @Override
        public ProteusView onUnknownViewType(ParserContext context, String viewType,
                                             JsonObject object, ProteusView parent, int childIndex) {
            return null;
        }

        @Override
        public JsonObject onLayoutRequired(ParserContext context, String viewType,
                                           JsonObject layout, ProteusView parent) {
            return null;
        }

        @Override
        public void onViewBuiltFromViewProvider(ProteusView createdView, String viewType,
                                                JsonObject layout, ProteusView parent, int childIndex) {
        }

        @Override
        public View onEvent(ParserContext context, View view, JsonElement attributeValue, EventType eventType) {
            Log.d("event", attributeValue.toString());
            return view;
        }

        @Override
        public PagerAdapter onPagerAdapterRequired(ParserContext parserContext, ProteusView parent,
                                                   List<ProteusView> children, JsonObject viewLayout) {
            return null;
        }

        @Override
        public Adapter onAdapterRequired(ParserContext parserContext, ProteusView parent,
                                         List<ProteusView> children, JsonObject viewLayout) {
            return null;
        }
    };

    private JsonElement getJsonFromFile(int resId) {
        InputStream inputStream = getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return gson.fromJson(reader, JsonElement.class);
    }

    private Map<String, JsonObject> getProviderFromFile(int resId) {
        InputStream inputStream = getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return gson.fromJson(reader, (new TypeToken<Map<String, JsonObject>>() {
        }).getType());
    }
}
