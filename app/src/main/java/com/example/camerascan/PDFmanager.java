package com.example.camerascan;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;

import java.io.File;
import java.io.IOException;

import lib.folderpicker.FolderPicker;

public class PDFmanager extends ImageLoader {

    private static final int GALLERY_REQUEST_CODE = 1555;
    private static final int FOLDERPICKER_CODE = 1666;

    Image img;
    Button gallery, convert, cdir;
    ImageView preview;
    String filename;
    String path;
    TextView pathtxt;
    Bitmap bitmap = null;
    byte[] streamArray = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_manager);

        SharedPreferences stored = getSharedPreferences("data", 0);
        path = stored.getString("pathpdf",
                Environment.getExternalStorageDirectory() + "/PDF_Converter/");

        preview = findViewById(R.id.preview);

        pathtxt = findViewById(R.id.pathtxt);
        pathtxt.setText("Vị trí:" + path);

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
        convert.setEnabled(false);

        cdir = findViewById(R.id.cd);
        cdir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PDFmanager.this, FolderPicker.class);
                intent.putExtra("title", "Chọn đường dẫn");
                startActivityForResult(intent, FOLDERPICKER_CODE);
            }
        });

        if (savedInstanceState!=null) {
            if (savedInstanceState.getParcelable("image")!=null) {
                bitmap = savedInstanceState.getParcelable("image");
                preview.setImageBitmap(bitmap);

                convert.setEnabled(savedInstanceState.getBoolean("clickable"));
                try {
                    img = Image.getInstance(savedInstanceState.getByteArray("convert"));
                } catch (IOException ioe) {

                } catch (BadElementException bee) {

                }
            }
            /*savedInstanceState.putParcelable("image",null);
            savedInstanceState.putByteArray("convert",null);*/
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences stored = getSharedPreferences("data", 0);
        SharedPreferences.Editor store = stored.edit();
        store.putString("pathpdf", path);
        store.commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (bitmap!=null) {
            outState.putParcelable("image", bitmap);
            outState.putBoolean("clickable", true);
            outState.putByteArray("convert", streamArray);
        }
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    ImageLoading imageLoading = new ImageLoading(PDFmanager.this);
                    imageLoading.execute(data);
                    break;
                case FOLDERPICKER_CODE:
                    path = data.getExtras().getString("data") + "/";
                    pathtxt.setText("Vị trí:" + path);
                    break;
            }
    }

    public void convert() {
        dirNotExist();
    }

    public void dirNotExist() {
        File dir = new File(path);
        if (!dir.exists()) {
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
        } else
            NameDialog();
    }

    public void NameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tên tập tin:");
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
                fileExist();
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

    public void fileExist() {
        if (filename.indexOf(".pdf") == -1)
            filename += ".pdf";
        File file = new File(path + filename);
        if (!file.exists()) {
            CreatePDF createPDF = new CreatePDF(PDFmanager.this);
            createPDF.execute(img);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Tập tin đã tồn tại.\nGhi đè?");
            builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CreatePDF createPDF = new CreatePDF(PDFmanager.this);
                    createPDF.execute(img);//, dir, name);
                }
            });
            builder.setNegativeButton("Từ chối", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NameDialog();
                }
            });

            builder.show();
        }
    }

    public void previewPDF() {
        File file = new File(path + filename);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e("Instance Failed", "Failed Get Instance");
            }
        }
    }

    @Override
    public void onTaskComplete(Object result, Image img) {
        preview.setImageBitmap((Bitmap)result);
        this.img=img;
        convert.setEnabled(true);
    }
}