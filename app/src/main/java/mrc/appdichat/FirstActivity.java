package mrc.appdichat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class FirstActivity extends AppCompatActivity {

    private Button mSignButton;
    private Button mLoginButton;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        mSignButton = (Button) findViewById(R.id.signup_button);
        mLoginButton = (Button) findViewById(R.id.login_button);
        toolbar = (Toolbar) findViewById(R.id.first_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Welcome!");


        mSignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIntent = new Intent(FirstActivity.this, SignupActivity.class);
                startActivity(signIntent);
                //finish();
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(FirstActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                //finish();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.first_app_bar_menu, menu);
        return true;
    }
}
