package com.example.camerascan.pdfconverter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageLoading extends AsyncTask<Intent, Void, Bitmap> {
    //class dùng để tải ảnh lên image view (+ tiền xử lí để chuyển pdf) sau khi được chọn từ gallery
    ProgressDialog dialog;
    Image img; //biến tạm chứa kết quả trả về
    byte[] tmp;//biến tạm chứa kết quả trả về
    private ImageLoader<Bitmap> callerContext; //context gọi ASyncTask

    public ImageLoading(ImageLoader cb) {
        //khởi tạo -> truyền vào context gọi ASyncTask
        this.callerContext = cb;
        dialog = new ProgressDialog(cb);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //tiền xử lí ASyncTask
        //cài đặt hiển thị dialog
        this.dialog.setMessage("Vui lòng chờ!\nĐang tải ảnh...");
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    @Override
    protected Bitmap doInBackground(Intent... intents) {
        //xử lí ASyncTask
        Uri selectedImage = intents[0].getData();//lấy dữ liệu truyền vào
        if (selectedImage == null) {
            String filePath = intents[0].getStringExtra(PDFConverter.sourcePath);
            selectedImage = Uri.fromFile(new File(filePath));
        }
        Bitmap res = null;//biến tạm chứa kết quả trả về
        try {
            //chuyển uri ảnh được chọn sang dạng Bitmap
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(callerContext.getContentResolver(), selectedImage);
            res = bitmap;//lưu kết quả trả về
            ByteArrayOutputStream stream3 = new ByteArrayOutputStream();//đưa ảnh bitmap về dạng mảng byte
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream3);//
            tmp = stream3.toByteArray();//lưu kết quả dạng mảng của ảnh
            img = Image.getInstance(stream3.toByteArray());//truyền vào ảnh đã được xử lí để convert PDF
        } catch (FileNotFoundException e) {
            Log.e("File Failed", "File Not Found"); //kiểm soát exception
        } catch (IOException ioe) {                             //
            Log.e("File Failed", "File Not Found"); //
        } catch (BadElementException bee) {                     //
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
        callerContext.onTaskComplete(bitmap, img, tmp);
    }
}
