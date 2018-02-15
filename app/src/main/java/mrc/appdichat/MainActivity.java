package mrc.appdichat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ViewPager mViewPager;
    private TabPagerAdapter mPagerAdapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        toolbar = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("App di chat");

        mViewPager=(ViewPager) findViewById(R.id.main_view_pager);
        mPagerAdapter=new TabPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        tabLayout= (TabLayout) findViewById(R.id.main_tab_layout);
        tabLayout.setupWithViewPager(mViewPager);

        //tabLayout.setTabMode(TabLayout.MODE_FIXED);
        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent=new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(settingsIntent);
                break;

            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                Intent firstIntent = new Intent(MainActivity.this, FirstActivity.class);
                startActivity(firstIntent);
                break;

            case R.id.action_mute:
                return true;

            case R.id.action_users:
                Intent usersIntent=new Intent(MainActivity.this,AllUsersActivity.class);
                startActivity(usersIntent);
                break;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }return  true;
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent firstIntent = new Intent(MainActivity.this, FirstActivity.class);
            startActivity(firstIntent);
            finish();
        }
    }
}
