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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private TextInputLayout nameInput;
    private TextInputLayout emailInput;
    private TextInputLayout passInput;
    private TextInputLayout cpassInput;
    private Button btn;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);

        nameInput = (TextInputLayout) findViewById(R.id.sign_name_field);
        emailInput = (TextInputLayout) findViewById(R.id.sign_email_field);
        passInput = (TextInputLayout) findViewById(R.id.sign_pass_field);
        btn = (Button) findViewById(R.id.sign_register_button);
        cpassInput = (TextInputLayout) findViewById(R.id.sign_cpass_field);

        mAuth = FirebaseAuth.getInstance();


        toolbar = (Toolbar) findViewById(R.id.signup_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Signup");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signup_user();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.signup_app_bar_menu, menu);
        return true;
    }

    public void signup_user() {
        final String name = nameInput.getEditText().getText().toString();
        String mail = emailInput.getEditText().getText().toString();
        String password = passInput.getEditText().getText().toString();
        String cpassword = cpassInput.getEditText().getText().toString();

        if (!name.isEmpty() && !mail.isEmpty() && !password.isEmpty()) {

            if (!isValidMail(mail)) {
                Toast.makeText(SignupActivity.this, "Please Enter A Valid E-Mail Address", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(SignupActivity.this, "Password Should Be Atleast 6 Characters Long", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(cpassword)) {
                Toast.makeText(SignupActivity.this, "Passsword Doesn't Match", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.setMessage("Signing Up...");
                progressDialog.show();

                mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.hide();

                            insertInDatabse(name);

                            Intent mainIntent = new Intent(SignupActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            progressDialog.hide();
                            Toast.makeText(SignupActivity.this, "Signup Failed::Wrong Credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {
            progressDialog.hide();
            Toast.makeText(SignupActivity.this, "Please Enter Complete Details", Toast.LENGTH_SHORT).show();
        }

    }

    public void insertInDatabse(String name) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("image", "default");
        hashMap.put("name", name);
        hashMap.put("status", "Hey there! I am using App di Chat");
        mDatabase.setValue(hashMap);
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
