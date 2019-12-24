package com.example.camerascan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadActivity extends Activity {
    UploadAPIs uploadAPIs;
    GridView gridView;
    DataAdapter dataAdapter;
    ArrayList<PhotoDto> photos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
// get session if logged in
        SharedPreferences prefs = getSharedPreferences("DeviceToken",MODE_PRIVATE);
        String email = prefs.getString("email", null);
        String password = prefs.getString("password", null);

        System.out.println("EMAIL USER: " + email);
        System.out.println("PASSWORD USER: " + password);

        uploadAPIs = APIUtils.getFileService();
        Call <ArrayList<PhotoDto>> listLinkPhoto = uploadAPIs.download(email,password);
        listLinkPhoto.enqueue(new Callback<ArrayList<PhotoDto>>() {
            @Override
            public void onResponse(Call <ArrayList<PhotoDto>> call, Response <ArrayList<PhotoDto>> response) {
                Toast.makeText(DownloadActivity.this,"Download Sucess!",Toast.LENGTH_LONG).show();
                photos = response.body();
                for (PhotoDto photoDto : photos){
                    System.out.println(photoDto.getUrl());
                }

                gridView=findViewById(R.id.gridView);

               // prepareData();
                dataAdapter = new DataAdapter(photos,getApplicationContext());
                gridView.setAdapter(dataAdapter);




            }

            @Override
            public void onFailure(Call <ArrayList<PhotoDto>> call, Throwable t) {
                //Toast.makeText(UploadActivity.this,"Register FAILED! " + t.getMessage(),Toast.LENGTH_LONG).show();
                Toast.makeText(DownloadActivity.this,"Error",Toast.LENGTH_LONG).show();
            }
        });
    }
    private ArrayList prepareData() {
        photos.add(new PhotoDto("selfiecamera_2019-12-21-13-30-24-311.jpg","http://res.cloudinary.com/dy5yspoxj/image/upload/v1576909874/SRWRestImageBase/selfiecamera_2019-12-21-13-30-24-311.jpg.jpg"));
        photos.add(new PhotoDto("selfiecamera_2019-12-21-13-30-24-311.jpg","http://res.cloudinary.com/dy5yspoxj/image/upload/v1576909874/SRWRestImageBase/selfiecamera_2019-12-21-13-30-24-311.jpg.jpg"));
        photos.add(new PhotoDto("selfiecamera_2019-12-21-13-30-24-311.jpg","http://res.cloudinary.com/dy5yspoxj/image/upload/v1576909874/SRWRestImageBase/selfiecamera_2019-12-21-13-30-24-311.jpg.jpg"));
        photos.add(new PhotoDto("selfiecamera_2019-12-21-13-30-24-311.jpg","http://res.cloudinary.com/dy5yspoxj/image/upload/v1576909874/SRWRestImageBase/selfiecamera_2019-12-21-13-30-24-311.jpg.jpg"));
        photos.add(new PhotoDto("selfiecamera_2019-12-21-13-30-24-311.jpg","http://res.cloudinary.com/dy5yspoxj/image/upload/v1576909874/SRWRestImageBase/selfiecamera_2019-12-21-13-30-24-311.jpg.jpg"));
        photos.add(new PhotoDto("selfiecamera_2019-12-21-13-30-24-311.jpg","http://res.cloudinary.com/dy5yspoxj/image/upload/v1576909874/SRWRestImageBase/selfiecamera_2019-12-21-13-30-24-311.jpg.jpg"));
        photos.add(new PhotoDto("selfiecamera_2019-12-21-13-30-24-311.jpg","http://res.cloudinary.com/dy5yspoxj/image/upload/v1576909874/SRWRestImageBase/selfiecamera_2019-12-21-13-30-24-311.jpg.jpg"));

        return photos;
    }
}
