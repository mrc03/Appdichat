package mrc.appdichat;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ViewPager mViewPager;
    private TabPagerAdapter mPagerAdapter;
    private TabLayout tabLayout;
    private boolean doubleBackToExitPressedOnce=false;

    private DatabaseReference usersDatabaseReference;

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

        usersDatabaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid());

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
                //setContentView(R.layout.activity_login);
                break;

            case R.id.action_search:
                /*final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item.findItem(R.id.action_search));
                SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));*/
                break;


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
        else
        {
            usersDatabaseReference.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        usersDatabaseReference.child("online").setValue("false");
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to EXIT", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
