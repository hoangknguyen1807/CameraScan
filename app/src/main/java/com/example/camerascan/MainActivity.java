package com.example.camerascan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    Button btnCamera;
    ImageView imgPhoto;
    int REQUEST_CODE_CAMERA= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCamera = findViewById(R.id.btnCamera);
        imgPhoto=findViewById(R.id.imgPhoto);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,REQUEST_CODE_CAMERA);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==REQUEST_CODE_CAMERA&&resultCode==RESULT_OK&& data!=null){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");// mặc định

            imgPhoto.setImageBitmap(bitmap);

        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}
