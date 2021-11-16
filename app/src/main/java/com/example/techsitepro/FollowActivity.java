package com.example.techsitepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.techsitepro.Adapters.UserAdapter;
import com.example.techsitepro.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowActivity extends AppCompatActivity {
    private TextView followers,following;
    private RecyclerView recyclerViewFollowers,recyclerViewFollowing;
    private String userId;
    private List<User> followersList,followingList;
    private List<String> followersIds,followingIds;
    private UserAdapter followersAdapter,followingAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        followers=findViewById(R.id.followers);
        following=findViewById(R.id.following);
        recyclerViewFollowers=findViewById(R.id.recyclerViewFollowers);
        recyclerViewFollowing=findViewById(R.id.recyclerViewFollowing);
        followersIds=new ArrayList<>();
        followingIds=new ArrayList<>();

        Intent intent=getIntent();
        userId=intent.getStringExtra("userId");



        recyclerViewFollowers.setHasFixedSize(true);
        recyclerViewFollowers.setLayoutManager(new LinearLayoutManager(this));
        followersList=new ArrayList<>();
        followersAdapter=new UserAdapter(this,followersList,false);
        recyclerViewFollowers.setAdapter(followersAdapter);

        recyclerViewFollowing.setHasFixedSize(true);
        recyclerViewFollowing.setLayoutManager(new LinearLayoutManager(this));
        followingList=new ArrayList<>();
        followingAdapter=new UserAdapter(this,followingList,false);
        recyclerViewFollowing.setAdapter(followingAdapter);

        getUser();
        getFollowersIds();
        getFollowingIds();

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Followers and Following");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String init=intent.getStringExtra("following");
        if(init.equals("true")){
            following.setTextColor(Color.parseColor("#000091"));
            recyclerViewFollowers.setVisibility(View.GONE);
            recyclerViewFollowing.setVisibility(View.VISIBLE);
        }
        else{
            followers.setTextColor(Color.parseColor("#000091"));
        }


        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                following.setTextColor(Color.parseColor("#000091"));
                followers.setTextColor(Color.parseColor("#919191"));
                recyclerViewFollowers.setVisibility(View.GONE);
                recyclerViewFollowing.setVisibility(View.VISIBLE);
            }
        });
        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followers.setTextColor(Color.parseColor("#000091"));
                following.setTextColor(Color.parseColor("#919191"));
                recyclerViewFollowers.setVisibility(View.VISIBLE);
                recyclerViewFollowing.setVisibility(View.GONE);
            }
        });
    }

    private void getFollowingIds() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int followingCount=0;
                followingIds.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    followingIds.add(snapshot.getKey());
                    followingCount++;
                }
                following.setText("Following ("+followingCount+")");
                getFollowing();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowing() {
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user=snapshot.getValue(User.class);
                    for(String id:followingIds){
                        if(user.getUserid().equals(id)){
                            followingList.add(user);
                        }
                    }
                }
                followingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowersIds() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int followersCount=0;
                followersIds.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    followersIds.add(snapshot.getKey());
                    followersCount++;
                }
                followers.setText("Followers ("+followersCount+")");
                getFollowers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followersList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user=snapshot.getValue(User.class);
                    for(String id:followersIds){
                        if(user.getUserid().equals(id)){
                            followersList.add(user);
                        }
                    }
                }
                followersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUser() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                getSupportActionBar().setTitle(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}