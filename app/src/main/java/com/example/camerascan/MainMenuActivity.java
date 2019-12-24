package com.example.camerascan;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.camerascan.pdfconverter.PDFConverter;

public class MainMenuActivity extends Activity {


    Button buttonToTakePhotoScreen, buttonToScanScreen, buttonToEditScreen, buttonToConvertScreen,
    buttonToShareScreen, buttonToUploadScreen, buttonToDownloadScreen, buttonToLoginScreen,buttonToLogoutScreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);

        buttonToTakePhotoScreen = findViewById(R.id.buttonTakePhoto);
        buttonToScanScreen = findViewById(R.id.buttonScan);
        buttonToEditScreen = findViewById(R.id.buttonEdit);
        buttonToConvertScreen = findViewById(R.id.buttonConvert);
        buttonToShareScreen = findViewById(R.id.buttonShare);
        buttonToUploadScreen = findViewById(R.id.buttonUpload);
        buttonToDownloadScreen = findViewById(R.id.buttonDownload);
        buttonToLoginScreen=findViewById(R.id.buttonLogin);
        buttonToLogoutScreen=findViewById(R.id.buttonLogout);

        buttonToTakePhotoScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTakePhotoCamera = new Intent(MainMenuActivity.this, TakePhotoCamera.class);
                startActivity(intentTakePhotoCamera);
            }
        });

        buttonToScanScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChangeToScanScreen = new Intent(MainMenuActivity.this, ScanActivityOptions.class);
                startActivity(intentChangeToScanScreen);
            }
        });

        buttonToEditScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToEditScreen = new Intent(MainMenuActivity.this,
                        PickPhotoToEditActivity.class);
                startActivity(intentToEditScreen);
            }
        });

        buttonToConvertScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent PDF = new Intent(MainMenuActivity.this, PDFConverter.class);
                startActivity(PDF);
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
                Intent intentChangeToUploadScreen = new Intent(MainMenuActivity.this, UploadActivity.class);
                startActivity(intentChangeToUploadScreen);
            }
        });

        buttonToDownloadScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChangeToDownloadScreen = new Intent(MainMenuActivity.this, DownloadActivity.class);
                startActivity(intentChangeToDownloadScreen);
            }


        });



        buttonToLoginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChangeToDownloadScreen = new Intent(MainMenuActivity.this, LoginActivity.class);
                startActivity(intentChangeToDownloadScreen);
            }
    });

        buttonToLogoutScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             onDestroy();


            }
        });

    }
    @Override
    protected void onDestroy() {
        // delete session
        SharedPreferences.Editor editor = getSharedPreferences("DeviceToken",MODE_PRIVATE).edit();
        editor.remove("email");
        editor.remove("password");
        editor.apply();
        Toast.makeText(this, "Logout success", Toast.LENGTH_SHORT).show();
        super.onDestroy();

    }

}
