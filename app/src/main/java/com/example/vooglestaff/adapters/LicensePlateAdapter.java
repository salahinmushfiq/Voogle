package com.example.vooglestaff.adapters;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vooglestaff.R;
import com.example.vooglestaff.databinding.LicensePlateItemLayoutBinding;

import java.util.ArrayList;


public class LicensePlateAdapter extends RecyclerView.Adapter<LicensePlateAdapter.ViewHolder> {
    //LicensePlateItemLayoutBinding licensePlateItemLayoutBinding;
    ArrayList<String>licensePlates;
    Context context;

    public LicensePlateAdapter(Context context, ArrayList<String> licensePlates) {
        this.licensePlates = licensePlates;
        Toast.makeText(context, "Constructor : "+this.licensePlates, Toast.LENGTH_SHORT).show();
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LicensePlateItemLayoutBinding licensePlateItemLayoutBinding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.license_plate_item_layout, parent, false);
        return new ViewHolder(licensePlateItemLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String licensePlate=licensePlates.get(position);
        Toast.makeText(context, "ONBIND : "+licensePlate, Toast.LENGTH_SHORT).show();
        holder.licensePlateItemLayoutBinding.textView.setText(licensePlate);
    }

    @Override
    public int getItemCount() {
        Toast.makeText(context, "size: "+licensePlates.size(), Toast.LENGTH_SHORT).show();
        return licensePlates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
          LicensePlateItemLayoutBinding licensePlateItemLayoutBinding;;
        public ViewHolder(@NonNull LicensePlateItemLayoutBinding licensePlateItemLayout) {
            super(licensePlateItemLayout.getRoot());
            licensePlateItemLayoutBinding=licensePlateItemLayout;
        }
    }
}
