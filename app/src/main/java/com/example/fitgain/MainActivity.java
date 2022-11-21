package com.example.fitgain;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button signUp,logIn;
    private FirebaseAuth mAuth;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(visibility == 0)
                    decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        mAuth = FirebaseAuth.getInstance();
        signUp = findViewById(R.id.BTNsignupGoTo);
        logIn = findViewById(R.id.BTNloginGoTo);

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember","");
        if(checkbox.equals("true")){
            Intent intent = new Intent(getApplicationContext(),MainScreen.class);
            startActivity(intent);
            finish();

        }else if(checkbox.equals("false")){
        }

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),logIn.class);
                startActivity(intent);
                //REMOVE THIS IF NEEDED!
                finish();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignUp.class);
                startActivity(intent);
                //REMOVE THIS IF NEEDED!
                finish();
            }
        });
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