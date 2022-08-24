package com.example.chitchat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chitchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private TextView tv_signup;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.loginEmali);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton  = findViewById(R.id.loginButton);
        tv_signup = findViewById(R.id.tv_signup);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this,signUp.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();

                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(login.this, "Enter valid email and password", Toast.LENGTH_SHORT).show();
                }else if (!email.matches(emailPattern)){
                    loginEmail.setError("Invalid Email");
                    Toast.makeText(login.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }else if (password.length()<6){
                    Toast.makeText(login.this, "Password length should atleast 6", Toast.LENGTH_SHORT).show();
                }else {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(login.this, HomeActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(login.this, "Error in login", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                progressDialog.dismiss();
            }
        });
    }
}