package com.example.fitgain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ManageUsersAdapter extends RecyclerView.Adapter<ManageUsersAdapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;

    Context context;

    ArrayList<UserForManaging> list;

    public ManageUsersAdapter(Context context, ArrayList<UserForManaging> list,
                              RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.list = list;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recyclerview_usersitem,parent,false);
        return new MyViewHolder(v, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageUsersAdapter.MyViewHolder holder, int position) {

        UserForManaging userForManaging = list.get(position);
        holder.name.setText(userForManaging.getName());
        holder.email.setText(userForManaging.getEmail());
        holder.Gender.setText(userForManaging.getGender());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name, email, Gender;
        public MyViewHolder(@NonNull View itemView,RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            name = itemView.findViewById(R.id.RECYCLERVIEWNamePutPlace);
            email = itemView.findViewById(R.id.RECYCLERVIEWEmailPutPlace);
            Gender = itemView.findViewById(R.id.RECYCLERVIEWGenderPutPlace);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });


        }
    }
}
