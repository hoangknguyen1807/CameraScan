package com.example.camerascan.ocrscanning;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camerascan.PreviewData;
import com.example.camerascan.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class ScanCamera extends Activity {

    SurfaceView cameraView;
    TextView textViewResult;
    CameraSource cameraSource;
    Button buttonToTxtView;

    final int REQUEST_CAMERA = 111; //const to request camera
    final int REQUEST_SENDTOTXT = 222; //const to switch screen

    //request to use user's camera
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        //reset camera source
        if (cameraSource!=null)
            cameraSource.release();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_camera_layout);

        cameraView = findViewById(R.id.cameraView);
        textViewResult = findViewById(R.id.textViewResult);
        buttonToTxtView=findViewById(R.id.buttonToTxtView);

        buttonToTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSendTextToTxtView = new Intent(ScanCamera.this, PreviewData.class); //create intent to send data to another screen
                intentSendTextToTxtView.putExtra("data",textViewResult.getText().toString()); //put data to intent
                startActivityForResult(intentSendTextToTxtView,REQUEST_SENDTOTXT);  //call intent to move to another screen and wait for result
            }
        });

        DoProcess();
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        //reset camera source
//        if (cameraSource!=null)
//            cameraSource.release();
//    }

    protected void DoProcess()
    {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {//check initial
            Log.w("MainActivity", "Detector dependencies are not yet available");

            //check storage is enough?
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, "Storage isn't enough!", Toast.LENGTH_LONG).show();
                Log.w("MainActivity", "Storage isn't enough!");
            }
        } else {

            //link camera to variable
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {

                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(ScanCamera.this,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
                            return;
                        }
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                }
            });
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0) {
                        textViewResult.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < items.size(); ++i) {
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                textViewResult.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        //reset camera source
        if (cameraSource!=null)
            cameraSource.release();
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        DoProcess();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==Activity.RESULT_OK && requestCode==REQUEST_SENDTOTXT)
        {
            Log.w("MainActivity", "finish");
//            if (cameraSource!=null)
//                cameraSource.release();
//            DoProcess();
        }
    }

}