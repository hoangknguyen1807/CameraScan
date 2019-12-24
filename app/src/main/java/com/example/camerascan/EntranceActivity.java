package com.example.camerascan;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.camerascan.ocrscanning.ScanCamera;

public class EntranceActivity extends Activity {
    Button buttonToMenu, buttonToScan, buttonToAbout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrance_layout);

        buttonToMenu=findViewById(R.id.buttonToMainMenu);
        buttonToScan=findViewById(R.id.buttonToQuickScan);
        buttonToAbout=findViewById(R.id.buttonToAbout);

        buttonToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChangeToMenuScreen = new Intent(EntranceActivity.this,MainMenuActivity.class);
                startActivity(intentChangeToMenuScreen);
            }
        });
        buttonToScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChangeToScan = new Intent(EntranceActivity.this, ScanCamera.class);
                startActivity(intentChangeToScan);
            }
        });


        buttonToAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // nothing to do :))
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
        super.onDestroy();

    }

}
