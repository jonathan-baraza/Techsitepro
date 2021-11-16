package com.example.techsitepro.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techsitepro.Models.Message;
import com.example.techsitepro.R;
import com.example.techsitepro.ViewImageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    private Context mContext;
    private List<Message> mMessages;
    private FirebaseUser fUser;

    public MessageAdapter(Context mContext, List<Message> mMessages) {
        this.mContext = mContext;
        this.mMessages = mMessages;
        fUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Message message=mMessages.get(position);
        if(message.getSenderid().equals(fUser.getUid())){
            //Auth user is sender
            holder.recipientView.setVisibility(View.GONE);
            holder.senderView.setVisibility(View.VISIBLE);
            if(message.getIsphoto().equals("true")){
                holder.senderMessage.setVisibility(View.GONE);
                holder.senderPhoto.setVisibility(View.VISIBLE);
                Picasso.get().load(message.getMessage()).into(holder.senderPhoto);

                holder.senderPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(mContext, ViewImageActivity.class);
                        intent.putExtra("id",message.getId());
                        intent.putExtra("type","message");
                        mContext.startActivity(intent);
                    }
                });

            }
            else{
                holder.senderPhoto.setVisibility(View.GONE);
                holder.senderMessage.setText(message.getMessage());
            }
            holder.senderTime.setText(message.getTime());
            if (message.getDelivered().equals("true")){
                holder.readStatus.setImageResource(R.drawable.ic_messge_read);
            }
        }
        else{
            //Auth user is recipient
            holder.senderView.setVisibility(View.GONE);
            holder.recipientView.setVisibility(View.VISIBLE);
            if(message.getIsphoto().equals("true")){
                holder.recipientMessage.setVisibility(View.GONE);
                holder.recipientPhoto.setVisibility(View.VISIBLE);
                Picasso.get().load(message.getMessage()).into(holder.recipientPhoto);
                holder.recipientPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(mContext, ViewImageActivity.class);
                        intent.putExtra("id",message.getId());
                        intent.putExtra("type","message");
                        mContext.startActivity(intent);
                    }
                });
            }
            else{
                holder.recipientPhoto.setVisibility(View.GONE);
                holder.recipientMessage.setText(message.getMessage());
            }
            holder.recipientTime.setText(message.getTime());

        }

    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout senderView,recipientView;
        private ImageView recipientPhoto,senderPhoto,readStatus;
        private TextView senderMessage,recipientMessage,recipientTime,senderTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderView=itemView.findViewById(R.id.senderView);
            recipientView=itemView.findViewById(R.id.recipientView);
            senderMessage=itemView.findViewById(R.id.senderMessage);
            recipientMessage=itemView.findViewById(R.id.recipientMessage);
            recipientTime=itemView.findViewById(R.id.recipientTime);
            senderTime=itemView.findViewById(R.id.senderTime);
            recipientPhoto=itemView.findViewById(R.id.recipientPhoto);
            senderPhoto=itemView.findViewById(R.id.senderPhoto);
            readStatus=itemView.findViewById(R.id.readStatus);


        }
    }
}
