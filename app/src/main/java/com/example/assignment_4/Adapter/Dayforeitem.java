package com.example.assignment_4.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment_4.Helper.ColorMaker;
import com.example.assignment_4.Holder.DayTimeWeatherItem;
import com.example.assignment_4.Holder.Dayforholder;
import com.example.assignment_4.Model.DayItem;
import com.example.assignment_4.databinding.DayforecastitemBinding;
import com.example.assignment_4.databinding.HourItemBinding;

import java.util.List;

public class Dayforeitem extends RecyclerView.Adapter<Dayforholder> {

    private final List<DayItem> list;
    private boolean isCelsius;
    public Dayforeitem(List<DayItem>  list, boolean isCelsius) {
        this.list = list;
        this.isCelsius = isCelsius;
    }

    @NonNull
    @Override
    public Dayforholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DayforecastitemBinding binding = DayforecastitemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new Dayforholder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Dayforholder holder, int position) {
        holder.binding.datetime.setText(list.get(position).getDaytime());
        holder.binding.tvtemp.setText(list.get(position).getHightp() + "/" + list.get(position).getLawtp());
        holder.binding.tvcondition.setText(list.get(position).getCon());
        holder.binding.tvprecipprob.setText(list.get(position).getPrep());
        holder.binding.tvuv.setText(list.get(position).getUv());
        holder.binding.tvmorningtemp.setText(list.get(position).getMortemp());
        holder.binding.tvnoontemp.setText(list.get(position).getNoontemp());
        holder.binding.tvevetemp.setText(list.get(position).getEvetemp());
        holder.binding.tvnighttemp.setText(list.get(position).getNighttemp());
        holder.binding.ivweathimg.setImageResource(list.get(position).getId(list.get(position).getIcon()));

        ColorMaker.setColorGradient(null,holder.binding.daycard, list.get(position).getTemp(), isCelsius ? "C" : "F");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
