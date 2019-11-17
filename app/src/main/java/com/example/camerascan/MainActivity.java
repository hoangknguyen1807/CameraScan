package com.example.camerascan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;


public class MainActivity extends Activity implements View.OnTouchListener {
    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f, MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    /**
     * Called when the activity is first created.
     */

    public final  int REQUEST_CODE_CAMERA=100;

     ImageView myImage;
    Uri uri;
    public int counter = 0;
    public final static int READ_EXTERNAL_REQUEST = 2;
    String mediaPath;
    String filePath="";
    public final static int PICK_IMAGE_REQUEST = 1;
    @Override
    public void onBackPressed() {
        onCreate(null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ZOOM IMAGE
        myImage = findViewById(R.id.imageView);

        myImage.setOnTouchListener(this);

        // Crop
        Button btnCrop = findViewById(R.id.btnCrop);
        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               CropImage.startPickImageActivity(MainActivity.this);

            }
        });

        // Rotate
        Button btnRotate = findViewById(R.id.btnRotate);
        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                roateImage(myImage);
            }
        });

        // Upload
        Button btnUpload = findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                setContentView(R.layout.login_form);
                final EditText edtEmail = findViewById(R.id.edtEmail);
                final EditText edtPassword = findViewById(R.id.edtPassword);
                Button btnLogin = findViewById(R.id.btnLogin);


                edtEmail.setText("admin@gmail.com");
                edtPassword.setText("123456");

                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplication(), "EMAIL: " + edtEmail.getText().toString() + ", PASSWORD: " + edtPassword.getText().toString(), Toast.LENGTH_LONG).show();
                        if (edtEmail.getText().toString().equals("admin@gmail.com") && edtPassword.getText().toString().equals("123456")) {
                            requestPermionAndPickImage();

                        }
                    }
                });

            }
        });

        // CONNECT CAMERA
        Button btnConnectCamera = findViewById(R.id.btnCamera);
        btnConnectCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},REQUEST_CODE_CAMERA);

            }
        });

        // SAVE IMAGE IN GALLERY
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myImage.invalidate();
                BitmapDrawable drawable = (BitmapDrawable) myImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "demo" , "demo");
            }
        });
    }

    private void roateImage(ImageView imageView) {
        Matrix matrix = new Matrix();
        imageView.setScaleType(ImageView.ScaleType.MATRIX); //required
        matrix.postRotate((float) (++counter)*90f, imageView.getDrawable().getBounds().width()/2,    imageView.getDrawable().getBounds().height()/2);
        imageView.setImageMatrix(matrix);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==REQUEST_CODE_CAMERA && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,REQUEST_CODE_CAMERA);

        }

        if (requestCode != READ_EXTERNAL_REQUEST) return;
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        } else {

            Toast.makeText(this,"YOU DO NOT HAVE ROLE CONNECT CAMERA",Toast.LENGTH_LONG).show();


        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUEST_CODE_CAMERA && resultCode==RESULT_OK && data!=null){


            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//            System.out.println("HEIGHT: " + bitmap.getHeight());
//
//            System.out.println("WIDTH: " + bitmap.getWidth());
//            int height = bitmap.getHeight();
//            int width = bitmap.getWidth();
//            int bounding = dpToPx(400);
//            float xScale = ((float) bounding) / width;
//            float yScale = ((float) bounding) / height;
//            float scale = (xScale <= yScale) ? xScale : yScale;
//            // Create a matrix for the scaling and add the scaling data
//            Matrix matrix = new Matrix();
//            matrix.postScale(scale, scale);
//            // Create a new bitmap and convert it to a format understood by the ImageView
//            Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
//
//
//            Drawable result = ConvertBitmapToDrawable(scaledBitmap);
//            myImage.setImageDrawable(result);

            //Bitmap  resized = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*2), (int)(bitmap.getHeight()*2), true);

            Drawable drawable = ConvertBitmapToDrawable(bitmap);

            myImage.setImageDrawable(drawable);
            // Apply the scaled bitmap
            //view.setImageBitmap(resized);
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null &&
                data.getData() != null) {
            // Khi đã chọn xong ảnh thì chúng ta tiến hành upload thôi
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mediaPath = cursor.getString(columnIndex);
            //str1.setText(mediaPath);
            // Set the Image in ImageView for Previewing the Media
            myImage.setImageBitmap(BitmapFactory.decodeFile(mediaPath));
            cursor.close();
            //Uri uri = data.getData();
            uploadToServer();
        }


        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri imageUri = CropImage.getPickImageResultUri(this,data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this,imageUri)){
                uri = imageUri;
                requestPermissions(new String[]{READ_EXTERNAL_STORAGE},0);
            }else {
                StartCrop(imageUri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                myImage.setImageURI(resultUri);

                Toast.makeText(this,"Image Update Successfully", Toast.LENGTH_LONG).show();



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }



        super.onActivityResult(requestCode, resultCode, data);


    }

    public void StartCrop(Uri imageUri){
        //CropImage.activity().start(MainActivity.this);
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

    public Drawable ConvertBitmapToDrawable(Bitmap bitmap)
    {

        Drawable drawable = new BitmapDrawable(getResources(),bitmap);
        return drawable;
    }

    private void requestPermionAndPickImage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            pickImage();
            return;
        }
        // Các bạn nhớ request permison cho các máy M trở lên nhé, k là crash ngay đấy.
        int result = ContextCompat.checkSelfPermission(this,
                READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        } else {
            requestPermissions(new String[]{
                    READ_EXTERNAL_STORAGE}, READ_EXTERNAL_REQUEST);
        }
    }

    public void pickImage() {
        // Gọi intent của hệ thống để chọn ảnh nhé.
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),
                PICK_IMAGE_REQUEST);
    }
    public void uploadToServer() {



        //Create a file object using file path
        File file = new File(filePath);
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("img", file.getName(), fileReqBody);
        //Create request body with text description and text media type
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");
        //
        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        UploadAPIs uploadAPIs = retrofit.create(UploadAPIs.class);
        Call call = uploadAPIs.uploadImage(part,description);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
            }
            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });
    }

    private String getRealPathFromURI(Uri uri,Activity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        myImage = (ImageView) v;

        myImage.setScaleType(ImageView.ScaleType.MATRIX);
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
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
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

        myImage.setImageMatrix(matrix); // display the transformation on screen

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

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
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
        String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
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



}
