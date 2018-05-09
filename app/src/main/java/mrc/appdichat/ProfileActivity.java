package mrc.appdichat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView imageView;
    private TextView nameInput;
    private TextView statusInput;
    private TextView friendsCountInput;
    private Button sendRequestButton;
    private Button declineRequestButton;

    String user_id;

    private DatabaseReference databaseReference;

    private DatabaseReference friendsDatabaseReference;
    private DatabaseReference currentFriendsReference;
    private FirebaseUser firebaseUser;
    String mcurrent_state;


    long childCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imageView = (CircleImageView) findViewById(R.id.profile_image_view);
        nameInput = (TextView) findViewById(R.id.profile_name_view);
        statusInput = (TextView) findViewById(R.id.profile_status_view);
        friendsCountInput = (TextView) findViewById(R.id.profile_friendcount_view);
        sendRequestButton = (Button) findViewById(R.id.profile_friends_button);
        declineRequestButton = (Button) findViewById(R.id.profile_decline_friends_button);
        declineRequestButton.setVisibility(View.GONE);

        user_id = getIntent().getStringExtra("key");


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        databaseReference.keepSynced(true);
        currentFriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");


        friendsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friends_request");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mcurrent_state = "not_friends";

        if (user_id.equals(firebaseUser.getUid())) {
            sendRequestButton.setVisibility(View.GONE);
            declineRequestButton.setVisibility(View.GONE);
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();

                nameInput.setText(name);
                statusInput.setText(status);
                Picasso.with(ProfileActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).
                        placeholder(R.drawable.defaultprofile).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.defaultprofile).into(imageView);

                    }
                });

                friendsDatabaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)) {

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if (req_type.equals("sent")) {
                                sendRequestButton.setText("CANCEL FRIEND REQUEST");
                                mcurrent_state = "sent";
                            } else if (req_type.equals("received")) {
                                sendRequestButton.setText("ACCEPT FRIEND REQUEST");
                                mcurrent_state = "received";
                                declineRequestButton.setVisibility(View.VISIBLE);

                            } else ;
                        } else {


                            currentFriendsReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_id)) {
                                        mcurrent_state = "friends";
                                        sendRequestButton.setText("UNFRIEND");
                                    } else {
                                        sendRequestButton.setText("SEND FRIEND REQUEST");
                                        mcurrent_state = "not_friends";
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestButton.setEnabled(false);
                if (mcurrent_state.equalsIgnoreCase("not_friends")) {


                    friendsDatabaseReference.child(firebaseUser.getUid())
                            .child(user_id).child("request_type").setValue("sent").
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // friendsDatabaseReference = FirebaseDatabase.getInstance().getReference();
                                        friendsDatabaseReference.
                                                child(user_id).child(firebaseUser.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                sendRequestButton.setEnabled(true);
                                                sendRequestButton.setText("CANCEL FRIEND REQUEST");
                                                mcurrent_state = "sent";
                                                // Toast.makeText(ProfileActivity.this, "Request Send Successfully", Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                    } else {
                                        Toast.makeText(ProfileActivity.this, "Sending Request Failed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else if (mcurrent_state.equalsIgnoreCase("sent")) {

                    friendsDatabaseReference.child(firebaseUser.getUid()).child(user_id).removeValue();
                    friendsDatabaseReference.child(user_id).child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            sendRequestButton.setEnabled(true);
                            mcurrent_state = "not_friends";
                            sendRequestButton.setText("SEND FRIEND REQUEST");
                        }
                    });


                } else if (mcurrent_state.equals("received")) {
                    String curr_date = DateFormat.getDateInstance().format(new Date());
                    declineRequestButton.setVisibility(View.VISIBLE);
                    mcurrent_state = "friends";
                    currentFriendsReference.child(firebaseUser.getUid()).child(user_id).child("date").setValue(curr_date);
                    currentFriendsReference.child(user_id).child(firebaseUser.getUid()).child("date").setValue(curr_date);
                    friendsDatabaseReference.child(firebaseUser.getUid()).child(user_id).removeValue();
                    friendsDatabaseReference.child(user_id).child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            sendRequestButton.setEnabled(true);
                            sendRequestButton.setText("UNFRIEND");
                            declineRequestButton.setVisibility(View.GONE);
                        }
                    });

                } else if (mcurrent_state.equals("friends")) {
                    mcurrent_state = "not_friends";
                    currentFriendsReference.child(firebaseUser.getUid()).child(user_id).removeValue();
                    currentFriendsReference.child(user_id).child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            sendRequestButton.setEnabled(true);
                            sendRequestButton.setText("SEND FRIEND REQUEST");
                        }
                    });

                } else ;
            }
        });


        declineRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mcurrent_state = "not_friends";
                friendsDatabaseReference.child(firebaseUser.getUid()).child(user_id).removeValue();
                friendsDatabaseReference.child(user_id).child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendRequestButton.setText("SEND FRIEND REQUEST");
                        sendRequestButton.setVisibility(View.GONE);
                    }
                });


            }
        });

        // REMAINING :: HOW TO COUNT THE NUMMBER OF THE CHILDERN AT A CERTAIN NODE.

        currentFriendsReference.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                childCount = dataSnapshot.getChildrenCount();
                String text = String.valueOf(childCount);
                if (!firebaseUser.getUid().equals(user_id))
                    friendsCountInput.setText(nameInput.getText().toString() + " has " + text + " connections ");
                else
                    friendsCountInput.setText("You" + " have " + text + " connections ");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid());
        usersDatabaseReference.child("online").setValue("true");
    }

    @Override
    protected void onStop() {
        super.onStop();
        DatabaseReference usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid());
        usersDatabaseReference.child("online").setValue("false");
    }
}

