package com.example.assignment_4.Holder;

import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment_4.databinding.DayforecastitemBinding;

public class Dayforholder extends RecyclerView.ViewHolder {
    public DayforecastitemBinding binding;
    public Dayforholder(DayforecastitemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
