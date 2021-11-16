package com.example.techsitepro.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.techsitepro.Adapters.PostAdapter;
import com.example.techsitepro.MessagesActivity;
import com.example.techsitepro.Models.Post;
import com.example.techsitepro.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {
    private ImageView myMessages;
    private RecyclerView recyclerViewPosts;
    private List<Post> allPosts;
    private PostAdapter postAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_home, container, false);
        myMessages=view.findViewById(R.id.myMessages);
        recyclerViewPosts=view.findViewById(R.id.recyclerViewPosts);
        recyclerViewPosts.setHasFixedSize(true);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        allPosts=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),allPosts,false);
        recyclerViewPosts.setAdapter(postAdapter);

        getAllPost();

        myMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),MessagesActivity.class));
            }
        });


        return view;
    }

    private void getAllPost() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allPosts.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Post post=snapshot.getValue(Post.class);
                    allPosts.add(post);
                }
                Collections.reverse(allPosts);
                postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}