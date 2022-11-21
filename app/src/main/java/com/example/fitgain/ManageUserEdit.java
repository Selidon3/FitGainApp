package com.example.fitgain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageUserEdit extends AppCompatActivity {

    //private static final String PASSWORD_PATTERN =
            //"^(?!.*\\\\s)(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,}$";

    private View decorView;
    String UID;
    private FirebaseAuth mAuth;
    DatabaseReference fbBase;
    FirebaseUser user;

    EditText nameEdit,currentWeight,targetWeight,email;
    RadioButton genderMale,genderFemale;
    Button removeUser, saveChanges,passwordChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_manage_user_edit);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(visibility == 0)
                    decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        nameEdit = findViewById(R.id.TXTManageEditName);
        email = findViewById(R.id.TXTManageEditEmail);
        currentWeight = findViewById(R.id.TXTManageEditCurrentWeight);
        targetWeight = findViewById(R.id.TXTManageEditTargetWeight);
        genderMale = findViewById(R.id.RADIOManageMale);
        genderFemale = findViewById(R.id.RADIOManageFemale);
        saveChanges = findViewById(R.id.BTNManageEditChanges);
        removeUser = findViewById(R.id.BTNRemoveUser);
        passwordChange = findViewById(R.id.BTNManageChangePassword);

        String UIDIntent = getIntent().getStringExtra("UID");

        mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getCurrentUser().getUid();
        fbBase = FirebaseDatabase.getInstance().getReference("Users").child(UIDIntent.toString());
        user = mAuth.getCurrentUser();
        //UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email.getText().toString());


//        fbBase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.child("firstTimeLogIn").getValue().toString().equals("0")){
//                    nameEdit.setText(dataSnapshot.child("name").getValue().toString());
//                    email.setText(dataSnapshot.child("email").getValue().toString());
//                    targetWeight.setText(dataSnapshot.child("targetWeight").getValue(String.class));
//                    currentWeight.setText(dataSnapshot.child("currentWeight").getValue(String.class));
//                    if(dataSnapshot.child("gender").getValue(String.class).equals("male")){
//                        genderMale.setChecked(true);
//                    }else{
//                        genderFemale.setChecked(true);
//                    }
//                }else{
//                    nameEdit.setText(dataSnapshot.child("name").getValue().toString());
//                    email.setText(dataSnapshot.child("email").getValue().toString());
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

            nameEdit.setText(getIntent().getStringExtra("NAME").toString());
            email.setText(getIntent().getStringExtra("EMAIL").toString());

        removeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                fbBase.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        startActivity(intent);
//                        finish();
//                    }
//                });
                FirebaseDatabase.getInstance().getReference("Users").child(UID.toString()).child("toRemove").setValue(UIDIntent);
                finish();

            }
        });

//        passwordChange.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_update_password, null);
//                EditText passwordChange = view.findViewById(R.id.TXTChangePassword);
//                EditText passwordConfirm = view.findViewById(R.id.TXTConfirmPassword);
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
//                builder.setView(view);
//                EditText oldPassView = view.findViewById(R.id.TXTOldPassword);
//
//
//                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if(!passwordChange.getText().toString().isEmpty() && !passwordConfirm.getText().toString().isEmpty() && !oldPassView.getText().toString().isEmpty()){
//                            if(passwordChange.getText().toString().equals(passwordConfirm.getText().toString())){
//                                if(validPassword(passwordChange.getText().toString())){
//                                    //change pass
//                                    String oldPass = oldPassView.getText().toString();
//                                    updatePassword(passwordChange.getText().toString(),oldPass);
//
//                                }
//                            }else{
//                                //if not equal passwords
//                                Toast.makeText(ManageUserEdit.this, "Passwords Must Be Equal! Try Again!", Toast.LENGTH_SHORT).show();
//                            }
//                        }else // if fields empty
//                            Toast.makeText(ManageUserEdit.this, "Passwords Is Empty! Try Again!", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //Close the dialog
//                    }
//                });
//                builder.create();
//                builder.show();
//            }
//        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validName(nameEdit.getText().toString()) && !currentWeight.getText().toString().isEmpty() && !targetWeight.getText().toString().isEmpty()) {
                    fbBase.child("targetWeight").setValue(targetWeight.getText().toString());
                    fbBase.child("currentWeight").setValue(currentWeight.getText().toString());
                    fbBase.child("name").setValue(nameEdit.getText().toString());
                    if (genderFemale.isChecked())
                        fbBase.child("gender").setValue("female");
                    else
                        fbBase.child("gender").setValue("male");
                    fbBase.child("firstTimeLogIn").setValue("0");
                    Toast.makeText(v.getContext(), "Changes saved", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                    Toast.makeText(v.getContext(), "Fields missing", Toast.LENGTH_SHORT).show();
            }
        });

    }

//    public void updatePassword(String newPassChange,String oldPass) {
//
//        AuthCredential authCredential = EmailAuthProvider.getCredential(email.getText().toString(),oldPass);
//
//        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                //Successfully reauthenticate! Begin update new password!
//                user.updatePassword(newPassChange).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        // changed pass
//                        Toast.makeText(ManageUserEdit.this, "Password Change Successfully!", Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) { // failed to update
//                        Toast.makeText(ManageUserEdit.this, "Password Not Changed! Try Again!", Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                //failed to authenticate with current pass
//                Toast.makeText(ManageUserEdit.this, "Current Password Is Incorrect! Try Again!", Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    public boolean validPassword(final String pass) {
//        boolean ret = false;
//
//        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
//        Matcher matcher = pattern.matcher(pass);
//
//        if(!matcher.matches()){
//            Toast.makeText(this, "Password must contain a-z / A-Z / 1-9 / Symbols / length 8-20", Toast.LENGTH_SHORT).show();
//        }
//        else
//            ret = true;
//
//        return ret;
//    }

    public boolean validName(final String nameS) {
        boolean ret = false;

        boolean res = nameS.matches("[A-Z]+[a-z]+");
        if(res && nameS.length()>=3){
            ret = true;
        }else
        {
            nameEdit.setError("Name must begin with upper letter and contain only characters (length at least 3 characters)");
            nameEdit.requestFocus();
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