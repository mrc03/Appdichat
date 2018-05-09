package mrc.appdichat;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by HP on 09-02-2018.
 */

public class Appdichat extends Application {
    private FirebaseUser user;
    private DatabaseReference usersDatabaseReference;
    @Override
    public void onCreate() {
        super.onCreate();
        user=FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        usersDatabaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid());

        usersDatabaseReference.child("online").onDisconnect().setValue("false");

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}
