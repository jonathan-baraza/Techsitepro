package com.example.techsitepro.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techsitepro.Fragments.HomeFragment;
import com.example.techsitepro.Fragments.ProfileFragment;
import com.example.techsitepro.MainActivity;
import com.example.techsitepro.Models.User;
import com.example.techsitepro.R;
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

import java.util.HashMap;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    private Context mContext;
    private List<User> mUsers;
    private FirebaseUser fUser;
    private boolean isFragment;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
        fUser=FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user=mUsers.get(position);

        if(user.getProfileimage().equals("default")){
            holder.imageProfile.setImageResource(R.mipmap.ic_launcher);
        }
        else{
            Picasso.get().load(user.getProfileimage()).into(holder.imageProfile);
        }
        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getFullname());

        checkFollowingStatus(user.getUserid(),holder.follow);

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(user.getFullname());
                String text=holder.follow.getText().toString();
                if(text.equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").child(user.getUserid()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUserid()).child("followers").child(fUser.getUid()).setValue(true);
                    addNotification(user.getUserid());
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").child(user.getUserid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUserid()).child("followers").child(fUser.getUid()).removeValue();
                }
            }
        });

        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFragment){
                    mContext.getSharedPreferences("profile",Context.MODE_PRIVATE).edit().putString("profileId",user.getUserid()).apply();
                    mContext.getSharedPreferences("profile2",Context.MODE_PRIVATE).edit().putBoolean("isbottom",false).apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                }
                else{
                    Intent intent=new Intent(mContext, MainActivity.class);
                    intent.putExtra("profileId",user.getUserid());
                    intent.putExtra("isbottom",false);
                    mContext.startActivity(intent);

                }
            }
        });

        holder.fullname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.imageProfile.performClick();
            }
        });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.imageProfile.performClick();
            }
        });
    }

    private void addNotification(String userid) {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Notifications").child(userid);
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
                    Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkFollowingStatus(String userid, Button button) {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(userid.equals(fUser.getUid())){
                    button.setVisibility(View.GONE);
                }else{
                    if(dataSnapshot.child(userid).exists()){
                        button.setVisibility(View.VISIBLE);
                        button.setText("unfollow");
                    }
                    else{
                        button.setVisibility(View.VISIBLE);
                        button.setText("follow");
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageProfile;
        private TextView username,fullname;
        private Button follow;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile=itemView.findViewById(R.id.imageProfile);
            username=itemView.findViewById(R.id.username);
            fullname=itemView.findViewById(R.id.fullname);
            follow=itemView.findViewById(R.id.follow);

        }
    }
}
