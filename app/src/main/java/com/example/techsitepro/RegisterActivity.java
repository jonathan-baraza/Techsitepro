package com.example.techsitepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText username,fullname,email,password,password2;
    private Button register;
    private TextView goToLogin;
    private AlertDialog.Builder builder;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;

    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username=findViewById(R.id.username);
        fullname=findViewById(R.id.fullname);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        password2=findViewById(R.id.password2);
        
        register=findViewById(R.id.register);
        goToLogin=findViewById(R.id.goToLogin);

        mAuth=FirebaseAuth.getInstance();
        ref= FirebaseDatabase.getInstance().getReference();

        pd=new ProgressDialog(this);
        pd.setMessage("Creating your account");
        pd.setCanceledOnTouchOutside(false);


        builder=new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setPositiveButton("Got it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Dismiss the alert dialog.
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username_txt=username.getText().toString();
                String fullname_txt=fullname.getText().toString();
                String email_txt=email.getText().toString();
                String password_txt=password.getText().toString();
                String password2_txt=password2.getText().toString();
                
                if(TextUtils.isEmpty(username_txt)||TextUtils.isEmpty(fullname_txt)||TextUtils.isEmpty(email_txt)||TextUtils.isEmpty(password_txt)||TextUtils.isEmpty(password2_txt)){
                    throwError("Credentials error","Ensure you have filled all the fields");
                }else{
                    if(password_txt.length()<6){
                        throwError("Password Length Error","Your password is too short, try a longer password.");
                    }
                    else{
                        if(!password_txt.equals(password2_txt)){
                            throwError("Password Error","Warning, the two passwords don't match.");
                        }
                        else{
                            registerUser(username_txt,fullname_txt,email_txt,password_txt);
                        }
                    }
                }
            }
        });
    }

    private void registerUser(String username, String fullname, String email, String password) {
        pd.show();
        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String,Object> map=new HashMap<>();
                map.put("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                map.put("username",username);
                map.put("fullname",fullname);
                map.put("email",email);
                map.put("bio","Update your bio");
                map.put("profileimage","default");
                ref.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "Update your profile for the best user experience.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                            finish();
                        }
                        else{
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "Registration failed! Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void throwError(String title, String message) {
        builder.setTitle(title);
        builder.setMessage(message);
        builder.create();
        builder.show();
    }
}