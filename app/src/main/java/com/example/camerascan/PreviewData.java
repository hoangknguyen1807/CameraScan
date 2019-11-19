package com.example.camerascan;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.camerascan.R;

import java.io.File;

import lib.folderpicker.FolderPicker;


public class PreviewData extends Activity {
    Button buttonCopy;
    Button buttonSave, cdir;
    TextView textViewToReviewData;
    TextView pathtxt;//hiển thị đường dẫn lưu file hiện tại
    public String path;
    public String filename;

    private static final int FOLDERPICKER_CODE = 1666;//mã request code cố định

    ClipboardManager clipboardManager;
    ClipData clipData;

    @Override
    public void finish() {
        this.setResult(Activity.RESULT_OK);
        super.finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_preview_layout);

        buttonCopy=findViewById(R.id.buttonCopy);
        buttonSave=findViewById(R.id.buttonSave);
        textViewToReviewData=findViewById(R.id.textViewToReviewData);
        clipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

        SharedPreferences stored = getSharedPreferences("data", 0);
        path = stored.getString("pathtxt",
                Environment.getExternalStorageDirectory() + "/PDF_Converter/");

        pathtxt = findViewById(R.id.pathtxt);
        pathtxt.setText("Vị trí:" + path);

        cdir = findViewById(R.id.cdir);//phím đổi đường dẫn lưu file
        cdir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreviewData.this, FolderPicker.class);
                intent.putExtra("title", "Chọn đường dẫn");
                startActivityForResult(intent, FOLDERPICKER_CODE);
            }
        });


        Intent intentReceived = this.getIntent();
        String _data =intentReceived.getStringExtra("data");
        textViewToReviewData.setText(_data);

        buttonCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _data = textViewToReviewData.getText().toString(); //get data from text view
                clipData=ClipData.newPlainText("text",_data);
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(PreviewData.this,"Copied!",Toast.LENGTH_SHORT).show();
                //finish();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTXT();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        //lưu lại dữ liệu khi unfocus ứng dụng/khi đóng ứng dụng
        SharedPreferences stored = getSharedPreferences("data", 0);
        SharedPreferences.Editor store = stored.edit();
        store.putString("pathtxt", path);
        store.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Xử lí kết quả được trả về từ intent
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case FOLDERPICKER_CODE:
                    //trường hợp intent chọn đường dẫn lưu file trả kết quả
                    path = data.getExtras().getString("data") + "/";//gán kết quả cho path
                    pathtxt.setText("Vị trí:" + path);//set textview thành đường dẫn hiện hành
                    break;
            }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void saveTXT(){
        //hàm chứa xử lí khi bấm nút lưu nội dung xuống file .txt
        //Kiểm tra thư mục có tồn tại không ->nếu không -> hỏi tạo -> nếu từ chối -> hủy việc lưu
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
        //hiện dialog nhập tên file cần lưu
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

    public void fileExist(){
        //kiểm tra file có tồn tại không -> nếu có -> hỏi ghi đè -> nếu từ chối ->nhập lại tên
        if (filename.indexOf(".txt")==-1)
            filename+=".txt";
        File file = new File(path + filename);
        if (!file.exists()){
            SaveTXT write = new SaveTXT(PreviewData.this);
            write.execute(path, filename, textViewToReviewData.getText().toString());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Tập tin đã tồn tại.\nGhi đè?");
            builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SaveTXT write = new SaveTXT(PreviewData.this);
                    write.execute(path, filename, textViewToReviewData.getText().toString());
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
}
