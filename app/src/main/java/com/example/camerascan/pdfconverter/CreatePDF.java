package com.example.camerascan.pdfconverter;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreatePDF extends
        AsyncTask<Bitmap, Void, String> {
    //Class convert ảnh sang file pdf

    PDFConverter callerContext; //context gọi ASyncTask
    ProgressDialog dialog = null;
    Image image;// biến đóng vai trò chứa tham số truyền vào

    public CreatePDF (Context callerContext){
        //Hàm khởi tạo khi gọi new CreatePDF, truyền context gọi hàm vào ASyncTask
        this.callerContext = (PDFConverter) callerContext;
        dialog = new ProgressDialog(callerContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Tiền xử lí ASynTask
        //Cài đặt hiển thị dialog

        this.dialog.setMessage("Vui lòng chờ!\nĐang tạo file pdf...");
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    @Override
    protected String doInBackground(Bitmap... bitmaps) {
        //xử lí ASyncTask
        //this.dialog.setMessage("Đang xử lí...");
        Bitmap bitmap = bitmaps[0];

        Document doc = new Document();//tạo trang document mới

        String state = Environment.getExternalStorageState();   //kiểm tra bộ nhớ
        if (!Environment.MEDIA_MOUNTED.equals(state)) {

            //nếu bộ nhớ ko được mount thì ko ghi được dữ liệu
            return null;
        }

        try {
            ByteArrayOutputStream stream3 = new ByteArrayOutputStream();//đưa ảnh bitmap về dạng mảng byte
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream3);//

            image = Image.getInstance(stream3.toByteArray());//truyền vào ảnh đã được xử lí để convert PDF

            File dir = new File(callerContext.path); //mở đường dẫn được truyền vào
            if (!dir.exists()){
                dir.mkdir(); //tạo nếu đường dẫn chưa tồn tại
            }

            //mở file pdf cần tạo | tạo nếu chưa tồn tại
            File file = new File(callerContext.path, callerContext.filename);

            FileOutputStream fOut = new FileOutputStream(file); //tạo stream để ghi dữ liệu vào
            //file pdf được tạo

            PdfWriter.getInstance(doc,fOut); //đồng bộ stream ghi dữ liệu với document

            doc.open(); //mở document

            int indentation = 0;

            float size;

            float scaler;

            if (image.getWidth()>=image.getHeight()) {

                size = image.getWidth();

                //tạo kích thước scale ảnh cho phù hợp với trang dữ liệu
                scaler = ((doc.getPageSize().getWidth() - doc.leftMargin()
                        - doc.rightMargin() - indentation) / size) * 100;
            } else {
                size = image.getHeight();

                scaler = ((doc.getPageSize().getHeight() - doc.topMargin()
                        - doc.bottomMargin() - indentation) / size) * 100;
            }
            //scale ảnh theo kích thước trên
            //image.scaleToFit(doc.getPageSize().getWidth(),doc.getPageSize().getHeight());
            image.scalePercent(scaler);

            image.setAlignment(Image.ALIGN_CENTER);


            //ghi ảnh vào document
            doc.add(image);
        } catch (FileNotFoundException e) {
            Log.e("File Failed", "File Not Found");               //kiểm soát
        } catch (DocumentException de) {                                    //exception
            Log.e("File Failed", "File Not Found");               //
        } catch (IOException ioe){                                          //
            Log.e("Instance Failed", "Failed Get Instance");      //
        } finally {
            doc.close(); //đóng document
            Log.e("OK", "OK");
        }
        return "OK";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //hậu xử lí ASyncTask
        dialog.dismiss();
        //Kiểm tra việc tạo file pdf có thành công không (tồn tại hay không)
        File file = new File(callerContext.path + callerContext.filename);
        if (file.exists()) {
            Toast.makeText(callerContext, "Tập tin PDF đã được lưu", Toast.LENGTH_LONG).show();
            //gọi intent xem file đã được tạo
            callerContext.previewPDF();
        } else {
            Toast.makeText(callerContext, "Tạo tập tin PDF không thành công", Toast.LENGTH_LONG).show();
        }
    }
}
