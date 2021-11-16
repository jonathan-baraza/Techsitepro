package com.example.techsitepro.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.techsitepro.Adapters.NotificationAdapter;
import com.example.techsitepro.Models.Notification;
import com.example.techsitepro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationFragment extends Fragment {
    private RecyclerView recyclerViewNotifications;
    private List<Notification> notificationList;
    private NotificationAdapter notificationAdapter;
    private FirebaseUser fUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_notification, container, false);
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        recyclerViewNotifications=view.findViewById(R.id.recyclerViewNotifications);
        recyclerViewNotifications.setHasFixedSize(true);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationList =new ArrayList<>();
        notificationAdapter=new NotificationAdapter(getContext(), notificationList);
        recyclerViewNotifications.setAdapter(notificationAdapter);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Notification notification=snapshot.getValue(Notification.class);
                    notificationList.add(notification);
                }
                Collections.reverse(notificationList);
                notificationAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }
}