package com.example.camerascan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.camerascan.imageeditor.EditImageActivity;
import com.example.camerascan.imageeditor.FileUtils;
import com.example.camerascan.imageeditor.ImageEditorIntentBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PickPhotoToEditActivity extends Activity implements View.OnClickListener {

    private static final int REQUEST_PERMISSION_WRITE = 31;
    private static final int OPEN_IMAGE_CODE = 32;
    private static final int EDIT_IMAGE_CODE = 23;

    private String imgPath = null;
    private ImageView imgView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pickphoto_layout);

        imgView = findViewById(R.id.imageViewEdit);

        Button btnPickPhoto = findViewById(R.id.btnPickPhoto);
        btnPickPhoto.setOnClickListener(this);

        Button btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPickPhoto:
                this.openImageFromStorage();
                break;
            case R.id.btnEdit:
                editImageClick();
                break;
        }
    }

    private void openImageFromStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            openImageWithPermissionsCheck();
        } else {
            openImage();
        }
    }

    private void openImageWithPermissionsCheck() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_WRITE);
            return;
        }
        openImage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_WRITE:
                if (grantResults.length > 0
                        &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImage();
                }
                break;
        }
    }

    private void openImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, OPEN_IMAGE_CODE);
    }

    private void editImageClick() {
        File outputFile = FileUtils.genEditFile();
        try {
            Intent intentEdit = new ImageEditorIntentBuilder(this, imgPath, outputFile.getAbsolutePath())
                    .withAddText()
                    .withPaintFeature()
                    /*.withFilterFeature()*/
                    .withRotateFeature()
                    .withCropFeature()
                    .withBrightnessFeature()
                    .withSaturationFeature()
                    .withStickerFeature()
                    /*.withBeautyFeature()*/
                    .forcePortrait(true)
                    .build();

            EditImageActivity.start(this, intentEdit, EDIT_IMAGE_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "Please choose an image for edit", Toast.LENGTH_SHORT).show();
            Log.e("Demo App", e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case OPEN_IMAGE_CODE:
                    handleOpenImageFromStorage(data);
                    break;
                case EDIT_IMAGE_CODE:
                    handleEditedImage(data);
                    break;
            }
        }
    }

    private void handleOpenImageFromStorage(Intent data) {
        Uri uri = data.getData();
        try {
            imgPath = UriUtil.getPath(PickPhotoToEditActivity.this, uri);
            if (imgPath == null)
                throw new NullPointerException("Unable to get image absolute path");
        } catch (NullPointerException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT);
        }
        //Toast.makeText(this, imgPath, Toast.LENGTH_SHORT).show();
        loadImage(imgPath);

        //File myfile = new File(data.getData().getPath());//data.getStringExtra("imgPath");
        //imgPath = myfile.getAbsolutePath();
        //imgPath = data.getStringExtra("imgPath");
        //loadImage(imgPath);
    }

    private void handleEditedImage(Intent data) {
        //String newFilePath = data.getStringExtra(ImageEditorIntentBuilder.OUTPUT_PATH);
        imgPath = data.getStringExtra(ImageEditorIntentBuilder.OUTPUT_PATH);
        boolean isImageEdited = data.getBooleanExtra(EditImageActivity.IS_IMAGE_EDITED, false);

        if (isImageEdited) {
            //Toast.makeText(this, getString(R.string.save_path, newFilePath), Toast.LENGTH_LONG).show();
            Toast.makeText(this, getString(R.string.save_path, imgPath), Toast.LENGTH_LONG).show();
        } else {
            //newFilePath = data.getStringExtra(ImageEditorIntentBuilder.SOURCE_PATH);
            imgPath = data.getStringExtra(ImageEditorIntentBuilder.SOURCE_PATH);
        }

        //loadImage(newFilePath);
        loadImage(imgPath);
    }

    private int loadImage(String imagePath) {
        Uri selectedImage = Uri.fromFile(new File(imagePath));
        Bitmap res;
        try {
            res = MediaStore.Images.Media.getBitmap(PickPhotoToEditActivity.this.getContentResolver(), selectedImage);
        } catch (FileNotFoundException exc) {
            Log.e("Demo App", exc.getMessage());
            return -1;
        } catch (IOException exc) {
            Log.e("Demo App", exc.getMessage());
            return -2;
        }
        imgView.setImageBitmap(res);
        return 0;
    }
}
