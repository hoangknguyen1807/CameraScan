package com.example.camerascan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lib.folderpicker.FolderPicker;

public class TakePhotoCamera extends Activity {
    public static final String defaultPath = "/aio_scanner/";
    // private static final int REQUEST_PERMISSION_CAMERA = 15;
    // private static final int REQUEST_PERMISSION_WRITE = 16;
    public static final int REQUEST_PERMISSIONS_CODE = 11;
    public static final int TAKE_PHOTO_CODE = 12;
    private static final int FOLDERPICKER_CODE = 10;
    private final String[] requiredPermissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static File imageFile;
    String chosenPath = null;
    TextView txtViewShowPath;
    String pathDCIM = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.takephoto_layout);

        Button btnChangePath = findViewById(R.id.buttonChangePath);
        Button btnCamera = findViewById(R.id.buttonCamera);
        txtViewShowPath = findViewById(R.id.txtViewShowPath);

        pathDCIM = Environment.
                getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).
                getAbsolutePath();
        txtViewShowPath.setText("Save at:\n" +
                pathDCIM + defaultPath);
        btnChangePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TakePhotoCamera.this, FolderPicker.class);
                intent.putExtra("title", "Chọn đường dẫn");
                startActivityForResult(intent, FOLDERPICKER_CODE);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        requestPermissionCheck();
    }


    private void requestPermissionCheck() {
        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_WRITE);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSION_CAMERA);
            return;
        }*/
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
                ||
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(this, requiredPermissions,
                    REQUEST_PERMISSIONS_CODE);
            return;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //takePhoto();
            } else {
                Toast.makeText(this,
                        "Camera Permission or Write Permission DENIED",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            imageFile = null;

            try {
                imageFile = createImageFile();
            } catch (IOException exc) {
                Toast.makeText(this,
                        "Cannot create new image file", Toast.LENGTH_SHORT).show();
                Log.e("Demo App", exc.getMessage());
            }

            if (imageFile != null) {
                Uri takenPhotoUri = FileProvider.getUriForFile(
                        this, "com.example.android.fileprovider",
                        imageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        takenPhotoUri);

                startActivityForResult(takePictureIntent,
                        TAKE_PHOTO_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        String path = chosenPath;
        if (path == null) {
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
                    + defaultPath;
        }
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File storageDir = new File(path);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File image = File.createTempFile(
                imageFileName,   /* prefix */
                ".jpg",          /* suffix */
                storageDir       /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        // currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String message = null;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO_CODE:
//                    Toast.makeText(getApplicationContext(), imageFile.getAbsolutePath(),
//                            Toast.LENGTH_LONG);
                    toastMessage("Saved image path : " + imageFile.getAbsolutePath());
                    takePhoto();
                    break;
                case FOLDERPICKER_CODE:
                    //trường hợp intent chọn đường dẫn lưu file trả kết quả
                    chosenPath = data.getExtras().getString("data") + "/"; // gán kết quả cho path
                    txtViewShowPath.setText("Save at:\n" + chosenPath); // set textview thành đường dẫn hiện hành
                    break;
            }
        } // not RESULT_OK
        else {
            if (resultCode == RESULT_CANCELED) {
                switch (requestCode) {
                    case TAKE_PHOTO_CODE:
                        if (imageFile.delete() == false) {
//                            Toast.makeText(TakePhotoCamera.this,
//                                    "Cannot delete created temp file",
//                                    Toast.LENGTH_SHORT);
                            message = "Cannot delete created temp file";
                        }

                        break;
                    case FOLDERPICKER_CODE:
//                        Toast.makeText(this,
//                                "Cannot choose folder to save",
//                                Toast.LENGTH_LONG);
                        message = "Cannot choose folder to save";
                        break;
                }
                if (message != null)
                    toastMessage(message);
            }
        }
    }

    private void toastMessage(String message) {
        Toast toast = Toast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 520);
        toast.show();
    }
}
