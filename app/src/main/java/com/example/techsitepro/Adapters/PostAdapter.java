package com.example.techsitepro.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techsitepro.CommentActivity;
import com.example.techsitepro.Models.Post;
import com.example.techsitepro.Models.User;
import com.example.techsitepro.PostDetailActivity;
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
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    private Context mContext;
    private List<Post> mPosts;
    private FirebaseUser fUser;
    private boolean isProfile;

    public PostAdapter(Context mContext, List<Post> mPosts,boolean isProfile) {
        this.mContext = mContext;
        this.mPosts = mPosts;
        this.isProfile=isProfile;
        fUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(isProfile){
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_post_item,parent,false);
            return new PostAdapter.ViewHolder(view);
        }
        else{
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,parent,false);
            return new PostAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        Post post=mPosts.get(position);
        if(isProfile){
            Picasso.get().load(post.getImageurl()).placeholder(R.drawable.avatar).into(holder.postImage);
        }else {


            setPublisherData(holder.username, holder.fullname, holder.imageProfile, post.getPublisherid());
            Picasso.get().load(post.getImageurl()).placeholder(R.drawable.picholder).into(holder.postImage);
            holder.description.setText(post.getDescription());

            checkIfLiked(holder.like, post.getId());
            getLikeCount(post.getId(), holder.numOfLikes);
            getCommentCount(post.getId(), holder.numOfComments);
            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.like.getTag() == "liked") {

                        FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getId()).child(fUser.getUid()).removeValue();
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getId()).child(fUser.getUid()).setValue(true);
                        addNotification(post.getId(), post.getPublisherid());
                    }


                }
            });

            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, CommentActivity.class);
                    intent.putExtra("postId", post.getId());
                    mContext.startActivity(intent);
                }
            });

            holder.numOfComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.comment.performClick();
                }
            });

            //TODO: Set more onclick functionality.

            holder.postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, PostDetailActivity.class);
                    intent.putExtra("postId", post.getId());
                    mContext.startActivity(intent);
                }
            });

            holder.postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, PostDetailActivity.class);
                    intent.putExtra("postId", post.getId());
                    mContext.startActivity(intent);
                }
            });

            holder.imageProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(mContext, ViewImageActivity.class);
                    intent.putExtra("id",post.getPublisherid());
                    intent.putExtra("type","ppic");
                    mContext.startActivity(intent);
                }
            });


        }



    }

    private void addNotification(String postid, String publisherid) {
        if(!postid.equals(publisherid)){
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Notifications").child(publisherid);
            String key=ref.push().getKey();
            HashMap<String,Object> map=new HashMap<>();
            map.put("id",key);
            map.put("notification","Liked your post");
            map.put("postid",postid);
            map.put("publisherid",fUser.getUid());
            map.put("ispost","true");

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

    private void getCommentCount(String id, TextView numOfComments) {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int commentCount=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    commentCount++;
                }
                if(commentCount==0){
                    numOfComments.setText("No comments yet");
                }
                else if(commentCount==1){
                    numOfComments.setText("View "+commentCount+" comment");
                }else{
                    numOfComments.setText("View all "+commentCount+" comments");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLikeCount(String id, TextView numOfLikes) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int likeCount=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    likeCount++;
                }
                if(likeCount==1){
                    numOfLikes.setText(likeCount+" like");
                }else{
                    numOfLikes.setText(likeCount+" likes");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void checkIfLiked(ImageView btnLike, String postId) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(fUser.getUid()).exists()){
                    btnLike.setImageResource(R.drawable.ic_liked);
                    btnLike.setTag("liked");
                }
                else{
                    btnLike.setImageResource(R.drawable.ic_like);
                    btnLike.setTag("unliked");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setPublisherData(TextView username, TextView fullname, CircleImageView imageProfile, String publisherid) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if(user.getProfileimage().equals("default")){
                    imageProfile.setImageResource(R.drawable.avatar);
                }
                else{
                    Picasso.get().load(user.getProfileimage()).placeholder(R.drawable.avatar).into(imageProfile);
                }
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imageProfile;
        private TextView username,numOfLikes,fullname,numOfComments;
        private SocialTextView description;
        private ImageView more,postImage,like,comment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile=itemView.findViewById(R.id.imageProfile);
            username=itemView.findViewById(R.id.username);
            numOfLikes=itemView.findViewById(R.id.numOfLikes);
            fullname=itemView.findViewById(R.id.fullname);
            numOfComments=itemView.findViewById(R.id.numOfComments);
            description=itemView.findViewById(R.id.description);
            more=itemView.findViewById(R.id.more);
            postImage=itemView.findViewById(R.id.postImage);
            like=itemView.findViewById(R.id.like);
            comment=itemView.findViewById(R.id.comment);

        }
    }
}
