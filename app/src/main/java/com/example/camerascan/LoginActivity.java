package com.example.camerascan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
   private String email;
    private String password;
    UploadAPIs uploadAPIs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_form);
        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtPassword = findViewById(R.id.edtPassword);

        Button buttonSignUpScreen = findViewById(R.id.btnSignUp);
        buttonSignUpScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChangeToSignUpScreen = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intentChangeToSignUpScreen);


            }
        });



        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = edtEmail.getText().toString();
                password = edtPassword.getText().toString();
                Toast.makeText(LoginActivity.this, "email: " + email + " ,password: " + password, Toast.LENGTH_SHORT).show();

                uploadAPIs = APIUtils.getFileService();
                Call<LoginUserDto> loginUserDto = uploadAPIs.login(email);
                loginUserDto.enqueue(new Callback<LoginUserDto>() {
                    @Override
                    public void onResponse(Call <LoginUserDto> call, Response<LoginUserDto> response) {
                        System.out.println("information: " + response.body());
                        LoginUserDto result= response.body();
                        if (result!=null && result.getPassword().equals(password)){
                            // Set session after login
                            SharedPreferences.Editor editor = getSharedPreferences("DeviceToken",MODE_PRIVATE).edit();
                            editor.putString("email",email);
                            editor.putString("password",password);
                            editor.apply();


                            System.out.println("email: " + email + " ,password: " + password);
                        }else {
                            Toast.makeText(LoginActivity.this,"Sai thông tin đăng nhập",Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onFailure(Call <LoginUserDto> call, Throwable t) {
                        //Toast.makeText(UploadActivity.this,"Register FAILED! " + t.getMessage(),Toast.LENGTH_LONG).show();
                        Toast.makeText(LoginActivity.this,"Error",Toast.LENGTH_LONG).show();
                    }
                });



            }
        });


    }
}
