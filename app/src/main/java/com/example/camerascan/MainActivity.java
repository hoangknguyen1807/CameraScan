package com.example.camerascan;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.camerascan.imageeditor.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.camerascan.imageeditor.EditImageActivity;
import com.example.camerascan.imageeditor.ImageEditorIntentBuilder;
import com.example.camerascan.pdfconverter.PDFConverter;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_PERMISSION_STORAGE = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;

    public static final int OPEN_IMAGE_CODE = 7;
    public static final int TAKE_PHOTO_CODE = 8;
    public static final int ACTION_EDITIMAGE = 9;


    private ImageView imgView;
    private Bitmap mainBitmap;
    private Dialog loadingDialog;
    private int imgWidth, imgHeight;

    private String imgPath;
    private String takenImgPath;

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
        Button btnTakePhoto = findViewById(R.id.btnCamera);
        btnTakePhoto.setOnClickListener(this);

        Button btnEditImage = findViewById(R.id.btnEditImage);
        btnEditImage.setOnClickListener(this);

        Button btnOpenImage = findViewById(R.id.btnOpenImage);
        btnOpenImage.setOnClickListener(this);

        Button btnConvert = findViewById(R.id.btnConvert);
        btnConvert.setOnClickListener(this);


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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCamera:
                this.takePhotoCamera();
                break;
            case R.id.btnOpenImage:
                this.openImageFromStorage();
                break;
            case R.id.btnEditImage:
                this.editImageClick();
                break;
            case R.id.btnConvert:
                this.convertImageToPDF();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImage();
            } else
                Toast.makeText(MainActivity.this,
                        "External storage Permission DENIED", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhotoCamera();
            }
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
        /*Intent intentOpenImage = new Intent(MainActivity.this,
                OpenImageActivity.class);
        this.startActivityForResult(intentOpenImage,
                OPEN_IMAGE_CODE);*/
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, OPEN_IMAGE_CODE);
    }

    private void takePhotoCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_IMAGE_CAPTURE);
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            /*File imageFile = null;
            try {
                imageFile = createImageFile();
            } catch (IOException exc)
            {
                Toast.makeText(this,
                        "Cannot create new image file", Toast.LENGTH_SHORT).show();
                Log.e("Demo App", exc.getMessage());
            }

            if (imageFile != null)
            {
                Uri uri = FileProvider.getUriForFile(
                        this, "com.example.android.fileprovider",
                        imageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        uri);

            }*/
            startActivityForResult(takePictureIntent, TAKE_PHOTO_CODE);
        }
    }

    private void loadImage(String imagePath) {
        /*compositeDisposable.clear();
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

        compositeDisposable.add(applyRotationDisposable);*/

        Uri selectedImage = Uri.fromFile(new File(imagePath));
        Bitmap res;
        try {
            res = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), selectedImage);
        } catch (FileNotFoundException exc) {
            Log.e("Demo App", exc.getMessage());
            return;
        } catch (IOException exc) {
            Log.e("Demo App", exc.getMessage());
            return;
        }
        imgView.setImageBitmap(res);
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
            Intent intentEdit = new ImageEditorIntentBuilder(this, imgPath, outputFile.getAbsolutePath())
                    .withAddText()
                    .withPaintFeature()
                    /*.withFilterFeature()*/
                    .withRotateFeature()
                    .withCropFeature()
                    /*.withBrightnessFeature()
                    .withSaturationFeature()*/
                    .withStickerFeature()
                    /*.withBeautyFeature()*/
                    .forcePortrait(true)
                    .build();

            EditImageActivity.start(this, intentEdit, ACTION_EDITIMAGE);
        } catch (Exception e) {
            Toast.makeText(this, "Please choose an image for edit", Toast.LENGTH_SHORT).show();
            Log.e("Demo App", e.getMessage());
        }
    }

    private void convertImageToPDF() {
        try {
            if (imgPath.isEmpty() || imgPath == null)
                throw new Exception("Source image path required.");
            Intent convertToPDF = new Intent(MainActivity.this,
                    PDFConverter.class);
            convertToPDF.putExtra(PDFConverter.sourcePath, imgPath);
            startActivity(convertToPDF);
        } catch (Exception e) {
            Toast.makeText(this, "Please choose an image to convert", Toast.LENGTH_SHORT).show();
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
                case TAKE_PHOTO_CODE:
                    handleTakePhoto(data);
                    break;
                case ACTION_EDITIMAGE:
                    handleEditorImage(data);
                    break;
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,   /* prefix */
                ".jpg",          /* suffix */
                storageDir       /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        // currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void handleTakePhoto(Intent data) {
        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
        //imgView.setImageBitmap(imageBitmap);
        try {
            imgPath = createImageFile().getAbsolutePath();
        } catch (IOException exc) {
            Toast.makeText(this,
                    "Cannot create new image file", Toast.LENGTH_SHORT).show();
            Log.e("Demo App", exc.getMessage());
        }
        //com.example.camerascan.imageeditor.utils.BitmapUtils.saveBitmap(imageBitmap, takenImgPath);
        BitmapUtils.saveBitmap(imageBitmap, imgPath);
        loadImage(imgPath);
    }

    private void handleOpenImageFromStorage(Intent data) {
        Uri uri = data.getData();
        imgPath = UriUtil.getPath(MainActivity.this, uri);
        Toast.makeText(this, imgPath, Toast.LENGTH_SHORT).show();
        loadImage(imgPath);
        //File myfile = new File(data.getData().getPath());//data.getStringExtra("imgPath");
        //imgPath = myfile.getAbsolutePath();

//        imgPath = data.getStringExtra("imgPath");
//        loadImage(imgPath);

    }

    private void handleEditorImage(Intent data) {
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

}
