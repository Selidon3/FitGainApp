package com.example.fitgain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public static EditText name,email,password;
    Button signUp;
    private View decorView;
    TextView goToLogIn;

    private static final String PASSWORD_PATTERN =
            "^(?!.*\\\\s)(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_sign_up);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(visibility == 0)
                    decorView.setSystemUiVisibility(hideSystemBars());
            }
        });


        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.TXTRegName);
        email = findViewById(R.id.TXTRegEmail);
        password = findViewById(R.id.TXTRegPassword);
        signUp = findViewById(R.id.BTNRegSignUp);
        goToLogIn = findViewById(R.id.TXTSignUpGoToLogIn);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validPassword(password.getText().toString()) && validName(name.getText().toString()) && validEmail(email.getText().toString())){
                    RegisterUser();
                }
            }
        });

        goToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),logIn.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private void RegisterUser(){
        String emailS = email.getText().toString();
        String nameS = name.getText().toString();
        String passwordS = password.getText().toString();


        mAuth.createUserWithEmailAndPassword(emailS,passwordS).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //email verify
                    FirebaseUser fbUser = mAuth.getCurrentUser();
                    fbUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(SignUp.this, "Email verify sent", Toast.LENGTH_LONG).show();

                        }
                    });

                    User user = new User(nameS,emailS);
                    FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUp.this, "Register successfully complete", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(),logIn.class);
                                startActivity(intent);
                                finish();
                            }else
                            {
                                Toast.makeText(SignUp.this, "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else
                {
                    mAuth.fetchSignInMethodsForEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            boolean newUser = task.getResult().getSignInMethods().isEmpty();
                            if(!newUser)
                                Toast.makeText(SignUp.this, "Email is already registered!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //Toast.makeText(SignUp.this, "User not created", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static boolean validPassword(final String pass) {
        boolean ret = false;

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(pass);

            if(!matcher.matches()){
                password.setError("Password must contain a-z / A-Z / 1-9 / Symbols / length 8-20");
                password.requestFocus();
            }
            else
                ret = true;

        return ret;
    }

    public static boolean validName(final String nameS) {
        boolean ret = false;

        boolean res = nameS.matches("[A-Z]+[a-z]+");
        if(res && nameS.length()>=3){
            ret = true;
        }else
        {
            name.setError("Name must begin with upper letter and contain only characters (length at least 3 characters)");
            name.requestFocus();
        }

        return ret;
    }

    public static boolean validEmail(final String emailS) {
        boolean ret = false;

        boolean res = emailS.matches("[a-zA-Z0-9]+[@]+[a-zA-Z]+[.]+[a-z]+");
        if(res){
            ret = true;
        }else
        {
            email.setError("Email is incorrect");
            email.requestFocus();
        }

        return ret;
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