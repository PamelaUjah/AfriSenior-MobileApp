package com.northumbria.old_ish;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText userName, userProfileName, userCountry, userStatus,userGender, userDob;
    private Button updateAccountSettingsbtn;
    private CircleImageView userProfileImage;
    private DatabaseReference settingsUserRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    final static int gallery_pic = 1;
    private ProgressDialog loadingBar;
    private StorageReference userProfileRef;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        settingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userName = (EditText) findViewById(R.id.setting_username);
        userProfileName = (EditText) findViewById(R.id.setting_profile_name);
        userCountry = (EditText) findViewById(R.id.setting_country);
        userGender = (EditText) findViewById(R.id.setting_gender);
        userDob = (EditText) findViewById(R.id.setting_dob);
        userStatus = (EditText) findViewById(R.id.setting_status);
        userProfileImage = (CircleImageView) findViewById(R.id.setting_profile_image);
        updateAccountSettingsbtn = (Button) findViewById(R.id.update_account_settings_btn);
        loadingBar = new ProgressDialog(this);
        userProfileRef = FirebaseStorage.getInstance().getReference().child("Profile Images");



        settingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    String myProfileImage = dataSnapshot.child("ProfileImage").getValue().toString();
                    String myuserName = dataSnapshot.child("Username").getValue().toString();
                    String myProfileName = dataSnapshot.child("Full Name").getValue().toString();
                    String myCountry = dataSnapshot.child("Country").getValue().toString();
                    String myProfileStatus= dataSnapshot.child("Profile Status").getValue().toString();
                    String mygender = dataSnapshot.child("Gender").getValue().toString();
                    String myDob= dataSnapshot.child("DOB").getValue().toString();

                    Glide.with(SettingsActivity.this).load(myProfileImage).dontAnimate().placeholder(R.drawable.profile).into(userProfileImage);
                    userName.setText(myuserName);
                    userProfileName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userDob.setText(myDob);
                    userCountry.setText(myCountry);
                    userGender.setText(mygender);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateAccountSettingsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAccountInfo();
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
                                settingsUserRef.child("ProfileImage").setValue(downloadUrl)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    //Intent selfIntent = new Intent(SettingsActivity.this,SettingsActivity.class);
                                                    //startActivity(selfIntent);
                                                    Glide.with(SettingsActivity.this).load(downloadUrl).dontAnimate().placeholder(R.drawable.profile).into(userProfileImage);
                                                    Toast.makeText(SettingsActivity.this, "Profile image stored to database successfully.", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();

                                                }
                                                else
                                                {

                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(SettingsActivity.this, "Error Occurred:" + message, Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                            }
                                        });

                            }
                        });
                    }
                });
            }
        }
    }



    private void validateAccountInfo() {
        String username = userName.getText().toString();
        String profilename = userProfileName.getText().toString();
        String status = userStatus.getText().toString();
        String dob = userDob.getText().toString();
        String country= userCountry.getText().toString();
        String gender = userGender.getText().toString();


        if(TextUtils.isEmpty(username)){

            Toast.makeText(this, "Please write your username...", Toast.LENGTH_SHORT).show();

        }

        else if(TextUtils.isEmpty(profilename)){

            Toast.makeText(this, "Please write your profile name...", Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(status)){

            Toast.makeText(this, "Please write your status...", Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(dob)){

            Toast.makeText(this, "Please enter your date of birth...", Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(country)){

            Toast.makeText(this, "Please enter your country of residence...", Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(gender)){

            Toast.makeText(this, "Please enter your gender...", Toast.LENGTH_SHORT).show();

        }
        else {
            loadingBar.setTitle("Updating Account");
            loadingBar.setMessage("Please wait while we are updating your account...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            updateAccountInformation(username,profilename,status,dob, country,gender);
        }
    }

    private void updateAccountInformation(String username, String profilename, String status, String dob, String country, String gender) {

        HashMap userMap = new HashMap();
        userMap.put("Username", username);
        userMap.put("Full Name", profilename);
        userMap.put("Profile Status", status);
        userMap.put("DOB", dob);
        userMap.put("Country", country);
        userMap.put("Gender", gender);

        settingsUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    loadingBar.dismiss();
                    sendUserToMainActivity();
                    Toast.makeText(SettingsActivity.this, "Account Settings Updated Successfully", Toast.LENGTH_SHORT).show();
                }

                else{
                    Toast.makeText(SettingsActivity.this, "Error Occurred whilst updating account information", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });

    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
