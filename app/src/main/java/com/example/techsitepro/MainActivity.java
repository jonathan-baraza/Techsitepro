package com.example.techsitepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.techsitepro.Fragments.HomeFragment;
import com.example.techsitepro.Fragments.NotificationFragment;
import com.example.techsitepro.Fragments.ProfileFragment;
import com.example.techsitepro.Fragments.SearchFragment;
import com.example.techsitepro.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView=findViewById(R.id.bottom_navigation_view);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        selectorFragment=new HomeFragment();
                        break;
                    case R.id.nav_search:
                        selectorFragment=new SearchFragment();
                        break;
                    case R.id.nav_addpost:
                        startActivity(new Intent(MainActivity.this,AddPostActivity.class));
                        break;
                    case R.id.nav_notification:
                        selectorFragment=new NotificationFragment();
                        break;
                    case R.id.nav_profile:
                        getSharedPreferences("profile",MODE_PRIVATE).edit().putString("profileId",null).apply();
                        getSharedPreferences("profile2",MODE_PRIVATE).edit().putBoolean("isbottom",true).apply();
                        selectorFragment=new ProfileFragment();
                        break;
                    default:
                        break;
                }

                if(selectorFragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectorFragment).commit();

                }
                return true;
            }
        });

        Bundle intent=getIntent().getExtras();
        if(intent!=null){
            System.out.println("intent gotten");
            String profileId=intent.getString("profileId");
            boolean isbottom=intent.getBoolean("isbottom");

            getSharedPreferences("profile2",MODE_PRIVATE).edit().putBoolean("isbottom",false).apply();
            getSharedPreferences("profile",MODE_PRIVATE).edit().putString("profileId",profileId).apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();


        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        }



    }


    @Override
    public void onBackPressed() {

    }
}