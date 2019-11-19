package com.example.camerascan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakePhotoCamera extends Activity {
    public static final int REQUEST_PERMISSION_STORAGE = 11;
    public static final int REQUEST_PERMISSION_CAMERA = 22;

    public static final int TAKE_PHOTO_CODE = 10;

    File imageFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_STORAGE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSION_CAMERA);
            return;
        }
        takePhotoFromCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                Toast.makeText(this,
                        "External storage Permission DENIED",
                        Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhotoFromCamera();
            } else {
                Toast.makeText(this,
                        "Camera Permission DENIED",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void takePhotoFromCamera() {
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
                // imgPath = imageFile.getAbsolutePath();
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
        File storageDir = Environment.getExternalStorageDirectory();
        //getExternalFilesDir(Environment.DIRECTORY_PICTURES);

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
        if (resultCode == RESULT_OK) {
            // insert action here
        } else {
            if (resultCode == RESULT_CANCELED) {
                switch (requestCode) {
                    case TAKE_PHOTO_CODE:
                        if (imageFile.delete() == false) {
                            Toast.makeText(this,
                                    "Cannot delete created temp file",
                                    Toast.LENGTH_SHORT);
                        }
                        break;
                }
            }
        }
    }

}
