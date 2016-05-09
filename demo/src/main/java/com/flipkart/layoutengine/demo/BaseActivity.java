package com.flipkart.layoutengine.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * BaseActivity
 *
 * @author aditya.sharat
 */
public abstract class BaseActivity extends AppCompatActivity {

    public BaseActivity() {
        PerformanceTracker.instance(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogbackConfigureHelper.configure();

        long startTime = System.currentTimeMillis();

        View view = createAndBindView();

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        onBuildComplete(elapsedTime);

        Toast.makeText(this, "render time: " + elapsedTime, Toast.LENGTH_SHORT).show();

        attachView(view);
    }

    abstract View createAndBindView();

    abstract void attachView(View view);

    abstract void onBuildComplete(long time);

    @Override
    protected void onPause() {
        PerformanceTracker.instance(this).apply();
        super.onPause();
    }
}
