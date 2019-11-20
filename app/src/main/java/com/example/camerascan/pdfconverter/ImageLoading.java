package com.example.camerascan.pdfconverter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageLoading extends AsyncTask<Intent, Void, Bitmap> {
    //class dùng để tải ảnh lên image view (+ tiền xử lí để chuyển pdf) sau khi được chọn từ gallery
    ProgressDialog dialog;
    private ImageLoader<Bitmap> callerContext; //context gọi ASyncTask
    Image img = null; //biến tạm chứa kết quả trả về
    byte[] tmp = null;//biến tạm chứa kết quả trả về

    public ImageLoading(ImageLoader cb){
        //khởi tạo -> truyền vào context gọi ASyncTask
        this.callerContext = cb;
        dialog = new ProgressDialog(cb);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //tiền xử lí ASyncTask
        //cài đặt hiển thị dialog
        callerContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        this.dialog.setMessage("Vui lòng chờ!\nĐang tải ảnh...");
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    @Override
    protected Bitmap doInBackground(Intent... intents) {
        //xử lí ASyncTask
        Uri selectedImage = intents[0].getData();//lấy dữ liệu truyền vào
        Bitmap res = null;//biến tạm chứa kết quả trả về
        try {
            //chuyển uri ảnh được chọn sang dạng Bitmap
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(callerContext.getContentResolver(), selectedImage);
            res = bitmap;//lưu kết quả trả về
        } catch (FileNotFoundException e){
            Log.e("File Failed", "File Not Found"); //kiểm soát exception
        } catch (IOException ioe) {                             //
            Log.e("File Failed", "File Not Found"); //
        }
        return res; //trả về kết quả là ảnh dạng bitmap
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        //hậu xử lí ASyncTask
        dialog.dismiss();
        //truyền các kết quả trả về vào hàm onTaskComplete để context gọi ASyncTask có thể sử dụng
        callerContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        callerContext.onTaskComplete(bitmap, img, tmp);

    }
}
