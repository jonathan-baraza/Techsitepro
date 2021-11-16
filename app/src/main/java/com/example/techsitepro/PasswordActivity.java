package com.example.techsitepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PasswordActivity extends AppCompatActivity {
    private EditText password;
    private Button call,submit;
    private String correctPassword;
    private TextView exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        getCorrectPassword();

        password=findViewById(R.id.password);
        call=findViewById(R.id.call);
        submit=findViewById(R.id.submit);
        exit=findViewById(R.id.exit);
        
        
        
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password_txt=password.getText().toString();
                if(TextUtils.isEmpty(password_txt)){
                    Toast.makeText(PasswordActivity.this, "You must enter site password.", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(password_txt.equals(correctPassword)){
                        Toast.makeText(PasswordActivity.this, "Welcome to Techsite pro.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PasswordActivity.this,StartActivity.class));
                    }
                    else{
                        Toast.makeText(PasswordActivity.this, "Password incorrect, kinldy contact the app admin.", Toast.LENGTH_SHORT).show();
                        
                    }
                    password.setText("");
                }
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+254704783187"));
                startActivity(intent);
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PasswordActivity.this, "Good bye!", Toast.LENGTH_SHORT).show();
                finishAffinity();
            }
        });

    }

    private void getCorrectPassword() {
        FirebaseDatabase.getInstance().getReference().child("Password").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                correctPassword=snapshot.child("password").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}