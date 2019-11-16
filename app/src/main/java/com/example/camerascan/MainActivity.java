package com.example.camerascan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {

    TextView text;
    Button dir, confirm, save;
    EditText content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toPDF(View view){
        Intent PDF = new Intent(this, PDFmanager.class);
        startActivity(PDF);
    }

    public void toTXT(View view){
        Intent TXT = new Intent(this, TXTmanager.class);
        startActivity(TXT);
    }
}
