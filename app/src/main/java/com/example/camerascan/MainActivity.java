package com.example.camerascan;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_PERMISSION_STORAGE = 1;

    public static final int OPEN_IMAGE_CODE = 7;

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

        Button btnUpload = findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        Button openImage = findViewById(R.id.btnOpenImage);
        openImage.setOnClickListener(this);

        loadingDialog = BaseActivity.getLoadingDialog(this, R.string.loading,
                false);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.btnOpenImage:
                this.openImageFromStorage();
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
                    break;
                case ACTION_REQUEST_EDITIMAGE:
                    handleEditorImage(data);
                    break;*/
            }
        }
    }

    private void handleOpenImageFromStorage(Intent data) {
        path = data.getStringExtra("imgPath");
        loadImage(path);
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

}
