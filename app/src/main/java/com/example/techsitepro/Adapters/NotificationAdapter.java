package com.example.techsitepro.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techsitepro.Models.Notification;
import com.example.techsitepro.Models.Post;
import com.example.techsitepro.Models.User;
import com.example.techsitepro.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    private Context mContext;
    private List<Notification> mNotifications;
    private FirebaseUser fUser;

    public NotificationAdapter(Context mContext, List<Notification> mNotifications) {
        this.mContext = mContext;
        this.mNotifications = mNotifications;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        Notification notification=mNotifications.get(position);
        holder.notification.setText(notification.getNotification());
        if(notification.getIspost().equals("true")){
            setPostItem(notification.getPostid(),holder.postImage);
        }
        setPublisherData(notification.getPublisherid(),holder.imageProfile,holder.username);



    }

    private void setPublisherData(String publisherid, CircleImageView imageProfile, TextView username) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                username.setText(user.getUsername());
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

    private void setPostItem(String postid,ImageView postImage) {
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post=snapshot.getValue(Post.class);
                postImage.setVisibility(View.VISIBLE);
                Picasso.get().load(post.getImageurl()).into(postImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imageProfile;
        private TextView username,notification;
        private ImageView postImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile=itemView.findViewById(R.id.imageProfile);
            username=itemView.findViewById(R.id.username);
            notification=itemView.findViewById(R.id.notification);
            postImage=itemView.findViewById(R.id.postImage);
        }
    }
}
