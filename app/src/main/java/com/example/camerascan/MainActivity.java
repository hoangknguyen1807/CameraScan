package com.example.camerascan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class MainActivity extends Activity {

    private static final int GALLERY_REQUEST_CODE = 1555;
    Random r = new Random();
    int i1 = r.nextInt(10000 - 5) + 5;
    String name = "demo"+i1+".pdf";
    //path of the pdf file which will get saved

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView text = findViewById(R.id.textView);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
                text.setText("ok");
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
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream3);
                        Image image = Image.getInstance(stream3.toByteArray());
                        CreatePdf(image);

                    } catch (FileNotFoundException e){

                    } catch (IOException ioe) {

                    } catch (BadElementException bee) {

                    }
                    break;

            }
    }

    private void CreatePdf(Image image){
        Document doc = new Document();

        try {
            File file = new File(Environment.getExternalStorageDirectory()+ "/PDFdemo/",
                    "demo.pdf");

            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc,fOut);

            doc.open();

            //String filename = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"PDFdemo/demo.jpg";

            int indentation = 0;
            float scaler = ((doc.getPageSize().getWidth() - doc.leftMargin()
                    - doc.rightMargin() - indentation) / image.getWidth()) * 100;

            image.scalePercent(scaler);
            doc.add(image);
        } catch (FileNotFoundException e) {
            Log.e("File Failed", "File Not Found");
        } catch (DocumentException de) {
            Log.e("File Failed", "File Not Found");
        } catch (IOException ioe){
            Log.e("Instance Failed", "Failed Get Instance");
        } finally {
            doc.close();
            Log.e("OK", "OK");
        }
    }
}
