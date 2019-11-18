package com.example.camerascan.pdfconverter;

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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageLoading extends AsyncTask<Intent, Void, Bitmap> {

    PDFConverter callerContext;
    ProgressDialog dialog = null;

    public ImageLoading(Context callerContext) {
        this.callerContext = (PDFConverter) callerContext;
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
    protected Bitmap doInBackground(Intent... intents) {
        Uri selectedImage = intents[0].getData();
        if (selectedImage == null) {
            String filePath = intents[0].getStringExtra(PDFConverter.sourcePath);
            selectedImage = Uri.fromFile(new File(filePath));
        }
        Bitmap res = null;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(callerContext.getContentResolver(), selectedImage);
            res = bitmap;
            ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream3);
            callerContext.img = Image.getInstance(stream3.toByteArray());
        } catch (FileNotFoundException e) {

        } catch (IOException ioe) {

        } catch (BadElementException bee) {

        }
        return res;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        dialog.dismiss();
        callerContext.preview.setImageBitmap(bitmap);
    }
}
