package com.example.camerascan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

public class MainMenuActivity extends Activity {
    Button buttonToTakePhotoScreen, buttonToScanScreen, buttonToEditScreen, buttonToConvertScreen,
    buttonToShareScreen, buttonToUploadScreen, buttonToDownloadScreen, buttonToLogoutScreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);

        buttonToTakePhotoScreen=findViewById(R.id.buttonTakePhoto);
        buttonToScanScreen=findViewById(R.id.buttonScan);
        buttonToEditScreen=findViewById(R.id.buttonEdit);
        buttonToConvertScreen=findViewById(R.id.buttonConvert);
        buttonToShareScreen=findViewById(R.id.buttonShare);
        buttonToUploadScreen=findViewById(R.id.buttonUpload);
        buttonToDownloadScreen=findViewById(R.id.buttonDownload);
        //buttonToLogoutScreen=findViewById(R.id.buttonLogout);

        buttonToTakePhotoScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonToScanScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChangeToScanScreen  =new Intent(MainMenuActivity.this, ScanActivityOptions.class);
                startActivity(intentChangeToScanScreen);

            }
        });

        buttonToEditScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonToConvertScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonToShareScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChangeToShareScreen = new Intent(MainMenuActivity.this, ShareActivity.class);
                startActivity(intentChangeToShareScreen);

            }
        });

        buttonToUploadScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonToDownloadScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//        buttonToLogoutScreen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });


    }
}
