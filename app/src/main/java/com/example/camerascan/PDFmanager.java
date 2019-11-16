package com.example.camerascan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.itextpdf.text.Image;

public class PDFmanager extends Activity {

    private static final int GALLERY_REQUEST_CODE = 1555;
    String name = "demo"+".pdf";
    //path of the pdf file which will get saved
    Image img;
    Button gallery, convert;
    ImageView preview;

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
            }
    }

    public void convert(){
        CreatePDF createPDF = new CreatePDF(PDFmanager.this);
        createPDF.execute(img);//, dir, name);
    }
}
