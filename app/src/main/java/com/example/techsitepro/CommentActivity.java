package com.example.techsitepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.techsitepro.Adapters.CommentAdapter;
import com.example.techsitepro.Models.Comment;
import com.example.techsitepro.Models.Post;
import com.example.techsitepro.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {
    private CircleImageView imageProfile;
    private EditText comment;
    private ImageView sendComment;
    private String postId;
    private FirebaseUser fUser;
    private RecyclerView recyclerViewComment;
    private List<Comment> allComments;
    private CommentAdapter commentAdapter;
    private Post post;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        imageProfile=findViewById(R.id.imageProfile);
        comment=findViewById(R.id.comment);
        sendComment=findViewById(R.id.sendComment);

        fUser= FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        Intent intent=getIntent();
        postId=intent.getStringExtra("postId");

        recyclerViewComment=findViewById(R.id.recyclerViewComments);
        recyclerViewComment.setHasFixedSize(true);
        recyclerViewComment.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
        allComments=new ArrayList<>();
        commentAdapter=new CommentAdapter(this,allComments,postId);
        recyclerViewComment.setAdapter(commentAdapter);

        readAllComments();

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment_txt=comment.getText().toString();
                if(!TextUtils.isEmpty(comment_txt)){
                    uploadComment(comment_txt);

                }
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if(user.getProfileimage().equals("default")){
                    imageProfile.setImageResource(R.drawable.avatar);
                }
                else{
                    Picasso.get().load(user.getProfileimage()).into(imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void readAllComments() {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allComments.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Comment comment=snapshot.getValue(Comment.class);
                    allComments.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadComment(String comment_txt) {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        String id=ref.push().getKey();
        HashMap<String,Object> map=new HashMap<>();
        map.put("id",id);
        map.put("comment",comment_txt);
        map.put("publisherid",fUser.getUid());

        ref.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    comment.setText("");
                    Toast.makeText(CommentActivity.this, "Comment added", Toast.LENGTH_SHORT).show();
                    addNotification();
                }
            }
        });
    }

    private void addNotification() {
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post=snapshot.getValue(Post.class);
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Notifications").child(post.getPublisherid());
                String key=ref.push().getKey();
                HashMap<String,Object> map=new HashMap<>();
                map.put("id",key);
                map.put("notification","Commented on your post");
                map.put("postid",postId);
                map.put("publisherid",fUser.getUid());
                map.put("ispost","true");

                if(!fUser.getUid().equals(post.getPublisherid())){
                    ref.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                System.out.println(task.getException().getMessage());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}