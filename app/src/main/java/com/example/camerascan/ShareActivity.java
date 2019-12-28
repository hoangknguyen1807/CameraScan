package com.example.camerascan;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;

public class ShareActivity extends Activity {
    Button buttonToShare;
    Button buttonToPickFile;
    TextView textViewToShowPath;

    final private int REQUEST_PICK_FILE = 555;

    Uri _fileUri;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public void requestPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_layout);

        buttonToShare = findViewById(R.id.buttonToShare);
        buttonToPickFile = findViewById(R.id.buttonToPickFile);
        textViewToShowPath=findViewById(R.id.textViewToShowPath);

        requestPermissions(ShareActivity.this);

        if (savedInstanceState!=null)
        {
            textViewToShowPath.setText(savedInstanceState.getString("tmpUri"));
            _fileUri=Uri.parse(savedInstanceState.getString("tmpUri"));
        }

        buttonToShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewToShowPath.getText()=="")
                {
                    Toast.makeText(ShareActivity.this, "Please choose file first!",Toast.LENGTH_SHORT).show();
                }
                else {
                    //File fileName = new File(_fileUri.getPath());
                    //Uri forLolipop = FileProvider.getUriForFile(ShareActivity.this,
                    //      "com.example.android.fileprovider",fileName);

                    Uri forLolipop = _fileUri;

                    // Get the Uri from the external file and add it to the intent
                    //Uri forLolipop = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), _fileUri.getPath()));

                    Intent intentToShareSheet = new Intent(Intent.ACTION_SEND,forLolipop);
                    intentToShareSheet.setType("image/*");
                    intentToShareSheet.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                   // intentToShareSheet.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    //intentToShareSheet.setData(forLolipop);
                    intentToShareSheet.putExtra(Intent.EXTRA_STREAM,forLolipop);
                    startActivity(Intent.createChooser(intentToShareSheet, "OCR Share File"));
                }
            }
        });

        buttonToPickFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent requestFileIntent = new Intent(Intent.ACTION_GET_CONTENT);

                requestFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                requestFileIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                requestFileIntent.setType("*/*");
                startActivityForResult(requestFileIntent, REQUEST_PICK_FILE);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (_fileUri!=null)
            outState.putString("tmpUri",_fileUri.getPath());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK_FILE:
                    _fileUri = data.getData();
                    textViewToShowPath.setText(_fileUri.getPath());
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {}
    }

}
