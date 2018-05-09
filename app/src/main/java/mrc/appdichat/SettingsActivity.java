package mrc.appdichat;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseAuth;
    String userId;

    private TextView nameInput;
    private TextView statusInput;
    private Button imageButton;
    private Button statusButton;
    private CircleImageView circleImageView;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int CAMERA_REQUEST = 2;

    private StorageReference storageReference;
    private DatabaseReference statsReference;

    String stats = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        statsReference = FirebaseDatabase.getInstance().getReference().child("Users");
        statsReference.keepSynced(true);


        userId = firebaseAuth.getUid();

        nameInput = (TextView) findViewById(R.id.settings_name_input);
        statusInput = (TextView) findViewById(R.id.settings_status_input);
        imageButton = (Button) findViewById(R.id.settings_changeimagebutton);
        statusButton = (Button) findViewById(R.id.settings_changestatusbutton);
        circleImageView = (CircleImageView) findViewById(R.id.settings_image_view);

        setListeners();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String displayName = dataSnapshot.child("name").getValue(String.class);
                String status = dataSnapshot.child("status").getValue(String.class);
                stats = status;
                final String image = dataSnapshot.child("image").getValue(String.class);
                nameInput.setText(displayName);
                statusInput.setText(status);
                if (!image.equalsIgnoreCase("default")) {

                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).
                            placeholder(R.drawable.defaultprofile).into(circleImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.defaultprofile).into(circleImageView);

                        }
                    });

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setListeners() {

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);


            }
        });

        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("EDIT STATUS");
                builder.setIcon(R.drawable.edit_stats);


                final EditText input = new EditText(SettingsActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                input.setText(stats);
                input.setSelection(stats.length());
                builder.setView(input);

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String new_stats = input.getText().toString();
                        if (!TextUtils.isEmpty(new_stats))
                            statsReference.child(userId).child("status").setValue(new_stats);
                        else
                            statsReference.child(userId).child("status").setValue("Hey there! I am using App di Chat");

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    public void onClick(View view) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        setResult(0, cameraIntent);
        startActivityForResult(cameraIntent, RESULT_LOAD_IMAGE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1, 1)
                    .start(this);
        }
            /*if(requestCode==CAMERA_REQUEST && resultCode==RESULT_OK)
            {

            }*/

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri croppedImageUri = CropImage.getActivityResult(data).getUri();
            StorageReference filepath = storageReference.child("profileimages").child(userId + ".jpeg");
            filepath.putFile(croppedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {
                        String download_url = task.getResult().getDownloadUrl().toString();
                        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("image").setValue(download_url);
                    } else {
                        Toast.makeText(SettingsActivity.this, "Error Storing Image", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.child("online").setValue("false");
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.child("online").setValue("true");
    }
}

