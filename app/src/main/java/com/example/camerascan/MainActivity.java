package com.example.camerascan;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends Activity {
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
                CreatePdf();
                text.setText("ok");
            }
        });
    }

    private void CreatePdf(){
        Document doc = new Document();

        try {
            File file = new File(Environment.getExternalStorageDirectory()+ "/PDFdemo/",
                    "demo.pdf");

            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc,fOut);

            doc.open();

            String filename = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"PDFdemo/demo.jpg";

            Image image = Image.getInstance(filename);

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
