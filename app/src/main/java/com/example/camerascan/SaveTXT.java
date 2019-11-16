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

    TXTmanager callerContext;
    ProgressDialog dialog = null;

    public SaveTXT(Context callerContext){
        this.callerContext = (TXTmanager) callerContext;
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
        String path= strings[0];
        String filename = strings[1];
        String textToWrite = strings[2];

        //Checking the availability state of the External Storage.
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {

            //If it isn't mounted - we can't write into it.
            return null;
        }

        //Create a new file that points to the root directory, with the given name:
        File file = new File(path, filename);

        //This point and below is responsible for the write operation
        FileOutputStream outputStream = null;
        try {
            file.createNewFile();
            //second argument of FileOutputStream constructor indicates whether
            //to append or create new file if one exists
            outputStream = new FileOutputStream(file, false);

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
