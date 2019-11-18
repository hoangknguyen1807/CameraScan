package com.example.camerascan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageLoading extends AsyncTask<Intent, Void, Bitmap> {

    ProgressDialog dialog;
    private ImageLoader<Bitmap> callerContext;
    Image img;

    public ImageLoading(ImageLoader cb){
        this.callerContext = cb;
        dialog = new ProgressDialog(cb);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setMessage("Vui lòng chờ!\nĐang tải ảnh...");
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    @Override
    protected Bitmap doInBackground(Intent... intents) {
        Uri selectedImage = intents[0].getData();
        Bitmap res = null;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(callerContext.getContentResolver(), selectedImage);
            res = bitmap;
            ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream3);
            img = Image.getInstance(stream3.toByteArray());
        } catch (FileNotFoundException e){

        } catch (IOException ioe) {

        } catch (BadElementException bee) {

        }
        return res;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        dialog.dismiss();
        callerContext.onTaskComplete(bitmap, img);
    }
}