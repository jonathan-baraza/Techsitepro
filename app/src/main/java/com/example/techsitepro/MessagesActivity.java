package com.example.techsitepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.techsitepro.Adapters.MyMessagesAdapter;
import com.example.techsitepro.Models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MessagesActivity extends AppCompatActivity {
    private RecyclerView recyclerViewMessages;
    private List<Message> myMessages;
    private List<Message> allMessages;
    private MyMessagesAdapter myMessagesAdapter;
    private List<String> ids;


    private FirebaseUser fUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        fUser= FirebaseAuth.getInstance().getCurrentUser();
        allMessages=new ArrayList<>();
        ids=new ArrayList<>();

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My messages");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerViewMessages=findViewById(R.id.recyclerViewMessages);
        recyclerViewMessages.setHasFixedSize(true);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        myMessages=new ArrayList<>();
        myMessagesAdapter=new MyMessagesAdapter(this,myMessages);
        recyclerViewMessages.setAdapter(myMessagesAdapter);

        getMyMessages();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessagesActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });
    }

    private void getMyMessages() {
        FirebaseDatabase.getInstance().getReference().child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allMessages.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Message message=snapshot.getValue(Message.class);
                    if(message.getSenderid().equals(fUser.getUid())||message.getRecipientid().equals(fUser.getUid())){
                        allMessages.add(message);
                    }
                }
                getExactMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getExactMessages() {
        ids.clear();
        myMessages.clear();
        Collections.reverse(allMessages);
        for(Message message:allMessages){
            String comboOne=message.getSenderid()+message.getRecipientid();
            String comboTwo=message.getRecipientid()+message.getSenderid();

            if(!(ids.contains(comboOne) || ids.contains(comboTwo))) {
                myMessages.add(message);
                ids.add(comboOne);
                ids.add(comboTwo);
            }

        }
        myMessagesAdapter.notifyDataSetChanged();
    }
}