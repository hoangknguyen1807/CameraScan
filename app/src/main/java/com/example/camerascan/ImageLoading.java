package com.example.camerascan;

import android.app.ProgressDialog;
import android.content.Context;
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

public class ImageLoading extends AsyncTask<Intent, Void, Void> {

    PDFmanager callerContext;
    ProgressDialog dialog = null;

    public ImageLoading(Context callerContext){
        this.callerContext = (PDFmanager) callerContext;
        dialog = new ProgressDialog(callerContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setMessage("Vui lòng chờ!\nĐang tải ảnh...");
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    @Override
    protected Void doInBackground(Intent... intents) {
        Uri selectedImage = intents[0].getData();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(callerContext.getContentResolver(), selectedImage);
            ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream3);
            callerContext.img = Image.getInstance(stream3.toByteArray());
        } catch (FileNotFoundException e){

        } catch (IOException ioe) {

        } catch (BadElementException bee) {

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
    }
}
