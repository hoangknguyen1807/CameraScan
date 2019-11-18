package com.example.camerascan;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.camerascan.R;


public class PreviewData extends Activity {
    Button buttonCopy;
    Button buttonSave;
    TextView textViewToReviewData;

    ClipboardManager clipboardManager;
    ClipData clipData;

    @Override
    public void finish() {
        this.setResult(Activity.RESULT_OK);
        super.finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_preview_layout);

        buttonCopy=findViewById(R.id.buttonCopy);
        buttonSave=findViewById(R.id.buttonSave);
        textViewToReviewData=findViewById(R.id.textViewToReviewData);
        clipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);



        Intent intentReceived = this.getIntent();
        String _data =intentReceived.getStringExtra("data");
        textViewToReviewData.setText(_data);

        buttonCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _data = textViewToReviewData.getText().toString(); //get data from text view
                clipData=ClipData.newPlainText("text",_data);
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(PreviewData.this,"Copied!",Toast.LENGTH_SHORT).show();
                //finish();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
