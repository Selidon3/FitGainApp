package com.example.fitgain;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class ManageUsers extends Fragment implements RecyclerViewInterface {

    String UID;
    private FirebaseAuth mAuth;
    DatabaseReference fbBase,fbBaseTimes, fbBaseAll;

    RecyclerView recyclerView;
    ManageUsersAdapter myAdapter;
    ArrayList<UserForManaging> list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manage_users, container, false);


        mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getCurrentUser().getUid();
        fbBaseAll = FirebaseDatabase.getInstance().getReference("Users");
        fbBase = FirebaseDatabase.getInstance().getReference("Users").child(UID.toString());
        fbBaseTimes = FirebaseDatabase.getInstance().getReference("Users").child(UID.toString()).child("Dates");


        recyclerView = v.findViewById(R.id.RECYCLERVIEWUsersItem);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        myAdapter = new ManageUsersAdapter(getContext(),list, this);
        recyclerView.setAdapter(myAdapter);




        fbBaseAll.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //System.out.println("THIS IS SNAPSHOT " + snapshot);
                    if(!UID.equals(snapshot.getKey().toString())){
                        // if the account has an "gender" value in it
                        if(snapshot.child("firstTimeLogIn").getValue().toString().equals("0")){
                            UserForManaging userForManaging = new UserForManaging(snapshot.child("name").getValue().toString(),snapshot.child("email").getValue().toString(),snapshot.child("gender").getValue().toString(),snapshot.getKey().toString());
                            list.add(userForManaging);
                        }else // if the account has not "gender" value in it
                            if(snapshot.child("firstTimeLogIn").getValue().toString().equals("1")){
                                UserForManaging userForManaging = new UserForManaging(snapshot.child("name").getValue().toString(),snapshot.child("email").getValue().toString(),"Undefined",snapshot.getKey().toString());
                                list.add(userForManaging);
                            }
                    }
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


            fbBase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.child("toRemove").getValue().toString().equals("0")){

//                        try
//                        {
//                            Thread.sleep(2000);
                            fbBaseAll.child(snapshot.child("toRemove").getValue().toString()).setValue(null);
                            fbBase.child("toRemove").setValue("0");
//                        }
//                        catch(InterruptedException ex){}
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        return v;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(),ManageUserEdit.class);
        intent.putExtra("NAME", list.get(position).getName());
        intent.putExtra("EMAIL", list.get(position).getEmail());
        //intent.putExtra("GENDER", list.get(position).getGender());
        intent.putExtra("UID", list.get(position).getUID());

        startActivity(intent);
    }
}