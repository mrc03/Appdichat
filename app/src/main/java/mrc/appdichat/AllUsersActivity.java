package mrc.appdichat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        toolbar = (Toolbar) findViewById(R.id.users__app_bar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.users_recycle_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);

        //  NOW CREATE CUSTOM ADAPTER FOR THE RECYCLER VIEW .UN LIKE LIST VIEW THE NORMAL ARRY ADAPTER
        // DOESNT GO HERE AS IT SEEMS AND SO THERE IS O CHOOICE RAHER THAN BUILDING THE OUR VERY OWN CUSTOM ADAPTER.


    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<Users, UsersViewHolder>(Users.class, R.layout.users_single_row, UsersViewHolder.class, databaseReference) {
                    @Override
                    protected void populateViewHolder(UsersViewHolder usersViewHolder, Users model, final int position) {
                        usersViewHolder.setName(model.getName());
                        usersViewHolder.setStatus(model.getStatus());
                        usersViewHolder.setImage(model.getImage(), getApplicationContext());
                        usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String user_id = getRef(position).getKey();
                                Intent profileIntent = new Intent(AllUsersActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("key", user_id);
                                startActivity(profileIntent);
                            }
                        });

                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        databaseReference.child(FirebaseAuth.getInstance().getUid()).child("online").setValue("true");

    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView nameInput = (TextView) mView.findViewById(R.id.single_display_name);
            nameInput.setText(name);
        }

        public void setStatus(String status) {
            TextView statusInput = (TextView) mView.findViewById(R.id.single_status);
            statusInput.setText(status);
        }

        public void setImage(final String image, final Context context) {
            final CircleImageView circleImageView = (CircleImageView) mView.findViewById(R.id.single_image_view);
            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).
                    placeholder(R.drawable.defaultprofile).into(circleImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(image).placeholder(R.drawable.defaultprofile).into(circleImageView);

                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.child(FirebaseAuth.getInstance().getUid()).child("online").setValue("false");
    }
}

