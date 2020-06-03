package com.northumbria.old_ish;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PointerIconCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class setupActivity extends AppCompatActivity {

    private EditText userName;
    private EditText fullname;
    private EditText countryName;
    private Button saveInformationbtn;
    private CircleImageView userProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private ProgressDialog loadingBar;
    private StorageReference userProfileRef;

    String currentUserId;
    final static int gallery_pic = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        userName = (EditText) findViewById(R.id.setup_username);
        fullname = (EditText) findViewById(R.id.setup_fullname);
        countryName = (EditText) findViewById(R.id.setup_country);
        saveInformationbtn = (Button) findViewById(R.id.setup_save_btn);
        userProfileImage = (CircleImageView) findViewById(R.id.setup_profile_image);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userProfileRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        loadingBar = new ProgressDialog(this);

        saveInformationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAccountSetupInformation();
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,gallery_pic);
            }
        });

        /*userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

                    //String image = dataSnapshot.child("ProfileImage").getValue().toString();

                    Glide.with(setupActivity.this).load(storageReference).dontAnimate().into(userProfileImage);

                        //Picasso.get().load(image).placeholder(R.drawable.profile).into(userProfileImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==gallery_pic && resultCode==RESULT_OK && data!=null){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK){

                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait while we are updating your profile image...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                final Uri resultUri = result.getUri();
                final StorageReference filepath = userProfileRef.child(currentUserId + ".jpg");
                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
                                userRef.child("ProfileImage").setValue(downloadUrl)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Glide.with(setupActivity.this).load(downloadUrl).dontAnimate().placeholder(R.drawable.profile).into(userProfileImage);
                                                    Toast.makeText(setupActivity.this, "Profile image stored to database successfully.", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();

                                                }
                                                else
                                                {
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(setupActivity.this, "Error Occurred:" + message, Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                            }
                                        });

                            }
                        });
                    }
                });
                    //@Override
                    /*public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        filepath.putFile(resultUri).add
                        if (task.isSuccessful()){
                            Toast.makeText(setupActivity.this, "Profile Image Stored Successfully...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            final String downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();
                            userRef.child("ProfileImage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Intent selfIntent = new Intent(setupActivity.this, setupActivity.class);
                                                startActivity(selfIntent);
                                                Toast.makeText(setupActivity.this,"Profile Image stored Successfully", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                            else{
                                                String message = task.getException().getMessage();
                                                Toast.makeText(setupActivity.this,"Error Occurred: "+ message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();

                                            }
                                        }
                                    });
                        }
                        else
                            Toast.makeText(setupActivity.this,"Error Occurred: Image Cannot be Cropped, Try Again", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                    }*/
                }
            }
        }


    private void saveAccountSetupInformation() {
        String username = userName.getText().toString();
        String userFullname = fullname.getText().toString();
        String country = countryName.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this,"Please enter your Username",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userFullname)){
            Toast.makeText(this,"Please Enter your full name",Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(country)) {
            Toast.makeText(this, "Please enter your country", Toast.LENGTH_SHORT).show();

        }
        else{
            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait while we are saving your account details...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("Username", username);
            userMap.put("Full Name", userFullname);
            userMap.put("Country", country);
            userMap.put("Profile Status", "Hey there, I am using AfriSenior Social Network");
            userMap.put("Gender", "None");
            userMap.put("DOB", "None");
            userMap.put("Relationship Status", "None");
            userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        sendUserToMainActivity();
                        Toast.makeText(setupActivity.this, "Your Account Has Been Created Successfully", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                    else {
                        String message = task.getException().getMessage();
                        Toast.makeText(setupActivity.this,"Error Occurred: " + message,Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void sendUserToMainActivity() {
        Intent setupIntent = new Intent(setupActivity.this, MainActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
}
