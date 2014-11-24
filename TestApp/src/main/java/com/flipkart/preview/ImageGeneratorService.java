package com.flipkart.preview;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.flipkart.layoutengine.builder.LayoutBuilderFactory;
import com.flipkart.layoutengine.builder.SimpleLayoutBuilder;
import com.flipkart.networking.API;
import com.flipkart.networking.request.BaseRequest;
import com.flipkart.networking.request.BitmapUploadRequest;
import com.flipkart.networking.request.RemoteRenderingRequest;
import com.flipkart.networking.request.components.OnRequestErrorListener;
import com.flipkart.networking.request.components.OnRequestFinishListener;
import com.flipkart.networking.request.components.RequestError;
import com.flipkart.networking.response.RemoteRenderingResponse;
import com.google.gson.JsonObject;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by kirankumar on 14/11/14.
 */
public class ImageGeneratorService extends Service {
    private Handler handler;
    private SimpleLayoutBuilder builder;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        builder = LayoutBuilderFactory.createSimpleLayoutBuilder(getBaseContext());
        builder.setSynchronousRendering(true);
        startPollingServer(intent);
        return Service.START_STICKY;
    }

    private void startPollingServer(Intent intent) {
       checkServer();
    }

    private void checkServer() {
        RemoteRenderingRequest request = new RemoteRenderingRequest();
        request.setOnResponseListener(new OnRequestFinishListener<RemoteRenderingResponse>() {
            @Override
            public void onRequestFinish(final BaseRequest<RemoteRenderingResponse> request) {
                final int id = request.getResponse().getResponse().getId();
                if(request.getResponse().getResponse().getLayout()!=null) {

                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bitmap = generateBitmap(request);
                            upload(bitmap,id);
                        }
                    };
                    // do not move this to main thread. Image handling breaks, causes thread block.
                    new Thread(r).start();


                }
                onRequestFinished();
            }
        });
        request.setOnErrorListener(new OnRequestErrorListener<RemoteRenderingResponse>() {
            @Override
            public void onRequestError(BaseRequest<RemoteRenderingResponse> request, RequestError error) {
                Log.e("Error","Could not fetch rendering request");
                onRequestFinished();
            }
        });
        API.getInstance(this.getApplicationContext()).processAsync(request);
    }

    private void upload(Bitmap bitmap, int id) {
        BitmapUploadRequest bitmapUploadRequest = new BitmapUploadRequest(bitmap,id);
        API.getInstance(this.getApplicationContext()).processAsync(bitmapUploadRequest);
    }

    private Bitmap generateBitmap(BaseRequest<RemoteRenderingResponse> request) {
        RemoteRenderingResponse response = request.getResponse();
        JsonObject layout = response.getResponse().getLayout();
        FrameLayout frameLayout = new FrameLayout(getBaseContext());
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.setBackgroundColor(Color.WHITE);


        View view = null;
        try {
            view = builder.build(frameLayout, layout);
        }catch (Exception e)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();
            TextView tv = new TextView(getBaseContext());
            tv.setText(e.getMessage()+"\n\n"+stackTrace);
            tv.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setTextSize(8);
            tv.setTextColor(Color.BLACK);
            view = tv;
            // do nothing
        }
            if(view!=null) {
            frameLayout.addView(view);
        }
        Bitmap b = null;
        if(view!=null) {
            b = loadBitmapFromView(frameLayout);
            Log.d("view", String.valueOf(b.getRowBytes()));
        }
        return b;

    }

    public Bitmap loadBitmapFromView(View view) {
        WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);

        float density = getBaseContext().getResources().getDisplayMetrics().density;
        bitmap = Bitmap.createScaledBitmap(bitmap,(int)(width/density),(int)(height/density),false);

        return bitmap;
    }

    private void onRequestFinished()
    {
       handler.postDelayed(new Runnable() {
           @Override
           public void run() {
             checkServer();
           }
       },1000);

    }


}
