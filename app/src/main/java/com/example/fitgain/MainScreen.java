package com.example.fitgain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MainScreen extends AppCompatActivity {

    private View decorView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    String UID;
    private FirebaseAuth mAuth;
    DatabaseReference fbBase,fbBaseRemove;
    TextView emailPutHeader;
    TextView namePutHeader;
    MenuItem itemManager;
    DatabaseReference fbBaseTimes;
    List<String> days = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main_screen);
        //status bar + nav bar hide (Fullscreen)
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(visibility == 0)
                    decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        mAuth = FirebaseAuth.getInstance();
        //Getting UID of the user
        if(getIntent().getExtras() != null){
            UID = (String) getIntent().getSerializableExtra("UID");
        }else {
            UID = mAuth.getCurrentUser().getUid();
        }
        //$$

        fbBase = FirebaseDatabase.getInstance().getReference("Users").child(UID.toString());
        emailPutHeader = (TextView)findViewById(R.id.TXTEmailPut);
        namePutHeader = (TextView)findViewById(R.id.TXTNamePut);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.menu_Open,
                R.string.menu_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        itemManager = navigationView.getMenu().getItem(3);
        View headerView = navigationView.getHeaderView(0);
        emailPutHeader = headerView.findViewById(R.id.TXTEmailPut);
        namePutHeader = headerView.findViewById(R.id.TXTNamePut);
        fbBaseTimes = FirebaseDatabase.getInstance().getReference("Users").child(UID.toString()).child("Dates");

//      creating first of the day data witch is "0" to calories || *костыль
        fbBaseTimes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        days.add(snapshot.getKey());
                }
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                if(!days.contains(currentDate)){
                    Food foodAdd = new Food("FirstData", "0");
                    fbBaseTimes.child(currentDate).push().setValue(foodAdd);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //####
        // Read from the database
        fbBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.child("firstTimeLogIn").getValue(String.class);
                String namePut = dataSnapshot.child("name").getValue(String.class);
                String emailPut = dataSnapshot.child("email").getValue(String.class);
                String manager = dataSnapshot.child("manager").getValue(String.class);
                emailPutHeader.setText(emailPut);
                namePutHeader.setText("Welcome " + namePut + "!");
                //show manage users button if user is manager
                if(manager.equals("1")){
                    itemManager.setVisible(true);
                }
                else
                    itemManager.setVisible(false);

                // CONTINUE FIRST TIME ENTRY (User Input continue)
                if(value.equals("1")){
                    Intent intent = new Intent(getApplicationContext(),FirstTimeLoged.class);
                    startActivity(intent);

//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                    new FirstTimeEnter()).commit();
                    //Toast.makeText(MainScreen.this, "WE MADE IT", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        MenuItem itemHome = navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomePage()).commit();
//
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.nav_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new HomePage()).commit();
                        //Toast.makeText(MainScreen.this, "Home Pressed", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.nav_add:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new AddFood()).commit();
                        //Toast.makeText(MainScreen.this, "Add Pressed", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.nav_history:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new History()).commit();
                        //Toast.makeText(MainScreen.this, "History Pressed", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.nav_settings:

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new EditProfileFragment()).commit();
                        //Toast.makeText(MainScreen.this, "Settings Pressed", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_manage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new ManageUsers()).commit();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.nav_logout:

                        //Checkbox remember deactivation
                        SharedPreferences pref = getSharedPreferences("checkbox", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("remember","false");
                        editor.apply();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                }

                return true;
            }
        });


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(hasFocus){
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }
    private int hideSystemBars(){
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
}