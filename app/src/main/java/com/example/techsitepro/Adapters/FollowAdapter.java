package com.example.techsitepro.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techsitepro.Fragments.ProfileFragment;
import com.example.techsitepro.Models.User;
import com.example.techsitepro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder>{
    private Context mContext;
    private List<User> mFollowers;
    private FirebaseUser fUser;
    private boolean isFragment;

    public FollowAdapter(Context mContext, List<User> mFollowers,boolean isFragment) {
        this.mContext = mContext;
        this.mFollowers = mFollowers;
        this.isFragment=isFragment;
        fUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.follower_profile_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FollowAdapter.ViewHolder holder, int position) {
        User user=mFollowers.get(position);
        if(user.getProfileimage().equals("default")){
            holder.imageProfile.setImageResource(R.drawable.avatar);
        }
        else{
            Picasso.get().load(user.getProfileimage()).into(holder.imageProfile);
        }
        holder.username.setText(user.getUsername());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.getSharedPreferences("profile",Context.MODE_PRIVATE).edit().putString("profileId",user.getUserid()).apply();
                mContext.getSharedPreferences("profile2",Context.MODE_PRIVATE).edit().putBoolean("isbottom",false).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFollowers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imageProfile;
        private TextView username;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile=itemView.findViewById(R.id.imageProfile);
            username=itemView.findViewById(R.id.username);
        }
    }
}
