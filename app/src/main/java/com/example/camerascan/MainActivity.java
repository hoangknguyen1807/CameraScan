package com.example.camerascan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity {

    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.textView);
    }

    public void toPDF(View view){
        Intent PDF = new Intent(this, PDFmanager.class);
        startActivity(PDF);
        text.setText("ok");
    }
}
