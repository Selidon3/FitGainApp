package com.example.fitgain;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class History extends Fragment {

    String UID;
    private FirebaseAuth mAuth;
    DatabaseReference fbBase,fbBaseTimes;

    EditText pickedDateEditText;
    TextView noFoodForToday;
    Calendar calendar;
    public String pickedDateToSearch;
    RecyclerView recyclerView;
    HistoryAdapter myAdapter;
    ArrayList<UserHistoryData> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);


        mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getCurrentUser().getUid();
        fbBase = FirebaseDatabase.getInstance().getReference("Users").child(UID.toString());
        fbBaseTimes = FirebaseDatabase.getInstance().getReference("Users").child(UID.toString()).child("Dates");


        recyclerView = v.findViewById(R.id.RECYCLERVIEWItems);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        myAdapter = new HistoryAdapter(getContext(),list);
        recyclerView.setAdapter(myAdapter);

        pickedDateEditText = v.findViewById(R.id.TXTDateShow);
        noFoodForToday = v.findViewById(R.id.TXTNoHistoryToday);
        Calendar calendar = Calendar.getInstance();
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        pickedDateEditText.setText(currentDate);
        check();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String Format = "dd-MM-yyyy";
                SimpleDateFormat date = new SimpleDateFormat(Format, Locale.getDefault());
                //setting the date to the editText
                pickedDateEditText.setText(date.format(calendar.getTime()));
                //saving current picked date to search it the DB
                //System.out.println("DATE IS " + date.format(calendar.getTime()));
                pickedDateToSearch = date.format(calendar.getTime());
                list.clear();
                check();
            }
        };
        pickedDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setting the date when pressed the EditText of the date
                new DatePickerDialog(getActivity(),date,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        return v;
    }
    public void check(){
        fbBaseTimes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    for (DataSnapshot inner : snapshot.getChildren()) {
                        //if found todays date history
                        if (snapshot.getKey().toString().equals(pickedDateEditText.getText().toString())) {

                            //found todays history
                            //finding todays history but not counting the "FirstData" initiation! :)
                            if(!inner.child("type").getValue().equals("FirstData")){
                                UserHistoryData user = inner.getValue(UserHistoryData.class);
                                list.add(user);
                                noFoodForToday.setText("Your history is:");
                            }

                        }else{
                            if(list.size() == 0)
                                noFoodForToday.setText("No history for this date!");
                        }


                    }
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            };

        });
    }

}