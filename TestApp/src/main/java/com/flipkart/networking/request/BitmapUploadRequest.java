package com.flipkart.networking.request;

import android.graphics.Bitmap;

import com.flipkart.config.Config;
import com.flipkart.networking.response.BitmapUploadResponse;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by kirankumar on 17/11/14.
 */
public class BitmapUploadRequest extends BaseRequest<BitmapUploadResponse> {

    private MultipartEntity entity;

    public BitmapUploadRequest(Bitmap b, int id) {
        super(Config.instance.get(Config.StringKey.ApiBaseUrl), Config.instance.get(Config.ApiPathKey.BitmapUploadRequest)+"&id="+id);
        buildEntity(b);
    }

    private void buildEntity(Bitmap b) {
        this.entity = new MultipartEntity();
        if(b!=null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
            this.entity.addPart("bitmap", new InputStreamBody(bs, "image/png", "bitmap.png"));
        }
    }

    @Override
    public Class<BitmapUploadResponse> getResponseClass() {
        return BitmapUploadResponse.class;
    }

    @Override
    public MultipartEntity getMultiPartEntity() {
        return entity;
    }
}
