package com.example.chitchat.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chitchat.R;
import com.example.chitchat.ModelClass.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class signUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;

    private EditText signupName,signupEmail,signupPassword,signupConfirmPassword;
    private Button signupButton;
    private ImageView profileImage;

    private final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Uri imageUri;
    String imageURI;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        TextView tv_login = findViewById(R.id.tv_login);
        signupName = findViewById(R.id.signupName);
        signupEmail = findViewById(R.id.signupEmali);
        signupPassword = findViewById(R.id.signupPassword);
        signupConfirmPassword = findViewById(R.id.signupConfirmPassword);
        signupButton  = findViewById(R.id.signupButton);
        profileImage = findViewById(R.id.profile_image);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signUp.this,login.class);
                startActivity(intent);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);
            }
        });


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();

                String name = signupName.getText().toString();
                String email = signupEmail.getText().toString();
                String password = signupPassword.getText().toString();
                String confirmPassword = signupConfirmPassword.getText().toString();
                String status = "Hey! there i am using  chitChat";


                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(signUp.this, "Enter valid email and password", Toast.LENGTH_SHORT).show();
                }else if (!email.matches(emailPattern)){
                    signupEmail.setError("Invalid Email");
                    Toast.makeText(signUp.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }else if (password.length()<6){
                    Toast.makeText(signUp.this, "Password length should atleast 6", Toast.LENGTH_SHORT).show();
                }else if (!password.equals(confirmPassword)){
                    Toast.makeText(signUp.this, "password didn't match", Toast.LENGTH_SHORT).show();
                }else {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                DatabaseReference databaseReference = database.getReference().child("user").child(mAuth.getUid());
                                StorageReference storageReference = storage.getReference().child("upload").child(mAuth.getUid());

                                if (imageUri != null){
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageURI = uri.toString();
                                                        User user = new User(mAuth.getUid(),name,email,imageURI,status);
                                                        databaseReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    Intent intent = new Intent(signUp.this, HomeActivity.class);
                                                                    startActivity(intent);
                                                                }else {
                                                                    Toast.makeText(signUp.this, "Error in Creating a new User", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else{
                                    imageURI = "https://firebasestorage.googleapis.com/v0/b/chitchat-7b5da.appspot.com/o/profile.png?alt=media&token=95062bf2-5a6b-40c4-894a-e49bf7a0d308";
                                    User user = new User(mAuth.getUid(),name,email,imageURI,status);
                                    databaseReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Intent intent = new Intent(signUp.this, HomeActivity.class);
                                                startActivity(intent);
                                            }else {
                                                Toast.makeText(signUp.this, "Error in Creating a new User", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }else{
                                Toast.makeText(signUp.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                progressDialog.dismiss();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==10 && resultCode == RESULT_OK){
            if (data!=null){
                imageUri = data.getData();
                profileImage.setImageURI(imageUri);;
            }
        }
    }
}