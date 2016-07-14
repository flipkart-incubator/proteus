/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Flipkart Internet Pvt. Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.flipkart.android.proteus.demo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flipkart.android.proteus.demo.models.Data;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class NativeActivity extends BaseActivity {

    private static final String IMAGE_URL = "https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png";
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
        userLevel.setText("(" + data.user.level + ")");

        TextView userAchievements = (TextView) view.findViewById(R.id.user_achievements);
        userAchievements.setText(STRING_ACHIEVEMENTS + data.user.achievements + "/" + data.metaData.totalAchievements);

        TextView userTags = (TextView) view.findViewById(R.id.user_tags);
        userTags.setText(getJoinedString(data.metaData.tags));

        TextView country = (TextView) view.findViewById(R.id.user_location_country);
        country.setText(data.user.location.country + ", ");

        TextView city = (TextView) view.findViewById(R.id.user_location_city);
        city.setText(data.user.location.city + ", ");

        TextView pincode = (TextView) view.findViewById(R.id.user_location_pincode);
        pincode.setText(data.user.location.pincode);

        TextView experience = (TextView) view.findViewById(R.id.user_experience);
        experience.setText("Experience : " + data.user.experience);

        TextView credits = (TextView) view.findViewById(R.id.user_credits);
        credits.setText("Credits : " + data.user.credits);
    }

    private String getJoinedString(List<String> tags) {
        StringBuilder builder = new StringBuilder();
        for (String tag: tags) {
            builder.append(tag).append(", ");
        }
        return builder.toString();
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
