package com.example.techsitepro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.techsitepro.Models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private Button updateProfilePic,update;
    private EditText username,fullname,bio;
    private FirebaseUser fUser;

    private Uri imageUri;
    private StorageTask storageTask;
    private StorageReference storageReference;
    private String imageUrl;

    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        updateProfilePic=findViewById(R.id.updateProfilePic);
        username=findViewById(R.id.username);
        fullname=findViewById(R.id.fullname);
        bio=findViewById(R.id.bio);
        update=findViewById(R.id.update);

        fUser= FirebaseAuth.getInstance().getCurrentUser();


        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        pd=new ProgressDialog(EditProfileActivity.this);
        pd.setMessage("Updating your profile");
        pd.setCanceledOnTouchOutside(false);



        setUserData();
        
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username_txt= username.getText().toString();
                String fullname_txt= fullname.getText().toString();
                String bio_txt= bio.getText().toString();
                
                if(TextUtils.isEmpty(username_txt)||TextUtils.isEmpty(fullname_txt)||TextUtils.isEmpty(bio_txt)){
                    Toast.makeText(EditProfileActivity.this, "You cannot update an empty field!", Toast.LENGTH_SHORT).show();
                }
                else{
                    pd.show();
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("username",username_txt);
                    map.put("fullname",fullname_txt);
                    map.put("bio",bio_txt);
                    
                    FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pd.dismiss();
                            if(task.isSuccessful()){
                                Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
                                Toast.makeText(EditProfileActivity.this, "Something went wrong! update failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        updateProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });






    }

    private void setUserData() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();
            uploadProfilePic();
        }
    }

    private void uploadProfilePic() {
        if(imageUri!=null){
            pd.show();
            storageReference=FirebaseStorage.getInstance().getReference().child("Uploads").child(System.currentTimeMillis()+getFileExtension(imageUri));
            storageTask=storageReference.putFile(imageUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        Toast.makeText(EditProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri result=task.getResult();
                    imageUrl=result.toString();

                    FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).child("profileimage").setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pd.dismiss();
                            if(task.isSuccessful()){
                                Toast.makeText(EditProfileActivity.this, "Photo updated successfully", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(EditProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            });

        }
        else{
            Toast.makeText(this, "No image was selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        String ext=mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
        return ext;
    }
}