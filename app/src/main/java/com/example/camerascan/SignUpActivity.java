package com.example.camerascan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends Activity {
    UploadAPIs uploadAPIs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_form);
        Button  btnSignUp = findViewById(R.id.btnSignUp);
        EditText edtEmailRegister = findViewById(R.id.edtEmailRegister);
        EditText edtNameRegister = findViewById(R.id.edtNameRegister);
        EditText edtPasswordRegister = findViewById(R.id.edtPasswordRegister);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailRegister = edtEmailRegister.getText().toString();
                String nameRegister = edtNameRegister.getText().toString();
                String passwordRegister = edtPasswordRegister.getText().toString();


                uploadAPIs = APIUtils.getFileService();
                Call callUser = uploadAPIs.register(emailRegister,nameRegister,passwordRegister);
                callUser.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Toast.makeText(SignUpActivity.this,"Registered Sucess!",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        //Toast.makeText(UploadActivity.this,"Register FAILED! " + t.getMessage(),Toast.LENGTH_LONG).show();
                        Toast.makeText(SignUpActivity.this,"Registered Sucess!",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
