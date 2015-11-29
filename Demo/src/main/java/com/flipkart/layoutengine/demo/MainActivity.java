package com.flipkart.layoutengine.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.flipkart.layoutengine.testapp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.inflate_proteus);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProteusActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    /*@SuppressLint("InflateParams")
    private View createAndBindView() {
        View view = getLayoutInflater().inflate(R.layout.activity_main, null, false);
        bindView(view);
        return view;
    }

    private void bindView(View view) {

    }*/
}
