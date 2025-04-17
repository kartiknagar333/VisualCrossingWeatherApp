package com.example.assignment_4.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment_4.Holder.DayTimeWeatherItem;
import com.example.assignment_4.Model.HourItem;
import com.example.assignment_4.Model.Weather;
import com.example.assignment_4.databinding.HourItemBinding;

import java.util.List;

public class DayTimeAdapter extends RecyclerView.Adapter<DayTimeWeatherItem> {

    private final List<HourItem> hourItemList;
    public DayTimeAdapter(List<HourItem> hourItemList) {
        this.hourItemList = hourItemList;
    }

    @NonNull
    @Override
    public DayTimeWeatherItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HourItemBinding binding = HourItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DayTimeWeatherItem(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DayTimeWeatherItem holder, int position) {
        holder.binding.tvday.setText(hourItemList.get(position).getDay());
        holder.binding.tvtime.setText(hourItemList.get(position).getTime());
        holder.binding.ivweathericon.setImageResource(Weather.getId(hourItemList.get(position).getIcon()));
        holder.binding.tvtemp.setText(hourItemList.get(position).getTemp());
        holder.binding.tvweathertype.setText(hourItemList.get(position).getType());

    }

    @Override
    public int getItemCount() {
        return hourItemList.size();
    }
}
