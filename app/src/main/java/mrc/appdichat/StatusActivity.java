package mrc.appdichat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private TextInputLayout textInputLayout;
    private Button saveButton;
    private DatabaseReference databaseReference;
    String userId;
    ProgressDialog progressDialog;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        textInputLayout = (TextInputLayout) findViewById(R.id.status_input);
        String currStatus = getIntent().getStringExtra("status");
        textInputLayout.getEditText().setText(currStatus);
        toolbar = (Toolbar) findViewById(R.id.status_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Status");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        progressDialog = new ProgressDialog(this);
        saveButton = (Button) findViewById(R.id.status_savechangesbutton);
        //EditText et = (EditText)findViewById(R.id.inbox);
        //et.setSelection(et.getText().length());
        textInputLayout.getEditText().setSelection(textInputLayout.getEditText().getText().length());
        saveButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Updating Status...");
                progressDialog.show();
                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                databaseReference.child("status").setValue(textInputLayout.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.hide();
                            Toast.makeText(StatusActivity.this, "Status Updated Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StatusActivity.this, SettingsActivity.class);
                            startActivity(intent);
                            finish();
                            ;
                        } else {
                            progressDialog.hide();
                            Toast.makeText(StatusActivity.this, "Error Updating Status", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.status_app_bar_menu, menu);
        return true;
    }
}
