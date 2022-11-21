package com.example.fitgain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordReset extends AppCompatActivity {
    EditText email;
    Button reset;
    FirebaseAuth mAuth;
    private View decorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_password_reset);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(visibility == 0)
                    decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        email = findViewById(R.id.TXTPasswordResetEmail);
        reset = findViewById(R.id.BTNResetPasswordSend);
        mAuth = FirebaseAuth.getInstance();

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }
    private void resetPassword(){
        String emailS = email.getText().toString().trim();

        if(emailS.isEmpty()){
            email.setError("Email is required");
            email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(emailS).matches()){
            email.setError("Provide valid email!");
            email.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(emailS).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(PasswordReset.this, "Check email to reset password!", Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    Toast.makeText(PasswordReset.this, "Try again! Something wrong happened!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(PasswordReset.this, "Email may not exist!", Toast.LENGTH_SHORT).show();
                }
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