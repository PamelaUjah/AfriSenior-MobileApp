package com.northumbria.old_ish;

import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView searchbtn;
    private EditText searchInputText;
    private RecyclerView searchResultList;
    private DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        mToolbar = (Toolbar) findViewById(R.id.find_friends_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

        searchResultList = (RecyclerView) findViewById(R.id.search_result_list);
        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(this));
        searchbtn = (ImageView) findViewById(R.id.search_friends_btn);
        searchInputText = (EditText) findViewById(R.id.search_box_input);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");


        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchBoxInput = searchInputText.toString();
                searchPeopleAndFriends(searchBoxInput);
            }
        });

    }

    private void searchPeopleAndFriends(String searchBoxInput) {

        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();

        Query searchPeopleandFriendsQuery = userRef.orderByChild("Full Name")
                .startAt(searchBoxInput).endAt(searchBoxInput + "\uf8ff");

        FirebaseRecyclerOptions<FindFriends> adapter =
                new FirebaseRecyclerOptions.Builder<FindFriends>()
                .setQuery(searchPeopleandFriendsQuery, FindFriends.class)
                .build();

        FirebaseRecyclerAdapter<FindFriends,FindFriendsViewHolder > firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>(adapter) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position, @NonNull FindFriends model) {
                holder.friendsStatus.setText(model.getProfileStatus());
                holder.friendsusername.setText(model.getFullName());
                Glide.with(FindFriendsActivity.this).load(model.getProfileImage()).dontAnimate().into(holder.friendsprofileImage);

            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_users_display_layout, viewGroup, false);
                FindFriendsActivity.FindFriendsViewHolder viewHolder = new FindFriendsActivity.FindFriendsViewHolder(view);
                return viewHolder;
            }
        };
        searchResultList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{
        TextView friendsusername, friendsStatus;
        CircleImageView friendsprofileImage;


        public FindFriendsViewHolder(View itemView){

            super(itemView);
            friendsusername = itemView.findViewById(R.id.all_users_profile_full_name);
            friendsStatus= itemView.findViewById(R.id.all_users_status);
            friendsprofileImage = itemView.findViewById(R.id.all_users_profileImage);

        }


    }
}
