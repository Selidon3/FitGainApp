package com.example.fitgain;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AddFood extends Fragment {
    String UID;
    private FirebaseAuth mAuth;
    DatabaseReference fbBase,fbBaseTimes;
    Button addFood;
    EditText typeFood,caloriesFood;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_food, container, false);
        mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getCurrentUser().getUid();
        fbBase = FirebaseDatabase.getInstance().getReference("Users").child(UID.toString());
        fbBaseTimes = FirebaseDatabase.getInstance().getReference("Users").child(UID.toString()).child("Dates");

        addFood = v.findViewById(R.id.BTNAddFood);
        typeFood = v.findViewById(R.id.TXTAddFoodName);
        caloriesFood = v.findViewById(R.id.TXTAddFoodCalories);


        //Todays date in format of dd-mm-yyyy
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());


        addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!typeFood.getText().toString().isEmpty() && !caloriesFood.getText().toString().isEmpty()) {
                    if(caloriesFood.getText().toString().equals("0")){
                        Toast.makeText(getActivity(), "Calories can't be "+"0"+"!", Toast.LENGTH_SHORT).show();
                    }else{
                        Food foodAdd = new Food(typeFood.getText().toString(), caloriesFood.getText().toString());
                        fbBaseTimes.child(currentDate).push().setValue(foodAdd);
                        typeFood.setText("");
                        caloriesFood.setText("");
                        Toast.makeText(getActivity(), "Food Added Successfully!", Toast.LENGTH_SHORT).show();
                    }

                } else
                    Toast.makeText(getActivity(), "Empty Fields!", Toast.LENGTH_SHORT).show();

//                fbBaseTimes.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            if (snapshot.getKey().equals(currentDate)) {
//
//                            }
//
//                        }
//                    }


//                @Override
//                public void onCancelled (@NonNull DatabaseError error){
//
//                }
//
//                });
            }
        });


        return v;
    }
}