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

        text = findViewById(R.id.textView);

        dir = findViewById(R.id.dir);
        dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        content = findViewById(R.id.content);

        confirm = findViewById(R.id.confirm);

        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveTXT write = new SaveTXT(MainActivity.this);
                write.execute(content.getText().toString());
            }
        });
    }

    public void toPDF(View view){
        Intent PDF = new Intent(this, PDFmanager.class);
        startActivity(PDF);
    }
}
