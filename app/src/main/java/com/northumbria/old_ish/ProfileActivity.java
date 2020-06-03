package com.northumbria.old_ish;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView userName, userProfileName, userCountry, userStatus,userGender, userDob;
    private CircleImageView userProfileImage;
    private DatabaseReference profileUserRef;
    private FirebaseAuth mAuth;
    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userName = (TextView) findViewById(R.id.my_profile_username);
        userProfileName = (TextView) findViewById(R.id.my_profile_fullname);
        userCountry = (TextView) findViewById(R.id.my_profile_country);
        userGender = (TextView) findViewById(R.id.my_profile_gender);
        userDob = (TextView) findViewById(R.id.my_profile_dob);
        userStatus = (TextView) findViewById(R.id.my_profile_status);
        userProfileImage = (CircleImageView) findViewById(R.id.my_profile_pic);

        profileUserRef.addValueEventListener(new ValueEventListener() {
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

                    Glide.with(ProfileActivity.this).load(myProfileImage).dontAnimate().placeholder(R.drawable.profile).into(userProfileImage);
                    userName.setText("@" + myuserName);
                    userProfileName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userDob.setText("Date of Birth: " + myDob);
                    userCountry.setText("Country: " + myCountry);
                    userGender.setText("Gender: "+ mygender);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
