package com.example.assignment_4.Holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment_4.databinding.HourItemBinding;

public class DayTimeWeatherItem extends RecyclerView.ViewHolder {
    public HourItemBinding binding;
    public DayTimeWeatherItem(HourItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
