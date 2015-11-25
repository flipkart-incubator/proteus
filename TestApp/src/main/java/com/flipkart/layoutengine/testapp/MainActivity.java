package com.flipkart.layoutengine.testapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.flipkart.layoutengine.EventType;
import com.flipkart.layoutengine.GifLoaderCallback;
import com.flipkart.layoutengine.ImageLoaderCallBack;
import com.flipkart.layoutengine.ParserContext;
import com.flipkart.layoutengine.builder.DataAndViewParsingLayoutBuilder;
import com.flipkart.layoutengine.builder.LayoutBuilder;
import com.flipkart.layoutengine.builder.LayoutBuilderCallback;
import com.flipkart.layoutengine.builder.LayoutBuilderFactory;
import com.flipkart.layoutengine.toolbox.BitmapLoader;
import com.flipkart.layoutengine.toolbox.GifLoader;
import com.flipkart.layoutengine.toolbox.IdGenerator;
import com.flipkart.layoutengine.toolbox.Styles;
import com.flipkart.layoutengine.view.DataProteusView;
import com.flipkart.layoutengine.view.ProteusView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Future;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private DataProteusView proteusView;
    private Gson gson;
    private DataAndViewParsingLayoutBuilder builder;
    private FrameLayout container;
    private JsonObject layout;
    private JsonObject data;
    private ViewGroup.LayoutParams layoutParams;
    private Styles styles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            gson = new Gson();
            styles = gson.fromJson(getJsonFromFile(R.raw.styles).getAsJsonObject(), Styles.class);
            createView();
        }
    }

    private void createView() {

        JsonObject layoutProvider = getJsonFromFile(R.raw.layout_provider).getAsJsonObject();

        layout = getJsonFromFile(R.raw.page_layout).getAsJsonObject();
        data = getJsonFromFile(R.raw.data_1).getAsJsonObject();

        builder = new LayoutBuilderFactory().getDataAndViewParsingLayoutBuilder(this, layoutProvider);

        builder.setListener(createCallback());

        builder.setBitmapLoader(new BitmapLoader() {
            @Override
            public Future<Bitmap> getBitmap(String imageUrl, View view) {
                return null;
            }

            @Override
            public void getBitmap(String imageUrl, final ImageLoaderCallBack callback, View view, JsonObject layout) {
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
        });

        builder.setGifLoader(new GifLoader() {
            @Override
            public Future<GifDrawable> getGifDrawable(String imageUrl, View view) {
                return null;
            }

            @Override
            public void getGifDrawable(String imageUrl, final GifLoaderCallback gifLoaderCallback, View view, JsonObject layout) {
                URL url;
                try {
                    url = new URL(imageUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return;
                }
                new AsyncTask<URL, Integer, GifDrawable>() {

                    @Override
                    protected GifDrawable doInBackground(URL... urls) {
                        try {
                            Log.d("GIF", "DOING IN BACKGROUND");
                            InputStream inputStream =  urls[0].openConnection().getInputStream();

                            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                            int nRead;
                            byte[] data = new byte[16384];

                            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                                buffer.write(data, 0, nRead);
                            }

                            buffer.flush();

                            GifDrawable drawable = new GifDrawable(buffer.toByteArray());
                            drawable.stop();
                            return drawable;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(GifDrawable gifDrawable) {
                        gifLoaderCallback.onResponse(gifDrawable);
                    }
                }.execute(url);
            }
        });

        container = new FrameLayout(MainActivity.this);
        layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        long startTime = System.currentTimeMillis();

        proteusView = (DataProteusView) builder.build(container, layout, data, 0, styles);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        Toast.makeText(this, "render time: " + elapsedTime, Toast.LENGTH_LONG).show();

        final GifImageView imageView = (GifImageView) proteusView.getView().findViewById(
                IdGenerator.getInstance().getUnique("gif_image_view"));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GifDrawable drawable = (GifDrawable) imageView.getDrawable();
                if(!drawable.isRunning()) {
                    drawable.reset();
                    drawable.addAnimationListener(new AnimationListener() {
                        @Override
                        public void onAnimationCompleted(int loopNumber) {
                            Toast.makeText(MainActivity.this, "Animation completed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    drawable.start();
                }
            }
        });

        container.addView(proteusView.getView(), layoutParams);
        setContentView(container);
    }

    private LayoutBuilderCallback createCallback() {
        return new LayoutBuilderCallback() {

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
            case R.id.action_new_data_1:
                return true;

            case R.id.action_new_data_2:
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private JsonElement getJsonFromFile(int resId) {
        InputStream inputStream = getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return gson.fromJson(reader, JsonElement.class);
    }

    public class ProteusViewHolderAdapter extends RecyclerView.Adapter<ProteusViewHolderAdapter.ProteusViewHolder> {

        private final JsonArray specs;
        private final LayoutBuilder layoutBuilder;
        private final JsonObject layout;

        public ProteusViewHolderAdapter(JsonArray specs, LayoutBuilder layoutBuilder, JsonObject layout) {
            this.specs = specs;
            this.layoutBuilder = layoutBuilder;
            this.layout = layout;
        }

        @Override
        public ProteusViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            DataProteusView proteusView = (DataProteusView) layoutBuilder.build(viewGroup, layout,
                    new JsonObject(), 0, styles);
            return new ProteusViewHolder(proteusView);
        }

        @Override
        public void onBindViewHolder(ProteusViewHolder viewHolder, int i) {
            viewHolder.getProteusItemView().updateData(specs.get(i).getAsJsonObject());
        }

        @Override
        public int getItemCount() {
            return specs.size();
        }

        public class ProteusViewHolder extends RecyclerView.ViewHolder {

            private final ProteusView proteusItemView;

            public ProteusViewHolder(ProteusView itemView) {
                super(itemView.getView());
                this.proteusItemView = itemView;
            }

            public ProteusView getProteusItemView() {
                return proteusItemView;
            }
        }
    }
}
