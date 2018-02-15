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
public class ChatFragment extends android.support.v4.app.Fragment {

    private RecyclerView recyclerView;
    private View view;


    private DatabaseReference chatDatabaseReference;
    private DatabaseReference usersDatabaseReference;
    private String current_id;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.chat_recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        current_id = FirebaseAuth.getInstance().getUid();

        chatDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Chat").child(current_id);
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        chatDatabaseReference.keepSynced(true);
        usersDatabaseReference.keepSynced(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Chat, ChatViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(
                Chat.class, R.layout.users_single_row, ChatViewHolder.class, chatDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(final ChatViewHolder viewHolder, Chat model, int position) {
                final String other_id = getRef(position).getKey();
                usersDatabaseReference.child(other_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("name").getValue().toString();
                        final String image = dataSnapshot.child("image").getValue().toString();
                        viewHolder.setName(name);
                        viewHolder.setImage(getContext(), image);
                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                chatIntent.putExtra("key", other_id);
                                chatIntent.putExtra("name", name);
                                chatIntent.putExtra("image", image);
                                startActivity(chatIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };
        firebaseRecyclerAdapter.setHasStableIds(true);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        View view;

        public ChatViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setName(String name) {
            TextView textView = view.findViewById(R.id.single_display_name);
            textView.setText(name);
        }

        public void setImage(final Context context, final String image) {
            final CircleImageView circleImageView = view.findViewById(R.id.single_image_view);
            Picasso.with(context).load(image).placeholder(R.drawable.defaultprofile).networkPolicy(NetworkPolicy.OFFLINE).into(circleImageView, new Callback() {
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
}
