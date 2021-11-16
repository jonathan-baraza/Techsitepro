package com.example.techsitepro.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techsitepro.Models.Comment;
import com.example.techsitepro.Models.User;
import com.example.techsitepro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    private Context mContext;
    private List<Comment> mComments;
    private FirebaseUser fUser;
    private String postId;
    private AlertDialog.Builder builder;

    public CommentAdapter(Context mContext, List<Comment> mComments,String postId) {
        this.mContext = mContext;
        this.mComments = mComments;
        this.postId = postId;
        fUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        Comment comment=mComments.get(position);
        holder.comment.setText(comment.getComment());
        setPulisherDetails(comment.getPublisherid(),holder.imageProfile,holder.username);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(comment.getPublisherid().equals(fUser.getUid())){
                    builder=new AlertDialog.Builder(mContext);
                    builder.setCancelable(false);
                    builder.setMessage("Do you want to delete this comment?");
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //dismiss
                        }
                    });
                    builder.setPositiveButton("Yes, delete!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteComment(comment.getId());
                        }
                    });
                    builder.create();
                    builder.show();
                }
                return true;
            }
        });
    }

    private void deleteComment(String id) {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(mContext, "Comment deleted", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(mContext, "Failed to delete comment", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setPulisherDetails(String publisherid, CircleImageView imageProfile, TextView username) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid).addValueEventListener(new ValueEventListener() {
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
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imageProfile;
        private TextView username,comment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile=itemView.findViewById(R.id.imageProfile);
            username=itemView.findViewById(R.id.username);
            comment=itemView.findViewById(R.id.comment);
        }
    }
}
