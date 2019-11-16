package com.example.camerascan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class MainActivity extends Activity {

    TextView text;
    Button dir, confirm;
    EditText content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.textView);

        content = findViewById(R.id.content);

        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt=content.getText().toString();
                text.setText(txt);
            }
        });

        dir = findViewById(R.id.dir);
        dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveTXT write = new SaveTXT(MainActivity.this);
                write.execute(text.getText().toString());
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 9999:
                Log.i("Test", "Result URI " + data.getData());
                break;
        }
    }

    public void toPDF(View view){
        Intent PDF = new Intent(this, PDFmanager.class);
        startActivity(PDF);
        text.setText("ok");
    }
}
