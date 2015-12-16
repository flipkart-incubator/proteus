package com.flipkart.layoutengine.demo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String RENDER_NATIVE = "Average Native Render Time: ";
    private static final String RENDER_PROTEUS = "Average Proteus Render Time: ";

    private PerformanceTracker tracker;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tracker = PerformanceTracker.instance(this);
        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.inflate_proteus);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProteusActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        view = findViewById(R.id.inflate_native);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NativeActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        TextView tv = (TextView) findViewById(R.id.perf_native);
        Pair<Long, Long> value = tracker.getAverageNativeRenderTime();
        tv.setText(RENDER_NATIVE + value.first + " (" + value.second + ")");

        tv = (TextView) findViewById(R.id.perf_proteus);
        value = tracker.getAverageProteusRenderTime();
        tv.setText(RENDER_PROTEUS + value.first + " (" + value.second + ")");
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onPostResume() {
        super.onPostResume();
        TextView tv = (TextView) findViewById(R.id.perf_native);
        Pair<Long, Long> value = tracker.getAverageNativeRenderTime();
        tv.setText(RENDER_NATIVE + value.first + " (" + value.second + ")");

        tv = (TextView) findViewById(R.id.perf_proteus);
        value = tracker.getAverageProteusRenderTime();
        tv.setText(RENDER_PROTEUS + value.first + " (" + value.second + ")");
    }

    @Override
    protected void onPause() {
        tracker.apply();
        super.onPause();
    }
}
