package com.example.camerascan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

public class ScanActivityOptions extends Activity {
    ImageButton buttonToCameraScreen, buttonToFilesScreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_options);

        buttonToCameraScreen=findViewById(R.id.buttonToCameraScreen);
        buttonToFilesScreen=findViewById(R.id.buttonToFilesScreen);

        buttonToFilesScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeToScreenFiles = new Intent(ScanActivityOptions.this, ScanFiles.class);
                startActivity(changeToScreenFiles);
            }
        });

        buttonToCameraScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeToScreenCamera = new Intent(ScanActivityOptions.this, ScanCamera.class);
                startActivity(changeToScreenCamera);
            }
        });

    }
}
