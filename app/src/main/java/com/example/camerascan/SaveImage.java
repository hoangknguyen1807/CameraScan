package com.example.camerascan;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class SaveImage extends
        AsyncTask<Bitmap, Void, Void> {

    MainActivity callerContext;
    ProgressDialog dialog = null;

    public SaveImage(Context callerContext){
        this.callerContext = (MainActivity) callerContext;
        dialog = new ProgressDialog(callerContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setMessage("Vui lòng chờ!\nĐang lưu file ...");
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    @Override
    protected Void doInBackground(Bitmap... bitmaps) {

        Bitmap bitmap = bitmaps[0];
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
        Toast.makeText(callerContext, "Lưu ảnh thành công", Toast.LENGTH_LONG).show();
    }
}
