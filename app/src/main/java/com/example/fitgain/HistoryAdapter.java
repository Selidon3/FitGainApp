package com.example.fitgain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    Context context;

    ArrayList<UserHistoryData> list;

    public HistoryAdapter(Context context, ArrayList<UserHistoryData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recyclerview_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.MyViewHolder holder, int position) {

        UserHistoryData historyData = list.get(position);
        holder.foodName.setText(historyData.getType());
        holder.foodCalories.setText(historyData.getCalories());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView foodName, foodCalories;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foodName = itemView.findViewById(R.id.FoodPutPlace);
            foodCalories = itemView.findViewById(R.id.CaloriesPutPlace);


        }
    }
}
