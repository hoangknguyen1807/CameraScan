package com.example.camerascan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
   private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_form);
        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtPassword = findViewById(R.id.edtPassword);




        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = edtEmail.getText().toString();
                password = edtPassword.getText().toString();
                Toast.makeText(LoginActivity.this, "email: " + email + " ,password: " + password, Toast.LENGTH_SHORT).show();

                // Set session after login
                SharedPreferences.Editor editor = getSharedPreferences("DeviceToken",MODE_PRIVATE).edit();
                editor.putString("email",email);
                editor.putString("password",password);
                editor.apply();


                System.out.println("email: " + email + " ,password: " + password);


            }
        });


    }
}
