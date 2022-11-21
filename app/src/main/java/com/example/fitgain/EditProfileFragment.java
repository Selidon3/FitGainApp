package com.example.fitgain;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class EditProfileFragment extends Fragment {

//TODO CHANGING PASSWORD BUTTON WITH VERIFICATION!

    private static final String PASSWORD_PATTERN =
            "^(?!.*\\\\s)(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,}$";

    String UID;
    private FirebaseAuth mAuth;
    DatabaseReference fbBase;
    //DatabaseReference fbBaseTimes;
    FirebaseUser user;

    EditText nameEdit,currentWeight,targetWeight,password,validityPassword;
    RadioButton genderMale,genderFemale;
    Button changePassword, saveChanges;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_editprofile, container, false);

        nameEdit = v.findViewById(R.id.TXTEditName);
        currentWeight = v.findViewById(R.id.TXTEditCurrentWeight);
        targetWeight = v.findViewById(R.id.TXTEditTargetWeight);
        genderMale = v.findViewById(R.id.RADIOMale);
        genderFemale = v.findViewById(R.id.RADIOFemale);
        changePassword = v.findViewById(R.id.BTNEditChangePassword);
        saveChanges = v.findViewById(R.id.BTNEditSaveChanges);

        mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getCurrentUser().getUid();
        fbBase = FirebaseDatabase.getInstance().getReference("Users").child(UID.toString());
        user = mAuth.getCurrentUser();


//        fbBase.child("targetWeight").setValue("70");
//        fbBase.child("currentWeight").setValue("66");
//        fbBase.child("gender").setValue("male");

        fbBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nameEdit.setText(snapshot.child("name").getValue(String.class));
                targetWeight.setText(snapshot.child("targetWeight").getValue(String.class));
                currentWeight.setText(snapshot.child("currentWeight").getValue(String.class));
                if(snapshot.child("gender").getValue(String.class).equals("male")){
                    genderMale.setChecked(true);
                }else{
                    genderFemale.setChecked(true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_update_password, null);
                EditText passwordChange = view.findViewById(R.id.TXTChangePassword);
                EditText passwordConfirm = view.findViewById(R.id.TXTConfirmPassword);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(view);
                EditText oldPassView = view.findViewById(R.id.TXTOldPassword);


                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!passwordChange.getText().toString().isEmpty() && !passwordConfirm.getText().toString().isEmpty() && !oldPassView.getText().toString().isEmpty()){
                            if(passwordChange.getText().toString().equals(passwordConfirm.getText().toString())){
                                if(validPassword(passwordChange.getText().toString())){
                                    //change pass
                                    String oldPass = oldPassView.getText().toString();
                                    updatePassword(passwordChange.getText().toString(),oldPass);

                                }
                            }else{
                                //if not equal passwords
                                Toast.makeText(getActivity(), "Passwords Must Be Equal! Try Again!", Toast.LENGTH_SHORT).show();
                            }
                        }else // if fields empty
                            Toast.makeText(getActivity(), "Passwords Is Empty! Try Again!", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Close the dialog
                    }
                });
                builder.create();
                builder.show();
            }


        });

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
                    Toast.makeText(v.getContext(), "Changes saved", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(v.getContext(), "Fields missing", Toast.LENGTH_SHORT).show();
            }
        });



        return v;

    }

    public void updatePassword(String newPassChange,String oldPass) {

        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(),oldPass);
        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //Successfully reauthenticate! Begin update new password!
                user.updatePassword(newPassChange).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // changed pass
                        Toast.makeText(getActivity(), "Password Change Successfully!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { // failed to update
                        Toast.makeText(getActivity(), "Password Not Changed! Try Again!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed to authenticate with current pass
                Toast.makeText(getActivity(), "Current Password Is Incorrect! Try Again!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean validPassword(final String pass) {
        boolean ret = false;

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(pass);

        if(!matcher.matches()){
            Toast.makeText(getActivity(), "Password must contain a-z / A-Z / 1-9 / Symbols / length 8-20", Toast.LENGTH_SHORT).show();
        }
        else
            ret = true;

        return ret;
    }

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

}