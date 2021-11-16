package com.example.techsitepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText email,password;
    private Button login;
    private TextView goToRegister;
    private AlertDialog.Builder builder;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);
        goToRegister=findViewById(R.id.goToRegister);

        mAuth=FirebaseAuth.getInstance();

        builder=new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setPositiveButton("Got it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Dismiss the alert dialog.
            }
        });

        goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_txt=email.getText().toString();
                String password_txt=password.getText().toString();

                if(TextUtils.isEmpty(email_txt)||TextUtils.isEmpty(password_txt)){
                    throwError("Credentials Error","You must enter both your email and password");
                }
                else{
                    signInUser(email_txt,password_txt);
                }
            }
        });
    }

    private void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Login successfull", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                }
                else{
                    throwError("Login Error","Wrong Email or Password!");
                    resetPage();
                }
            }
        });
    }

    private void resetPage() {
        email.setText("");
        password.setText("");
    }

    private void throwError(String title, String message) {
        builder.setTitle(title);
        builder.setMessage(message);
        builder.create();
        builder.show();
    }
}