package com.example.techsitepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.techsitepro.Adapters.PostAdapter;
import com.example.techsitepro.Models.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerViewPostDetail;
    private String postId;
    private List<Post> myOnePost;
    private PostAdapter postAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Post detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent=getIntent();
        postId=intent.getStringExtra("postId");

        recyclerViewPostDetail=findViewById(R.id.recyclerViewPostDetail);
        recyclerViewPostDetail.setHasFixedSize(true);
        recyclerViewPostDetail.setLayoutManager(new LinearLayoutManager(this));
        myOnePost=new ArrayList<>();
        postAdapter=new PostAdapter(this,myOnePost,false);
        recyclerViewPostDetail.setAdapter(postAdapter);

        getThePost();


    }

    private void getThePost() {
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myOnePost.clear();
                Post post=snapshot.getValue(Post.class);
                myOnePost.add(post);
                postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}