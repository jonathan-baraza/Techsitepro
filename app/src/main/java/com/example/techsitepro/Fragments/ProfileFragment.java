package com.example.techsitepro.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techsitepro.Adapters.FollowAdapter;
import com.example.techsitepro.Adapters.PostAdapter;
import com.example.techsitepro.Adapters.UserAdapter;
import com.example.techsitepro.ChatActivity;
import com.example.techsitepro.EditProfileActivity;
import com.example.techsitepro.FollowActivity;
import com.example.techsitepro.Models.Post;
import com.example.techsitepro.Models.User;
import com.example.techsitepro.OptionsActivity;
import com.example.techsitepro.R;
import com.example.techsitepro.ViewImageActivity;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment {
    private TextView usernameTitle,username,fullname,email,bio,followers,following,posts,viewAllFollowers;
    private ImageView options,imageProfile;
    private ImageButton sendMessage;
    private Button follow;
    private RecyclerView recyclerViewFollowers,recyclerViewPosts;
    String profileId;
    private FirebaseUser fUser;
    private PostAdapter postAdapter;
    private List<Post> myPosts;
    private FollowAdapter followAdapter;
    private List<User> followersList;
    private List<String> followersIds;
    private LinearLayout followersLayout,followingLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        usernameTitle=view.findViewById(R.id.usernameTitle);
        options=view.findViewById(R.id.options);
        username=view.findViewById(R.id.username);
        fullname=view.findViewById(R.id.fullname);
        email=view.findViewById(R.id.email);
        bio=view.findViewById(R.id.bio);
        followers=view.findViewById(R.id.followers);
        following=view.findViewById(R.id.following);
        posts=view.findViewById(R.id.posts);
        viewAllFollowers=view.findViewById(R.id.viewAllFollowers);
        imageProfile=view.findViewById(R.id.imageProfile);
        sendMessage=view.findViewById(R.id.sendMessage);
        follow=view.findViewById(R.id.follow);

        recyclerViewFollowers=view.findViewById(R.id.recyclerViewFollowers);
        recyclerViewPosts=view.findViewById(R.id.recyclerViewPosts);
        followersIds=new ArrayList<>();

        followersLayout=view.findViewById(R.id.followersLayout);
        followingLayout=view.findViewById(R.id.followingLayout);

        fUser=FirebaseAuth.getInstance().getCurrentUser();


        String userData=getContext().getSharedPreferences("profile", Context.MODE_PRIVATE).getString("profileId","null");
        boolean isbottom=getContext().getSharedPreferences("profile2", Context.MODE_PRIVATE).getBoolean("isbottom",false);

        System.out.println("is bottom ni hii apa "+isbottom);
        System.out.println("ile id tumepata ni "+userData);
        if(userData.equals("null") && isbottom==true){
            profileId=fUser.getUid();
            sendMessage.setVisibility(View.GONE);
        }
        else{
            profileId=userData;

            sendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("userId",profileId);
                    startActivity(intent);
                }
            });

        }




        getUserData();
        setFollowButton(profileId,follow);


        recyclerViewPosts.setHasFixedSize(true);
        recyclerViewPosts.setLayoutManager(new GridLayoutManager(getContext(),3));
        myPosts=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),myPosts,true);
        recyclerViewPosts.setAdapter(postAdapter);
        getMyPosts();

        recyclerViewFollowers.setHasFixedSize(true);
        LinearLayoutManager layout=new LinearLayoutManager(getContext());
        layout.setOrientation(RecyclerView.HORIZONTAL);
        recyclerViewFollowers.setLayoutManager(layout);
        followersList=new ArrayList<>();
        followAdapter=new FollowAdapter(getContext(),followersList,true);
        recyclerViewFollowers.setAdapter(followAdapter);

        getFollowersIds();
        getFollowingIds();


        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), ViewImageActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("type","ppic");
                startActivity(intent);
            }
        });




        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=follow.getText().toString();
                switch (text){
                    case "follow":
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").child(profileId).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers").child(fUser.getUid()).setValue(true);
                        addNotification();
                        break;
                    case "unfollow":
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").child(profileId).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers").child(fUser.getUid()).removeValue();

                        break;
                    case "Edit Profile":
                        startActivity(new Intent(getContext(), EditProfileActivity.class));

                }
            }
        });


        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),OptionsActivity.class));
            }
        });

        viewAllFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), FollowActivity.class);
                intent.putExtra("userId",profileId);
                intent.putExtra("following","false");
                startActivity(intent);
            }
        });

        followersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewAllFollowers.performClick();
            }
        });

        followingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), FollowActivity.class);
                intent.putExtra("userId",profileId);
                intent.putExtra("following","true");

                startActivity(intent);
            }
        });

        return view;
    }

    private void getFollowingIds() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int followingCount=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    followingCount++;
                }
                following.setText(followingCount+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowersIds() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int followersCount=0;
                followersIds.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                   followersIds.add(snapshot.getKey());
                   followersCount++;
                }
                getFollowersList();
                followers.setText(followersCount+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowersList() {
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
                Collections.reverse(followersList);

                followAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMyPosts() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Post post=snapshot.getValue(Post.class);
                    if(post.getPublisherid().equals(profileId)){
                        myPosts.add(post);
                    }
                }
                Collections.reverse(myPosts);
                postAdapter.notifyDataSetChanged();
                int postCount=myPosts.size();
                posts.setText(postCount+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addNotification() {
        if(!fUser.getUid().equals(profileId)){
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Notifications").child(profileId);
            String key=ref.push().getKey();
            HashMap<String,Object> map=new HashMap<>();
            map.put("id",key);
            map.put("notification","Started following you");
            map.put("postid",null);
            map.put("publisherid",fUser.getUid());
            map.put("ispost","false");

            ref.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void setFollowButton(String profileId, Button button) {
        if(profileId.equals(fUser.getUid())){
            button.setText("Edit Profile");
            button.setBackgroundColor(Color.YELLOW);
            button.setTextColor(Color.BLACK);
        }
        else{
            FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(profileId).exists()){
                        button.setText("unfollow");
                    }
                    else{
                        button.setText("follow");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void getUserData() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                if(user.getProfileimage().equals("default")){
                    imageProfile.setImageResource(R.drawable.avatar);
                }
                else{
                    Picasso.get().load(user.getProfileimage()).into(imageProfile);
                }
                usernameTitle.setText(user.getUsername());
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                email.setText(user.getEmail());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}