package mrc.appdichat;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "ERRORERROR";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private View view;

    private String user_id;


    private DatabaseReference databaseReference;
    private DatabaseReference usersDatabaseReference;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(user_id);
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);
        usersDatabaseReference.keepSynced(true);


        /*if(view==null)
        {
            Log.e(TAG,"KUCH TO GADBAD HAI");
        }
        else
        {
            Log.e(TAG,"ye to sai hai bhai");
        }*/

        // printing ye to sai hai implies view is not null.


        recyclerView = (RecyclerView) view.findViewById(R.id.friends_recycle_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter
                <Friends, FriendsViewHolder>(
                Friends.class, R.layout.users_single_row, FriendsViewHolder.class, databaseReference
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, final Friends model, final int position) {
                String otherId = getRef(position).getKey();
                usersDatabaseReference.child(otherId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("name").getValue().toString();
                        final String image = dataSnapshot.child("image").getValue().toString();
                        viewHolder.setName(name);
                        viewHolder.setImage(getContext(), image);
                        viewHolder.setDate(model.getDate());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String user_id = getRef(position).getKey();

                                String[] choices = new String[]{"View Profile", "Send Message"};
                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                                builder.setTitle("SELECT");
                                builder.setItems(choices, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        switch (which) {
                                            case 0:
                                                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                                profileIntent.putExtra("key", user_id);
                                                startActivity(profileIntent);
                                                break;
                                            case 1:
                                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                chatIntent.putExtra("key",user_id);
                                                chatIntent.putExtra("name", name);
                                                chatIntent.putExtra("image", image);
                                                startActivity(chatIntent);
                                                break;

                                            default:
                                                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                    @Override
                                                    public void onDismiss(DialogInterface dialog) {

                                                    }
                                                });


                                        }


                                    }

                                });
                                builder.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDate(String date) {
            TextView textView = mView.findViewById(R.id.single_status);
            textView.setText("Friend Since " + date);
        }

        public void setName(String name) {
            TextView textView = mView.findViewById(R.id.single_display_name);
            textView.setText(name);
        }

        public void setImage(final Context context, final String image) {
            final CircleImageView circleImageView = mView.findViewById(R.id.single_image_view);
            Picasso.with(context).load(image).placeholder(R.drawable.defaultprofile).
                    networkPolicy(NetworkPolicy.OFFLINE).into(circleImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(image).placeholder(R.drawable.defaultprofile).
                            into(circleImageView);

                }
            });
        }


    }
}
