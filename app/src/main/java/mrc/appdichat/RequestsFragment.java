package mrc.appdichat;


import android.content.Context;
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
public class RequestsFragment extends android.support.v4.app.Fragment {


    private RecyclerView recyclerView;
    private View mView;


    private DatabaseReference friendsDatabaseReference;
    private DatabaseReference usersDatabaseReference;

    // private final int color = getResources().getColor(R.color.colorPrimaryDark);  WILL NOT WORK.


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_requests, container, false);
        recyclerView = mView.findViewById(R.id.req_recycle_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        friendsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friends_request").
                child(FirebaseAuth.getInstance().getUid());
        friendsDatabaseReference.keepSynced(true);
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        usersDatabaseReference.keepSynced(true);

        return mView;


    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Requests, RequestsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Requests, RequestsViewHolder>
                (
                        Requests.class, R.layout.users_single_row, RequestsViewHolder.class, friendsDatabaseReference
                ) {
            @Override
            protected void populateViewHolder(final RequestsViewHolder viewHolder, final Requests model, final int position) {

                String otherId = getRef(position).getKey();

                usersDatabaseReference.child(otherId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String image = dataSnapshot.child("image").getValue().toString();
                        viewHolder.setName(name);
                        viewHolder.setImage(getContext(), image);
                        viewHolder.setType(model.getRequest_type(), getActivity().getResources().getColor(R.color.colorPrimaryDark));

                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String user_id = getRef(position).getKey();
                                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                profileIntent.putExtra("key", user_id);
                                startActivity(profileIntent);
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

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {
        private View view;


        public RequestsViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setName(String name) {
            TextView textView = view.findViewById(R.id.single_display_name);
            textView.setText(name);

        }

        public void setType(String type, int color) {

            if (type.equalsIgnoreCase("sent")) {
                TextView textView = view.findViewById(R.id.single_status);
                textView.setText("Status: " + type);
            } else {
                TextView textView = view.findViewById(R.id.single_status);
                textView.setTextColor(color);
                textView.setText("Status: " + type);
            }


        }

        public void setImage(final Context context, final String image) {

            final CircleImageView circleImageView = view.findViewById(R.id.single_image_view);
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
