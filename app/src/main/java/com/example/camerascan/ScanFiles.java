package com.example.camerascan;


import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class ScanFiles extends Activity {

    final int REQUEST_SENDTOTXT = 222; //const to switch screen

    ImageButton buttonToOCR;
    ImageView imageViewToPreviewSelectedImage;
    Button buttonToSelectFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.files_selector_layout);

        buttonToOCR=findViewById(R.id.buttonOCR);
        imageViewToPreviewSelectedImage=findViewById(R.id.imageViewPreviewSelectedImage);
        buttonToSelectFile=findViewById(R.id.buttonToSelectFile);

//        if (savedInstanceState!=null) {
//            tmpVal = savedInstanceState.getInt("data");
//            textView.setText("hihi: " + tmpVal);
//        }


        buttonToOCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap img = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.testimg);

                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!textRecognizer.isOperational()) {//check initial
                    Log.w("MainActivity", "Detector dependencies are not yet available");

                    //check storage is enough?
                    IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                    boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

                    if (hasLowStorage) {
                        Toast.makeText(ScanFiles.this, "Storage isn't enough!", Toast.LENGTH_LONG).show();
                        Log.w("MainActivity", "Storage isn't enough!");
                    }
                }
                else {
                    Frame frame = new Frame.Builder().setBitmap(img).build();
                    SparseArray<TextBlock> items = textRecognizer.detect(frame);

                    StringBuilder stringBuilder = new StringBuilder();

                    for (int i = 0; i < items.size(); ++i) {
                        TextBlock item = items.valueAt(i);
                        stringBuilder.append(item.getValue());
                        stringBuilder.append("\n");
                    }

                    changeToPreviewScreen(stringBuilder.toString());
                }
            }
        });

        buttonToSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pich file from Vinh :)))
            }
        });
    }

    private void changeToPreviewScreen(String data)
    {
        Intent intentChangeToPreviewScreen = new Intent(ScanFiles.this, PreviewData.class);
        intentChangeToPreviewScreen.putExtra("data",data);
        startActivityForResult(intentChangeToPreviewScreen,REQUEST_SENDTOTXT);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        outState.putInt("data",tmpVal);
//        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==Activity.RESULT_OK && requestCode==REQUEST_SENDTOTXT)
        {
            Log.w("MainActivity", "finish");
           //do something to show success here :)
        }
    }

}
