package com.example.camerascan.pdfconverter;

import android.app.Activity;

import com.itextpdf.text.Image;


public abstract class ImageLoader<Bitmap> extends Activity {
    //Abstract đại diện để nhiều activity có thể cùng sử dụng ASyncTask ImageLoading
    public abstract void onTaskComplete(Bitmap result, Image img, byte[] array);//hàm để override
    //sau khi ASyncTask được xử lí xong
}
