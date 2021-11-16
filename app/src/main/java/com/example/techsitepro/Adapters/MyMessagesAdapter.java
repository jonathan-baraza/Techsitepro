package com.example.techsitepro.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techsitepro.ChatActivity;
import com.example.techsitepro.Models.Message;
import com.example.techsitepro.Models.User;
import com.example.techsitepro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyMessagesAdapter extends RecyclerView.Adapter<MyMessagesAdapter.ViewHolder>{
    private Context mContext;
    private List<Message> mMessages;
    private FirebaseUser fUser;

    public MyMessagesAdapter(Context mContext, List<Message> mMessages) {
        this.mContext = mContext;
        this.mMessages = mMessages;
        fUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_message_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyMessagesAdapter.ViewHolder holder, int position) {
        Message message=mMessages.get(position);
        if(!message.getIsphoto().equals("true")){
            holder.message.setText(message.getMessage());
            holder.message.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        }
        else{
            holder.message.setText("photo");
            int drawable=R.drawable.ic_camera;
            holder.message.setCompoundDrawablesWithIntrinsicBounds(drawable,0,0,0);
            holder.message.setCompoundDrawablePadding(5);
            holder.time.setText(message.getTime());
        }
        String userId="";
        if(message.getSenderid().equals(fUser.getUid())){
             userId= message.getRecipientid();
             holder.readStatus.setVisibility(View.VISIBLE);
             if(message.getDelivered().equals("true")){
                 holder.readStatus.setImageResource(R.drawable.ic_messge_read);
             }
             else{
                 holder.readStatus.setImageResource(R.drawable.ic_messge_sent);
             }
        }
        else{
             userId=message.getSenderid();
            holder.readStatus.setVisibility(View.GONE);
            if(!message.getDelivered().equals("true")){
                holder.message.setTextColor(Color.parseColor("#3800b3"));
            }
            else{
                holder.message.setTextColor(Color.parseColor("#747474"));
            }

        }
        setUpMessageUser(holder.imageProfile,holder.username,userId);

        String finalUserId = userId;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(mContext, ChatActivity.class);
            intent.putExtra("userId", finalUserId);
            mContext.startActivity(intent);
        }
    });

        setMessageCount(holder.messageCount,userId);

    }

    private void setMessageCount(TextView messageCount, String userId) {
        FirebaseDatabase.getInstance().getReference().child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Message message=snapshot.getValue(Message.class);
                    if(message.getSenderid().equals(userId) && message.getRecipientid().equals(fUser.getUid())){
                        if(message.getDelivered().equals("false")){
                            count++;
                        }
                    }
                }
                if(count>0){
                    messageCount.setText(count+"");
                }
                else{
                    messageCount.setVisibility(View.GONE);
                }
                //notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUpMessageUser(CircleImageView imageProfile, TextView username, String userId) {
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
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imageProfile;
        private TextView username,message,time,messageCount;
        private ImageView readStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile=itemView.findViewById(R.id.imageProfile);
            username=itemView.findViewById(R.id.username);
            message=itemView.findViewById(R.id.message);
            time=itemView.findViewById(R.id.time);
            messageCount=itemView.findViewById(R.id.messageCount);
            readStatus=itemView.findViewById(R.id.readStatus);

        }
    }
}
