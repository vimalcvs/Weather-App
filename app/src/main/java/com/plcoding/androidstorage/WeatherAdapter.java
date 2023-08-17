package com.plcoding.androidstorage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<ModelWeather> list;

    public WeatherAdapter(Context context, ArrayList<ModelWeather> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelWeather modelWeather = list.get(position);
        if (modelWeather != null) {
            Glide.with(context)
                    .load("http:" + modelWeather.getIcon())
                    .into(holder.cicon);

            holder.itemView.setBackgroundColor(0000000);
            holder.wSpeed.setText(modelWeather.getWSpeed() + "km/h");
            holder.temp.setText(modelWeather.getTemp() + "°C");
            holder.humid.setText(modelWeather.getHumidity() + "%");
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            SimpleDateFormat output = new SimpleDateFormat("hh:mm aa ");

            try {
                Date t = input.parse(modelWeather.getTime());
                holder.time.setText(output.format(t));
            } catch (ParseException e) {
                e.printStackTrace();

            }
        } else {
            Log.e("WAdapter", "Model object at position " + position + " is null");
        }


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView time;
        TextView temp;
        TextView humid;
        TextView wSpeed;
        ImageView cicon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            temp = itemView.findViewById(R.id.vtemp);
            humid = itemView.findViewById(R.id.humid);
            wSpeed = itemView.findViewById(R.id.wSpeed);
            cicon = itemView.findViewById(R.id.cicon);

        }


    }


}
