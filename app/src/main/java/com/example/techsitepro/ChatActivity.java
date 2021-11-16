package com.example.techsitepro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techsitepro.Adapters.MessageAdapter;
import com.example.techsitepro.Models.Message;
import com.example.techsitepro.Models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private ImageView back;
    private CircleImageView imageProfile;
    private TextView username;
    private EditText message;
    private ImageButton send,sendHolder,sendPhoto;

    private FirebaseUser fUser;
    private String userId;

    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> myMessages;

    private Uri imageUri;
    private String imageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        fUser= FirebaseAuth.getInstance().getCurrentUser();

        back=findViewById(R.id.back);
        imageProfile=findViewById(R.id.imageProfile);
        username=findViewById(R.id.username);
        message=findViewById(R.id.message);
        sendHolder=findViewById(R.id.sendHolder);
        send=findViewById(R.id.send);
        sendPhoto=findViewById(R.id.sendPhoto);

        Intent intent=getIntent();
        userId=intent.getStringExtra("userId");


        recyclerViewMessages=findViewById(R.id.recyclerViewMessages);
        recyclerViewMessages.setHasFixedSize(true);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        myMessages=new ArrayList<>();
        messageAdapter=new MessageAdapter(this,myMessages);
        recyclerViewMessages.setAdapter(messageAdapter);
        ((LinearLayoutManager)recyclerViewMessages.getLayoutManager()).setStackFromEnd(true);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatActivity.this,MessagesActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });

        setRecepientData();
        getMessages();


        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkIfMessageEmpty(message.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkIfMessageEmpty(message.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkIfMessageEmpty(message.getText().toString());
                recyclerViewMessages.scrollToPosition(myMessages.size() - 1);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(message.getText().toString(),"false");
            }
        });

        sendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().start(ChatActivity.this);
            }
        });



    }

    private void getMessages() {
        FirebaseDatabase.getInstance().getReference().child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myMessages.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Message message=snapshot.getValue(Message.class);
                    if((message.getSenderid().equals(fUser.getUid()) && message.getRecipientid().equals(userId))||message.getSenderid().equals(userId) && message.getRecipientid().equals(fUser.getUid())){
                        myMessages.add(message);
                    }
                }
                messageAdapter.notifyDataSetChanged();
                recyclerViewMessages.scrollToPosition(myMessages.size() - 1);
                updateDelivered();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateDelivered() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Messages");
        Map<String,Object> map=new HashMap<>();
        map.put("delivered","true");
        for(Message message:myMessages){
            if(message.getRecipientid().equals(fUser.getUid())){
                ref.child(message.getId()).updateChildren(map);
            }
        }
    }

    private void sendMessage(String message_txt,String isphoto) {
        DateFormat df=new SimpleDateFormat("h:mm a");
        String time=df.format(Calendar.getInstance().getTime());


        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Messages");
        String key=ref.push().getKey();
        HashMap<String,Object> map=new HashMap<>();
        map.put("id",key);
        map.put("senderid",fUser.getUid());
        map.put("recipientid",userId);
        map.put("message",message_txt);
        map.put("time",time);
        map.put("isphoto",isphoto);
        map.put("delivered","false");

        ref.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    message.setText("");
                    recyclerViewMessages.scrollToPosition(myMessages.size() - 1);
                }
            }
        });
    }

    private void checkIfMessageEmpty(String message) {
        if(message.length()>0){
            sendHolder.setVisibility(View.GONE);
            send.setVisibility(View.VISIBLE);
        }
        else{
           send.setVisibility(View.GONE);
           sendHolder.setVisibility(View.VISIBLE);
        }
    }

    private void setRecepientData() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if(user.getProfileimage().equals("default")){
                    imageProfile.setImageResource(R.drawable.avatar);
                }
                else{
                    Picasso.get().load(user.getProfileimage()).into(imageProfile);
                }
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();
            sendPhotoMessage();
        }
    }

    private void sendPhotoMessage() {
        StorageReference fileRef= FirebaseStorage.getInstance().getReference().child("photoMessages").child(System.currentTimeMillis()+getFileExtension(imageUri));
        StorageTask uploadTask=fileRef.putFile(imageUri);
        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri data=task.getResult();
                    imageUrl=data.toString();
                    sendMessage(imageUrl,"true");
                }

            }
        });
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        String ext=mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
        return ext;
    }

    @Override
    public void onBackPressed() {
        back.performClick();
    }
}