package com.example.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mUserList;

    private DatabaseReference mUserRef;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All User");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mUserList = findViewById(R.id.users_list);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onStart() {
        super.onStart();
        startListening();

        mUserRef.child("online").setValue("true");
    }

    public void startListening() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Users").limitToFirst(50);

        FirebaseRecyclerOptions<Users> options = new  FirebaseRecyclerOptions.Builder<Users>().setQuery(query, Users.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(options) {

            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);
                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(UserViewHolder holder, int position, Users model) {

                holder.setName(model.name);
                holder.setStatus(model.status);
                holder.setUserImage(model.thumb_image);

                final String user_id = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id", user_id);
                        startActivity(profileIntent);

                    }
                });

            }
        };

        mUserList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setStatus(String status) {
            TextView userStatusView = mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);
        }

        public void setUserImage(String thumb_image) {
            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);
            if (!thumb_image.equals("default")) {
                Picasso.get().load(thumb_image).placeholder(R.mipmap.default_profie).into(userImageView);
            }

        }
    }
}
