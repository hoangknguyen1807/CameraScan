package com.example.camerascan;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.camerascan.imageeditor.FileUtils;

import java.io.File;

import com.example.camerascan.BaseActivity;
import com.example.camerascan.imageeditor.EditImageActivity;
import com.example.camerascan.imageeditor.ImageEditorIntentBuilder;
import com.example.camerascan.imageeditor.utils.BitmapUtils;
import com.example.camerascan.OpenImageActivity;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_PERMISSION_STORAGE = 1;

    public static final int OPEN_IMAGE_CODE = 7;
    public static final int ACTION_EDITIMAGE = 9;

    private ImageView imgView;
    private Bitmap mainBitmap;
    private Dialog loadingDialog;
    private int imgWidth, imgHeight;

    private String path;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imgWidth = metrics.widthPixels;
        imgHeight = metrics.heightPixels;

        imgView = findViewById(R.id.imageView);

        Button btnEditImage = findViewById(R.id.btnEditImage);
        btnEditImage.setOnClickListener(this);

        Button openImage = findViewById(R.id.btnOpenImage);
        openImage.setOnClickListener(this);

        loadingDialog = BaseActivity.getLoadingDialog(this, R.string.loading,
                false);
    }

    @Override
    protected void onPause() {
        compositeDisposable.clear();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.btnOpenImage:
                this.openImageFromStorage();
                break;
            case R.id.btnEditImage:
                this.editImageClick();
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_STORAGE);
            return;
        }
        openImage();
    }

    private void openImage() {
        MainActivity.this.startActivityForResult(new Intent(
                        MainActivity.this,
                        OpenImageActivity.class),
                OPEN_IMAGE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_STORAGE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openImage();
        } /*else if (requestCode == REQUEST_PERMISSON_CAMERA
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case OPEN_IMAGE_CODE:
                    handleOpenImageFromStorage(data);
                    break;
                /*case TAKE_PHOTO_CODE:
                    handleTakePhoto();
                    break;*/
                case ACTION_EDITIMAGE:
                    handleEditorImage(data);
                    break;
            }
        }
    }

    private void handleOpenImageFromStorage(Intent data) {
        path = data.getStringExtra("imgPath");
        loadImage(path);
    }

    private void handleEditorImage(Intent data) {
        String newFilePath = data.getStringExtra(ImageEditorIntentBuilder.OUTPUT_PATH);
        boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IS_IMAGE_EDITED, false);

        if (isImageEdit) {
            Toast.makeText(this, getString(R.string.save_path, newFilePath), Toast.LENGTH_LONG).show();
        } else {
            newFilePath = data.getStringExtra(ImageEditorIntentBuilder.SOURCE_PATH);

        }

        loadImage(newFilePath);
    }

    private void loadImage(String imagePath) {
        compositeDisposable.clear();
        Disposable applyRotationDisposable = loadBitmapFromFile(imagePath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(subscriber -> loadingDialog.show())
                .doFinally(() -> loadingDialog.dismiss())
                .subscribe(
                        this::setMainBitmap,
                        e -> Toast.makeText(
                                this, R.string.load_error, Toast.LENGTH_SHORT).show()
                );

        compositeDisposable.add(applyRotationDisposable);
    }

    private void setMainBitmap(Bitmap sourceBitmap) {
        if (mainBitmap != null) {
            mainBitmap.recycle();
            mainBitmap = null;
            System.gc();
        }
        mainBitmap = sourceBitmap;
        imgView.setImageBitmap(mainBitmap);
    }

    private Single<Bitmap> loadBitmapFromFile(String filePath) {
        return Single.fromCallable(() ->
                BitmapUtils.getSampledBitmap(
                        filePath,
                        imgWidth / 4,
                        imgHeight / 4
                )
        );
    }

    private void editImageClick() {
        File outputFile = FileUtils.genEditFile();
        try {
            Intent intent = new ImageEditorIntentBuilder(this, path, outputFile.getAbsolutePath())
                    .withAddText()
                    .withPaintFeature()
                    .withFilterFeature()
                    .withRotateFeature()
                    .withCropFeature()
                    .withBrightnessFeature()
                    .withSaturationFeature()
                    .withBeautyFeature()
                    .forcePortrait(true)
                    .build();

            EditImageActivity.start(this, intent, ACTION_EDITIMAGE);
        } catch (Exception e) {
            Toast.makeText(this, "Please choose an image for edit", Toast.LENGTH_SHORT).show();
            Log.e("Demo App", e.getMessage());
        }
    }

}
