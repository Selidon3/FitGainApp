package com.example.fitgain;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomePage extends Fragment {

    String UID;
    private FirebaseAuth mAuth;
    DatabaseReference fbBase,fbBaseTimes;

    ProgressBar pBar;
    TextView currentCaloriesPlace,weightCaloriesPlace,percentProgress,limitExceeded,noDataToday,targetWeight;
    String currentWeightFromDb,targetWeightFromDb;
    int sumOfCaloriesAte = 0;
    int calcCurrentWeight = 0;
    String text1;
    String text2;
    boolean genderDB = true; // false = female, true = male


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_page, container, false);

        mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getCurrentUser().getUid();
        fbBase = FirebaseDatabase.getInstance().getReference("Users").child(UID.toString());
        fbBaseTimes = FirebaseDatabase.getInstance().getReference("Users").child(UID.toString()).child("Dates");

        pBar = v.findViewById(R.id.progressBar);
        currentCaloriesPlace = v.findViewById(R.id.currentCalories);
        weightCaloriesPlace = v.findViewById(R.id.weightCalories);
        percentProgress = v.findViewById(R.id.currentPercent);
        limitExceeded = v.findViewById(R.id.progBarExceeded);
        noDataToday = v.findViewById(R.id.noDataToday);
        targetWeight = v.findViewById(R.id.targetWeight);

        //man : kg * 1 * 24
        //woman : kg * 0.9 * 24

        //Todays date in format of dd-mm-yyyy
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        fbBaseTimes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    sumOfCaloriesAte = 0;
                    for(DataSnapshot inner : snapshot.getChildren()){
                        sumOfCaloriesAte += Integer.parseInt(inner.child("calories").getValue().toString());
                    }
                    fbBase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            targetWeightFromDb = snapshot2.child("targetWeight").getValue(String.class);
                            targetWeight.setText("Your target weight is: "+ targetWeightFromDb +" kg");
                            //currentWeightFromDb = snapshot2.child("currentWeight").getValue(String.class);
                            if(snapshot2.child("gender").getValue(String.class).equals("male")){
                                genderDB = true;
                            }else{
                                genderDB = false;
                            }
                            if(genderDB){
                                calcCurrentWeight = Integer.parseInt(targetWeightFromDb)*1*24;
                                weightCaloriesPlace.setText(calcCurrentWeight+"");
                            }
                            else{
                                calcCurrentWeight = (int)(Double.parseDouble(targetWeightFromDb)*24*0.9);
                                weightCaloriesPlace.setText(calcCurrentWeight+"");
                            }


                            if (snapshot.getKey().equals(currentDate)) {
                                //found todays history
                                if(sumOfCaloriesAte == 0){
                                    pBar.setProgress(0);
                                    noDataToday.setVisibility(View.VISIBLE);
                                }else{
                                    noDataToday.setVisibility(View.GONE);
                                }

                                if (sumOfCaloriesAte >= calcCurrentWeight) {

                                    //Exceeded limit of todays calories
                                    currentCaloriesPlace.setText(weightCaloriesPlace.getText().toString());
                                    pBar.setProgress(100);
                                    percentProgress.setText("100%");
                                    limitExceeded.setVisibility(View.VISIBLE);
                                    if(sumOfCaloriesAte == 0){
                                        pBar.setProgress(0);
                                    }
                                } else {
                                    //current calorie cound is lower than my weight calorie count
                                    currentCaloriesPlace.setText(sumOfCaloriesAte + "");
                                    text1 = currentCaloriesPlace.getText().toString();
                                    text2 = weightCaloriesPlace.getText().toString();
                                    double number1 = Integer.parseInt(text1);
                                    double number2 = Integer.parseInt(text2);
                                    double progStatus = (number1 / number2) * 100;
                                    pBar.setProgress((int) progStatus);
                                    percentProgress.setText((int) progStatus + "%");
                                    limitExceeded.setVisibility(View.GONE);
                                }
                            }else{
                                // if no history of food today
                                //not needed coz of rows 97-101 and puttint to DB every day new create of food
//                                currentCaloriesPlace.setText("0");
//                                pBar.setProgress(0);
//                                percentProgress.setText("0%");
//                                noDataToday.setVisibility(View.VISIBLE);
//                                if(genderDB){
//                                    calcCurrentWeight = Integer.parseInt(targetWeightFromDb)*1*24;
//                                    weightCaloriesPlace.setText(calcCurrentWeight+"");
//                                }
//                                else{
//                                    calcCurrentWeight = (int)(Double.parseDouble(targetWeightFromDb)*24*0.9);
//                                    weightCaloriesPlace.setText(calcCurrentWeight+"");
//                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        @Override
        public void onCancelled (@NonNull DatabaseError error){

        }

        });


        return v;
    }
}