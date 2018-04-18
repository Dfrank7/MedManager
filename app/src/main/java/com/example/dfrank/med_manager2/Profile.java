package com.example.dfrank.med_manager2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dfrank on 4/1/18.
 */

public class Profile extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private int CHOOSE_IMAGE = 1001;
    private User user;

    @BindView(R.id.displayName)
    EditText displayName;
//    @BindView(R.id.imageView)
//    ImageView profilePhoto;
    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;
    @BindView(R.id.buttonSave)
    Button saveUser;
    @BindView(R.id.emailName) EditText email;
    private Uri uriProfileImage;
    String profileImageUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("User");
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //User user = new User(getName());
        //mDatabase.child(getUid()).setValue(user);
//        profilePhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showImageChooser();
//            }
//        });

        loadUserInfo();
        saveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInfo();
            }
        });

//        user = new User(getName(), getPhotoUrl());
//        mDatabase.setValue(user);

    }

    private String getUid() {
        return mAuth.getCurrentUser().getUid();
    }

    private String getName() {
        return mAuth.getCurrentUser().getDisplayName();
    }

    private String getEmail() {
        return mAuth.getCurrentUser().getEmail();
    }
    private Uri getPhotoUrl(){
        return mAuth.getCurrentUser().getPhotoUrl();
    }

    private void loadUserInfo(){
//        if (getPhotoUrl()!=null){
//            Glide.with(this)
//                    .load(getPhotoUrl())
//                    .into(profilePhoto);
//        }
        if (getName()!=null){
            displayName.setText(getName());
        }
        if (getEmail()!=null){
            email.setText(getEmail());
        }

    }

    private void saveUserInfo(){
        String name = displayName.getText().toString().trim();
        String emailText = email.getText().toString();
        profileImageUrl = getPhotoUrl().toString();
        if (name.isEmpty()){
            displayName.setError("Field can't be empty");
            return;
        }

        if (mAuth.getCurrentUser()!=null && profileImageUrl!=null){
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            mAuth.getCurrentUser().updateEmail(emailText)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "User email address updated.");
                            }
                        }
                    });

            mAuth.getCurrentUser().updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Profile.this,"Profile Updated",Toast.LENGTH_SHORT ).show();
                                finish();
                            }else {
                                Toast.makeText(Profile.this,"Profile Update Failed",Toast.LENGTH_SHORT ).show();
                            }
                        }
                    });
        }

    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CHOOSE_IMAGE && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
//                profilePhoto.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {
        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");

        if (uriProfileImage != null) {
            //progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressBar.setVisibility(View.GONE);
                            profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //progressBar.setVisibility(View.GONE);
                            // Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}

