package com.example.techsitepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.techsitepro.Models.Message;
import com.example.techsitepro.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewImageActivity extends AppCompatActivity {
    private ImageView back,image;
    private String id,type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        back=findViewById(R.id.back);
        image=findViewById(R.id.image);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        type=intent.getStringExtra("type");

        if(type.equals("message")){
            FirebaseDatabase.getInstance().getReference().child("Messages").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Message message=snapshot.getValue(Message.class);
                    Picasso.get().load(message.getMessage()).into(image);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            FirebaseDatabase.getInstance().getReference().child("Users").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user=snapshot.getValue(User.class);
                    if(user.getProfileimage().equals("default")){
                        image.setImageResource(R.drawable.avatar);
                    }
                    else{
                        Picasso.get().load(user.getProfileimage()).into(image);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}