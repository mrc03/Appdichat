package mrc.appdichat;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String name;
    String image;
    String user_id;

    private Toolbar toolbar;
    private CircleImageView circleImageView;
    private TextView chatName;

    private DatabaseReference messageDatabaseReference;
    private FirebaseAuth mAuth;

    private ImageButton sendImageButton;
    private ImageButton sendTextButton;
    private EditText messageInput;

    private DatabaseReference chatDatabasereference;
    private DatabaseReference usersDatabaseReference;
    private DatabaseReference rootReference;

    private RecyclerView mMessagesList;
    private List<Messages> messagesList = new ArrayList<>();
    private MessageAdapter messageAdapter;

    private LinearLayoutManager linearLayoutManager;
    private int flag = 0;

    // for storing text of edit text
    String storeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.chat_app_bar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_app_bar, null);
        actionBar.setCustomView(action_bar_view);
        /*chatName=findViewById(R.id.chat_display_name);
        circleImageView=findViewById(R.id.chat_image_view);*/


        user_id = getIntent().getStringExtra("key");
        name = getIntent().getStringExtra("name");
        image = getIntent().getStringExtra("image");


        messageDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Messages");
        messageDatabaseReference.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        chatDatabasereference = FirebaseDatabase.getInstance().getReference().child("Chat");
        chatDatabasereference.keepSynced(true);
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());

        sendImageButton = findViewById(R.id.chat_plus_button);
        sendTextButton = findViewById(R.id.chat_send_button);
        messageInput = findViewById(R.id.chat_message_field);


        mMessagesList = findViewById(R.id.chat_recycle_view);
        linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setStackFromEnd(true);
        mMessagesList.setLayoutManager(linearLayoutManager);
        mMessagesList.setHasFixedSize(true);
        messageAdapter = new MessageAdapter(messagesList);
        mMessagesList.setAdapter(messageAdapter);

        chatName = action_bar_view.findViewById(R.id.chat_display_name);
        circleImageView = action_bar_view.findViewById(R.id.chat_image_view);
        chatName.setText(name);
        Picasso.with(this).load(image).placeholder(R.drawable.defaultprofile).networkPolicy(NetworkPolicy.OFFLINE).into(circleImageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(ChatActivity.this).load(image).placeholder(R.drawable.defaultprofile).into(circleImageView);

            }
        });


        loadMessages();


        sendTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessagesList.removeAllViews();

                storeText = messageInput.getText().toString();
                if (!TextUtils.isEmpty(storeText)) {
                    messageInput.setText("");
                    storeMessage();
                    updateChat();
                }


            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        usersDatabaseReference.child("online").setValue("true");
    }

    public void updateChat() {
        chatDatabasereference.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(user_id)) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    chatDatabasereference.child(mAuth.getUid()).child(user_id).setValue(chatAddMap);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_app_bar_menu, menu);
        return true;
    }

    public void storeMessage() {
        String text = storeText;
        if (!TextUtils.isEmpty(text)) {
            Map map = new HashMap();
            map.put("message", text);
            map.put("from", mAuth.getUid());
            map.put("type", "text");


            messageDatabaseReference.child(mAuth.getUid()).child(user_id).push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });

            messageDatabaseReference.child(user_id).child(mAuth.getUid()).push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //Toast.makeText(ChatActivity.this, "Hi", Toast.LENGTH_SHORT).show();

                }
            });
        }


    }


    public void loadMessages() {


        DatabaseReference tempreference = messageDatabaseReference.child(mAuth.getUid()).child(user_id);

        tempreference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
                mMessagesList.scrollToPosition(messagesList.size() - 1);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        storeText = messageInput.getText().toString();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usersDatabaseReference.child("online").setValue("false");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(storeText)) {
            messageInput.setText(storeText);
            messageInput.setSelection(storeText.length());
        }
    }
}
