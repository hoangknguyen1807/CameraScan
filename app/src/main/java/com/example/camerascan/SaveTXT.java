package com.example.camerascan;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class SaveTXT extends
        AsyncTask<String, Void, Void> {
    //ASynTask xử lí việc lưu text xuống file .txt
    TXTmanager callerContext;//context gọi ASyncTask
    ProgressDialog dialog = null;

    public SaveTXT(Context callerContext){
        //khởi tạo --> truyền vào context gọi ASyncTask
        this.callerContext = (TXTmanager) callerContext;
        dialog = new ProgressDialog(callerContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //tiền xử lí ASyncTask
        //cài đặt hiển thị dialog
        this.dialog.setMessage("Vui lòng chờ!\nĐang lưu file ...");
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    @Override
    protected Void doInBackground(String... strings) {
        //xử lí ASyncTask
        String path= strings[0];            //
        String filename = strings[1];       //
        String textToWrite = strings[2];    //lấy dữ liệu truyền vào

        //Kiểm tra bộ tình trạng bộ nhớ
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {

            //nếu bộ nhớ ko được mount -> không thể ghi
            return null;
        }

        //Tạo file với đường dẫn và tên
        File file = new File(path, filename);

        //tạo luồng output ghi dữ liệu
        FileOutputStream outputStream = null;
        try {
            file.createNewFile();//tạo file mới

            //đồng bộ luồng output với file được tạo, tham số thứ 2: ghi nối tiếp hay xóa rồi ghi lại file
            outputStream = new FileOutputStream(file, false);

            outputStream.write(textToWrite.getBytes()); //ghi dữ liệu vào file
            outputStream.flush();                       //
            outputStream.close();                       //
        } catch (Exception e) {
            e.printStackTrace(); //kiểm soát Exception
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //hậu xử lí ASyncTask
        dialog.dismiss();
        //kiểm tra việc tạo có thành công hay không (file có tồn tại không)
        File file = new File(callerContext.path + callerContext.filename);
        if (file.exists()) {
            Toast.makeText(callerContext, "Tập tin TXT đã được lưu", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(callerContext, "Tạo tập tin TXT không thành công", Toast.LENGTH_LONG).show();
        }
    }
}
