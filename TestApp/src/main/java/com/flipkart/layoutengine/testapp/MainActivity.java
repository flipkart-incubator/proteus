package com.flipkart.layoutengine.testapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.flipkart.layoutengine.builder.LayoutBuilder;
import com.flipkart.networking.API;
import com.flipkart.networking.request.BaseRequest;
import com.flipkart.networking.request.HomeRequest;
import com.flipkart.networking.request.components.OnRequestErrorListener;
import com.flipkart.networking.request.components.OnRequestFinishListener;
import com.flipkart.networking.request.components.RequestError;
import com.flipkart.networking.response.HomeResponse;
import com.google.gson.JsonObject;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HomeRequest request = new HomeRequest();
        request.setOnResponseListener(createOnResponse());
        request.setOnErrorListener(new OnRequestErrorListener<HomeResponse>() {
            @Override
            public void onRequestError(BaseRequest<HomeResponse> request, RequestError error) {
                Toast.makeText(MainActivity.this,"Rquest error "+error.getReason(),Toast.LENGTH_LONG).show();
            }
        });
        API.getInstance(this.getApplicationContext()).processAsync(request);

    }

    private OnRequestFinishListener<HomeResponse> createOnResponse() {
        return new OnRequestFinishListener<HomeResponse>() {
            @Override
            public void onRequestFinish(BaseRequest<HomeResponse> request) {
                HomeResponse response = request.getResponse();
                JsonObject layout = response.getResponse().getLayout();
                LayoutBuilder builder = new LayoutBuilder(MainActivity.this);
                View view = builder.build(layout);
                MainActivity.this.setContentView(view);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
