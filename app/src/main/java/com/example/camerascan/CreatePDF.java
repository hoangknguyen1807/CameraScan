package com.example.camerascan;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreatePDF extends
        AsyncTask<Image, Void, String> {

    PDFmanager callerContext;
    ProgressDialog dialog = null;
    Image image;

    public CreatePDF (Context callerContext){
        this.callerContext = (PDFmanager) callerContext;
        dialog = new ProgressDialog(callerContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setMessage("Vui lòng chờ!\nĐang tạo file pdf...");
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    @Override
    protected String doInBackground(Image... images) {
        this.dialog.setMessage("Đang xử lí...");
        image = images[0];
        Document doc = new Document();

        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {

            //If it isn't mounted - we can't write into it.
            return null;
        }

        try {
            File file = new File(Environment.getExternalStorageDirectory()+ "/PDFdemo/",
                    callerContext.filename + ".pdf");

            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc,fOut);

            doc.open();

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
        return "OK";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        dialog.dismiss();
        Toast.makeText(callerContext, "File PDF đã được lưu", Toast.LENGTH_LONG).show();
    }
}
