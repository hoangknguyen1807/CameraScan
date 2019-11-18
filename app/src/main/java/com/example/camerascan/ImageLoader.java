package com.example.camerascan;

import android.app.Activity;

import com.itextpdf.text.Image;

public abstract class ImageLoader<Bitmap> extends Activity {
    public abstract void onTaskComplete(Bitmap result, Image img);
}