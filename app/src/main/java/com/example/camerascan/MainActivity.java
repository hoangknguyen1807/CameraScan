package com.example.camerascan;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
import androidx.core.content.FileProvider;

import com.example.camerascan.imageeditor.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.example.camerascan.imageeditor.EditImageActivity;
import com.example.camerascan.imageeditor.ImageEditorIntentBuilder;
import com.example.camerascan.pdfconverter.PDFConverter;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    public static final int REQUEST_PERMISSION_STORAGE = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;

    public static final int OPEN_IMAGE_CODE = 7;
    public static final int TAKE_PHOTO_CODE = 8;
    public static final int ACTION_EDITIMAGE = 9;


    private ImageView imgView;
    private Bitmap mainBitmap;
    private Dialog loadingDialog;
    private int imgWidth, imgHeight;

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    /* member fields for Zoom - from here */
    private static final String TAG = "Touch";
    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    int mode = NONE;
    /* member fields for Zoom - to here */

    private String imgPath;
    Uri takenPhotoUri;

    //private CompositeDisposable compositeDisposable = new CompositeDisposable();

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
        imgView.setOnTouchListener(this);

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
        //compositeDisposable.clear();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //compositeDisposable.dispose();
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
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
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
            File imageFile = null;
            try {
                imageFile = createImageFile();
            } catch (IOException exc) {
                Toast.makeText(this,
                        "Cannot create new image file", Toast.LENGTH_SHORT).show();
                Log.e("Demo App", exc.getMessage());
            }

            if (imageFile != null) {
                imgPath = imageFile.getAbsolutePath();
                takenPhotoUri = FileProvider.getUriForFile(
                        this, "com.example.android.fileprovider",
                        imageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        takenPhotoUri);
                startActivityForResult(takePictureIntent, TAKE_PHOTO_CODE);
            }
        }
    }

    private int loadImage(String imagePath) {
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
            return -1;
        } catch (IOException exc) {
            Log.e("Demo App", exc.getMessage());
            return -2;
        }
        imgView.setImageBitmap(res);
        return 0;
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // nên cài đặt cái tác vụ onTouch (Zoom) này vào trong một class
        // để dễ sử dụng lên các ảnh khác
        imgView = (ImageView) v;

        imgView.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;


        dumpEvent(event);
        // Handle touch events here...

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG"); // write to LogCat
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    // create the transformation in the matrix  of points
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                } else if (mode == ZOOM) {
                    // pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f) {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; // setting the scaling of the
                        // matrix...if scale > 1 means
                        // zoom in...if scale < 1 means
                        // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        imgView.setImageMatrix(matrix); // display the transformation on screen

        return true; // indicate event was handled
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * -----------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * -----------------------------------------------------------------------
     */

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Show an event in the LogCat view, for debugging
     */
    private void dumpEvent(MotionEvent event) {
        String[] names = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d("Touch Events ---------", sb.toString());
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
        } else if (resultCode == RESULT_CANCELED) {
            switch (requestCode) {
                case TAKE_PHOTO_CODE:
                    // Canceled, so imgPath is set to blank
                    imgPath = "";
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
        // at this point, data is null
        if (loadImage(imgPath) == -1)
            // FileNotFound, -1, set imgPath to blank
            imgPath = "";
        /*Bitmap imgBitmap;
        try
        {
            imgBitmap = android.provider.MediaStore.Images.Media.getBitmap(this.getContentResolver()
                    , takenPhotoUri);
            imgView.setImageBitmap(imgBitmap);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_LONG).show();
            Log.e("Failed to load image", e.getMessage());
            // Image not found, imgPath is set to blank
            imgPath = "";
        }*/
    }

    private void handleOpenImageFromStorage(Intent data) {
        Uri uri = data.getData();
        try {

            imgPath = UriUtil.getPath(MainActivity.this, uri);
            if (imgPath == null)
                throw new NullPointerException("Unable to get image absolute path");
        } catch (NullPointerException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT);
        }
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
