package com.flipkart.layoutengine.demo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flipkart.layoutengine.testapp.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class NativeActivity extends AppCompatActivity {

    private static final String IMAGE_URL = "http://img6a.flixcart.com/www/prod/images/flipkart_logo_retina-9fddfff2.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long startTime = System.currentTimeMillis();

        View view = createAndBindView();

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        Toast.makeText(this, "render time: " + elapsedTime, Toast.LENGTH_LONG).show();

        setContentView(view);
    }

    @SuppressLint("InflateParams")
    private View createAndBindView() {
        View view = getLayoutInflater().inflate(R.layout.activity_native, null, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {
        TextView tv = (TextView) view.findViewById(R.id.html_text_view);
        tv.setText(Html.fromHtml(getString(R.string.html)));

        ImageView iv = (ImageView) view.findViewById(R.id.url_image_view);
        loadImage(iv, IMAGE_URL);
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
}
