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

    MainActivity callerContext;
    ProgressDialog dialog = null;
    String filename = "NEW_TXT.txt";

    public SaveTXT(Context callerContext){
        this.callerContext = (MainActivity) callerContext;
        dialog = new ProgressDialog(callerContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setMessage("Vui lòng chờ!\nĐang lưu file ...");
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    @Override
    protected Void doInBackground(String... strings) {
        //Text of the Document
        String textToWrite = strings[0];

        //Checking the availability state of the External Storage.
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {

            //If it isn't mounted - we can't write into it.
            return null;
        }

        //Create a new file that points to the root directory, with the given name:
        File file = new File(Environment.getExternalStorageDirectory()+ "/PDFdemo/", filename);

        //This point and below is responsible for the write operation
        FileOutputStream outputStream = null;
        try {
            file.createNewFile();
            //second argument of FileOutputStream constructor indicates whether
            //to append or create new file if one exists
            outputStream = new FileOutputStream(file, true);

            outputStream.write(textToWrite.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
        Toast.makeText(callerContext, "Lưu file thành công", Toast.LENGTH_LONG).show();
    }
}
