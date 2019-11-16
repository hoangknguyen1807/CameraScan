package com.example.camerascan;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.itextpdf.text.Image;

import java.io.File;

import lib.folderpicker.FolderPicker;

public class PDFmanager extends Activity {

    private static final int GALLERY_REQUEST_CODE = 1555;
    private static final int FOLDERPICKER_CODE = 1666;

    Image img;
    Button gallery, convert, cdir;
    ImageView preview;
    String filename;
    String path = Environment.getExternalStorageDirectory()+ "/PDF_Converter/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_manager);

        preview = findViewById(R.id.preview);

        gallery = findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });

        convert = findViewById(R.id.convert);
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convert();
            }
        });

        cdir = findViewById(R.id.cd);
        cdir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PDFmanager.this, FolderPicker.class);
                startActivityForResult(intent, FOLDERPICKER_CODE);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("dir",path);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        path = savedInstanceState.getString("dir");
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    ImageLoading imageLoading = new ImageLoading(PDFmanager.this);
                    imageLoading.execute(data);//, dir, name);
                    break;
                case FOLDERPICKER_CODE:
                    path = data.getExtras().getString("data");
                    break;
            }
    }

    public void convert(){
        dirNotExist();
    }

    public void dirNotExist(){
        File dir = new File(path);
        if (!dir.exists()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Đường dẫn không tồn tại.\nTạo thư mục?");
            builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NameDialog();
                }
            });
            builder.setNegativeButton("Từ chối", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
        else
            NameDialog();
    }

    public void NameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tên file:");
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filename = input.getText().toString();
                CreatePDF createPDF = new CreatePDF(PDFmanager.this);
                createPDF.execute(img);//, dir, name);
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
