package com.example.vooglestaff.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vooglestaff.R;
import com.example.vooglestaff.databinding.PhoneNumberItemLayoutBinding;

import java.util.ArrayList;


public class PhoneNoAdapter  extends RecyclerView.Adapter<PhoneNoAdapter.ViewHolder> {
    ArrayList<String> licensePlates;
    Context context;

    public PhoneNoAdapter(ArrayList<String> licensePlates, Context context) {
        this.licensePlates = licensePlates;
        this.context = context;
    }

    @NonNull
    @Override
    public PhoneNoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PhoneNumberItemLayoutBinding phoneNumberItemLayoutBinding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.phone_number_item_layout, parent, false);
        return new PhoneNoAdapter.ViewHolder(phoneNumberItemLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PhoneNoAdapter.ViewHolder holder, int position) {
        holder.phoneNumberItemLayoutBinding.textView.setText(this.licensePlates.get(position));

    }

    @Override
    public int getItemCount() {
        return this.licensePlates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        PhoneNumberItemLayoutBinding phoneNumberItemLayoutBinding;
        public ViewHolder(@NonNull PhoneNumberItemLayoutBinding itemView) {
            super(itemView.getRoot());
            phoneNumberItemLayoutBinding=itemView;
        }
    }
}
