package com.flipkart.layoutengine.demo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flipkart.layoutengine.demo.models.Data;
import com.flipkart.layoutengine.testapp.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class NativeActivity extends BaseActivity {

    private static final String IMAGE_URL = "http://img6a.flixcart.com/www/prod/images/flipkart_logo_retina-9fddfff2.png";
    private static final String STRING_ACHIEVEMENTS = "Achievements - ";

    private Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        data = getJsonFromFile(R.raw.data_init);
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("InflateParams")
    protected View createAndBindView() {
        View view = getLayoutInflater().inflate(R.layout.activity_native, null, false);
        bindView(view);
        return view;
    }

    @Override
    void attachView(View view) {
        setContentView(view);
    }

    @Override
    void onBuildComplete(long time) {
        PerformanceTracker.instance(this).updateNativeRenderTime(time);
    }

    private void bindView(View view) {
        TextView tv = (TextView) view.findViewById(R.id.html_text_view);
        tv.setText(Html.fromHtml(getString(R.string.html)));

        ImageView iv = (ImageView) view.findViewById(R.id.url_image_view);
        loadImage(iv, IMAGE_URL);

        bindUserView(view);
    }

    @SuppressLint("SetTextI18n")
    private void bindUserView(View view) {
        TextView userName = (TextView) view.findViewById(R.id.user_name);
        userName.setText(data.user.name);

        TextView userLevel = (TextView) view.findViewById(R.id.user_level);
        userLevel.setText(data.user.level + "");

        TextView userAchievements = (TextView) view.findViewById(R.id.user_achievements);
        userAchievements.setText(STRING_ACHIEVEMENTS + data.user.achievements + "/" + data.metaData.totalAchievements);
    }

    private void loadImage(final ImageView view, String urlString) {
        URL url;
        try {
            url = new URL(urlString);
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
                view.setImageBitmap(result);
            }
        }.execute(url);
    }

    private Data getJsonFromFile(int resId) {
        InputStream inputStream = getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return new Gson().fromJson(reader, Data.class);
    }
}
