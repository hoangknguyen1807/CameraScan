package com.example.camerascan.pdfconverter;

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

import com.example.camerascan.R;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;

import java.io.File;
import java.io.IOException;

import lib.folderpicker.FolderPicker;

public class PDFConverter extends ImageLoader {

    public static final String sourcePath = "source_path";

    //activity chính xử lí chuyển đổi ảnh -> pdf
    private static final int GALLERY_REQUEST_CODE = 1555;//
    private static final int FOLDERPICKER_CODE = 1666;//biến request code được gán cố định

    Image img;//lưu ảnh đã được xử lí để chuyển PDF
    Button gallery, convert, cdir;//button ở layout
    ImageView preview;//ImageView để preview sau khi chọn ảnh
    String filename;//chứa tên file pdf cần tạo
    String path;//đường dẫn chứa file
    TextView pathtxt;//textview hiển thị đường dẫn lưu hiện hành
    Bitmap bitmap = null;//dùng để saveInstanceState
    byte[] imgInBytes;//dùng để saveInstanceState

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_converter);

        //load lại dữ liệu (đường dẫn) mà người dùng đã chọn trước đây
        SharedPreferences stored = getSharedPreferences("data", 0);
        path = stored.getString("pathpdf",
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF_Converter/");

        preview = findViewById(R.id.preview);

        pathtxt = findViewById(R.id.pathtxt);
        pathtxt.setText("Vị trí:" + path);

        /*gallery = findViewById(R.id.gallery);//phím chọn ảnh từ gallery
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });*/

        convert = findViewById(R.id.convert);//phím chuyển ảnh sang pdf
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convert();
            }
        });
        convert.setEnabled(false);//chưa chọn ảnh thì phim chuyển PDF không bấm được

        cdir = findViewById(R.id.cd);//phím đổi đường dẫn vị trí lưu
        cdir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PDFConverter.this, FolderPicker.class);
                intent.putExtra("title", "Chọn đường dẫn");
                startActivityForResult(intent, FOLDERPICKER_CODE);
            }
        });

        if (savedInstanceState != null) {//khôi phục InstanceState
            if (savedInstanceState.getParcelable("image") != null) {
                bitmap = savedInstanceState.getParcelable("image");
                preview.setImageBitmap(bitmap);

                convert.setEnabled(savedInstanceState.getBoolean("clickable"));

                try {
                    byte[] middlemen = savedInstanceState.getByteArray("convert");
                    img = Image.getInstance(middlemen);
                    imgInBytes = middlemen;
                } catch (IOException ioe) {

                } catch (BadElementException bee) {

                }
            }
        }

        ImageLoading imageLoading = new ImageLoading(PDFConverter.this);
        imageLoading.execute(getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        //lưu lại dữ liệu khi unfocus ứng dụng/khi đóng ứng dụng
        SharedPreferences stored = getSharedPreferences("data", 0);
        SharedPreferences.Editor store = stored.edit();
        store.putString("pathpdf", path);
        store.commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //lưu InstanceState
        if (bitmap != null) {
            outState.putParcelable("image", bitmap);
            outState.putBoolean("clickable", true);
            outState.putByteArray("convert", imgInBytes);
        }
    }

    private void pickFromGallery() {
        //hàm gọi intent chọn ảnh từ gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//chọn kiểu là pick ảnh
        String[] mimeTypes = {"image/jpeg", "image/png"};//kiểu dữ liệu ảnh được hiển thị
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);//truyền kiểu dữ liệu vào intent được gọi
        startActivityForResult(intent, GALLERY_REQUEST_CODE);//gọi intent
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Xử lí kết quả được trả về từ intent
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    //trường hợp intent chọn ảnh từ gallery trả kết quả
                    //gọi intent load ảnh lên để preview và tiền xử lí ảnh để chuyển pdf
                    ImageLoading imageLoading = new ImageLoading(PDFConverter.this);
                    imageLoading.execute(data);
                    break;
                case FOLDERPICKER_CODE:
                    //trường hợp intent chọn đường dẫn lưu file trả kết quả
                    path = data.getExtras().getString("data") + "/";//gán kết quả cho path
                    pathtxt.setText("Vị trí:" + path);//set textview thành đường dẫn hiện hành
                    break;
            }
    }

    public void convert() {
        //hàm xử lí khi nhấn phím chuyển pdf
        dirNotExist();
    }

    public void dirNotExist() {
        //kiểm tra đường dẫn có tồn tại không -> hỏi tạo nếu ko tồn tại -> nếu hủy -> hủy việc tạo pdf
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
        //hiện dialog nhập tên cho file pdf cần tạo
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
        //kiểm tra file pdf cần tạo có tồn tại không -> hỏi ghi đè nếu tồn tại -> nếu từ chối -> đặt tên khác
        if (filename.indexOf(".pdf") == -1)
            filename += ".pdf";
        File file = new File(path + filename);
        if (!file.exists()) {
            CreatePDF createPDF = new CreatePDF(PDFConverter.this);
            createPDF.execute(img);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Tập tin đã tồn tại.\nGhi đè?");
            builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CreatePDF createPDF = new CreatePDF(PDFConverter.this);
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
        //hàm xem file pdf sau khi được tạo
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
    public void onTaskComplete(Object result, Image img, byte[] array) {
        //hàm nhận và xử lí kết quả trả về của ASyncTask ImageLoading
        bitmap = (Bitmap) result;//lưu ảnh bitmap
        preview.setImageBitmap(bitmap);//dán ảnh lên ImageView
        this.img = img;
        convert.setEnabled(true);//cho phép bấm phím chuyển pdf
        imgInBytes = array;
    }
}
