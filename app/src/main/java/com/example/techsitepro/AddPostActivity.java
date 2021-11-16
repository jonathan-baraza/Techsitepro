package com.example.techsitepro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddPostActivity extends AppCompatActivity {
    private ImageView exit,selectedImage;
    private TextView submitPost;
    private SocialAutoCompleteTextView description;
    private Uri imageUri;
    private String imageUrl;
    private List<String> tags;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        exit=findViewById(R.id.exit);
        selectedImage=findViewById(R.id.selectedImage);
        submitPost=findViewById(R.id.submitPost);
        description=findViewById(R.id.description);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        pd=new ProgressDialog(this);
        pd.setMessage("Uploading your post");
        pd.setCanceledOnTouchOutside(false);


        submitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri==null){
                    Toast.makeText(AddPostActivity.this, "No image was selected", Toast.LENGTH_SHORT).show();
                }
                else{
                    String postDescription=description.getText().toString();
                    if(TextUtils.isEmpty(postDescription)){
                        Toast.makeText(AddPostActivity.this, "Enter post description", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        uploadImage(postDescription);
                    }
                }

            }
        });


        CropImage.activity().start(AddPostActivity.this);
    }

    private void uploadImage(String descriptionString) {
        pd.show();
        StorageReference fileRef= FirebaseStorage.getInstance().getReference().child("Posts").child(System.currentTimeMillis()+getFileExtension(imageUri));
        StorageTask uploadTask=fileRef.putFile(imageUri);
        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if(!task.isSuccessful()){
                    pd.dismiss();
                    Toast.makeText(AddPostActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri data=task.getResult();
                imageUrl=data.toString();

                DatabaseReference myRef= FirebaseDatabase.getInstance().getReference().child("Posts");
                String postId=myRef.push().getKey();

                HashMap<String,Object> map=new HashMap<>();
                map.put("id",postId);
                map.put("imageurl",imageUrl);
                map.put("description",descriptionString);
                map.put("publisherid", FirebaseAuth.getInstance().getCurrentUser().getUid());

                myRef.child(postId).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        DatabaseReference tagRef=FirebaseDatabase.getInstance().getReference().child("Hashtags");
                        tags=new ArrayList<>();
                        tags=description.getHashtags();
                        HashMap<String,Object> tagMap=new HashMap<>();

                        if(!tags.isEmpty()){
                            for(String tag:tags){
                                tagMap.clear();
                                tagMap.put("tag",tag.toLowerCase());
                                tagMap.put("postId",postId);

                                tagRef.child(tag.toLowerCase()).child(postId).setValue(tagMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        pd.dismiss();
                                        Toast.makeText(AddPostActivity.this, "Post addedd successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        else{
                            pd.dismiss();
                            Toast.makeText(AddPostActivity.this, "Post addedd successfully", Toast.LENGTH_SHORT).show();
                        }

                        startActivity(new Intent(AddPostActivity.this,MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();
            selectedImage.setImageURI(imageUri);
        }
    }
}