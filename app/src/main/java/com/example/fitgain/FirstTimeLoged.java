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
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class FirstTimeLoged extends AppCompatActivity {

    String UID;
    private FirebaseAuth mAuth;
    DatabaseReference fbBase;
    private View decorView;
    EditText currentWeight,targerWeight;
    RadioButton radioMale,radioFemale;
    Button saveContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_first_time_loged);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(visibility == 0)
                    decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        mAuth = FirebaseAuth.getInstance();

        UID = mAuth.getCurrentUser().getUid();
        fbBase = FirebaseDatabase.getInstance().getReference("Users").child(UID.toString());
        currentWeight = findViewById(R.id.TXTRegCurrentWeight);
        targerWeight = findViewById(R.id.TXTRegTargetWeight);
        radioFemale = findViewById(R.id.RADIORegFemale);
        radioMale = findViewById(R.id.RADIORegMale);
        saveContinue = findViewById(R.id.BTNRegContinue);

        fbBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.child("firstTimeLogIn").getValue(String.class);
                if(value.equals("0")){
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        saveContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currentWeight.getText().toString().isEmpty() && !targerWeight.getText().toString().isEmpty()
                        && (radioFemale.isChecked()||radioMale.isChecked())){
                    fbBase.child("targetWeight").setValue(targerWeight.getText().toString());
                    fbBase.child("currentWeight").setValue(currentWeight.getText().toString());
                    if(radioMale.isChecked())
                        fbBase.child("gender").setValue("male");
                    else
                        fbBase.child("gender").setValue("female");
                    fbBase.child("firstTimeLogIn").setValue("0");
//                    try
//                    {
//                        Thread.sleep(1000);
//                    }
//                    catch(InterruptedException ex){}
                    Intent intent = new Intent(getApplicationContext(),MainScreen.class);
                    startActivity(intent);
                    finish();
                }
                else
                    Toast.makeText(v.getContext(), "Missing Fields", Toast.LENGTH_LONG).show();
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