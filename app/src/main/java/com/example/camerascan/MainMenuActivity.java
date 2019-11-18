package com.example.camerascan;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;

public class MainMenuActivity extends Activity {
    Button buttonToTakePhotoScreen, buttonToScanScreen, buttonToEditScreen, buttonToConvertScreen,
    buttonToShareScreen, buttonToUploadScreen, buttonToDownloadScreen, buttonToLogoutScreen;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buttonToTakePhotoScreen=findViewById(R.id.buttonTakePhoto);
        buttonToScanScreen=findViewById(R.id.buttonScan);
        buttonToEditScreen=findViewById(R.id.buttonEdit);
        buttonToConvertScreen=findViewById(R.id.buttonConvert);
        buttonToShareScreen=findViewById(R.id.buttonShare);
        buttonToUploadScreen=findViewById(R.id.buttonUpload);
        buttonToDownloadScreen=findViewById(R.id.buttonDownload);
        buttonToLogoutScreen=findViewById(R.id.buttonLogout);
    }
}
