package com.example.wheatherapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Wheather_Adapter extends RecyclerView.Adapter<Wheather_Adapter.ViewHolder> {
    private Context context;
    private ArrayList<wheather_model> list;

    public Wheather_Adapter(Context context, ArrayList<wheather_model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.weather,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        wheather_model wheatherModel=list.get(position);
        Picasso.get().load("http:"+wheatherModel.getIcon()).into(holder.condition);
        holder.temperature.setText(wheatherModel.getTemprature()+"°C");
        holder.windspeed.setText(wheatherModel.getWindspeed()+"km/h");
        SimpleDateFormat input=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output=new SimpleDateFormat("hh:mm aa");
        try {
            Date t=input.parse(wheatherModel.getTime());
            holder.time.setText(output.format(t));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView time,temperature,windspeed;
        ImageView condition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time=itemView.findViewById(R.id.time);
            temperature=itemView.findViewById(R.id.temperature);
            windspeed=itemView.findViewById(R.id.wind_speed);
            condition=itemView.findViewById(R.id.wheather_condition);
        }
    }

}

