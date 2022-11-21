package com.example.fitgain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class logIn extends AppCompatActivity {
    TextView resetPasswordView,goToSignUp;
    EditText email,password;
    private FirebaseAuth mAuth;
    Button logIn;
    CheckBox remember;
    public String UID;
    private View decorView;
    DatabaseReference fbBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_log_in);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(visibility == 0)
                    decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        logIn = findViewById(R.id.BTNlogIn);
        email = findViewById(R.id.TXTEmail);
        password = findViewById(R.id.TXTPassword);
        remember = findViewById(R.id.rememberMe);
        resetPasswordView = findViewById(R.id.TXTPasswordResetView);
        goToSignUp = findViewById(R.id.TXTLogInGoToSignUp);
        mAuth = FirebaseAuth.getInstance();
        fbBase = FirebaseDatabase.getInstance().getReference("Users");

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember","");
        if(checkbox.equals("true")){
            UID = mAuth.getCurrentUser().getUid();
            Intent intent = new Intent(getApplicationContext(),MainScreen.class);
            intent.putExtra("UID",UID);
            startActivity(intent);
            finish();

        }else if(checkbox.equals("false")){
            //Toast.makeText(this, "Please Log In", Toast.LENGTH_SHORT).show();
        }

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbBase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
                                if(snapshot.child("email").getValue().toString().equals(email.getText().toString())){

                                    mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                FirebaseUser fbUser = mAuth.getCurrentUser();

                                                if(fbUser.isEmailVerified()){
                                                    UID = mAuth.getCurrentUser().getUid();
                                                    Intent intent = new Intent(getApplicationContext(),MainScreen.class);
                                                    intent.putExtra("UID",UID);
                                                    startActivity(intent);
                                                    finish();
                                                }else{
                                                    Toast.makeText(logIn.this, "Please verify your Email", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else
                                                Toast.makeText(logIn.this, "Email or Password is not correct!", Toast.LENGTH_LONG).show();
                                        }
                                    });/////
                                }
                            }else
                                Toast.makeText(logIn.this, "Empty fields!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    SharedPreferences pref = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("remember","true");
                    editor.apply();
                    //Toast.makeText(logIn.this, "Checked", Toast.LENGTH_SHORT).show();
                }
                else if(!buttonView.isChecked()){
                    SharedPreferences pref = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("remember","false");
                    editor.apply();
                    //Toast.makeText(logIn.this, "Unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        resetPasswordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),PasswordReset.class);
                startActivity(intent);
            }
        });

        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignUp.class);
                startActivity(intent);
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