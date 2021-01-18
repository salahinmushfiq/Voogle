package com.example.voogle.Adapters;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voogle.Fragments.TestMapsFragment;
import com.example.voogle.Functions.MapClick;
import com.example.voogle.PojoClasses.Bus;
import com.example.voogle.R;
import com.example.voogle.databinding.BusButtonLayoutBinding;
import com.example.voogle.databinding.RouteButtonItemLayoutBinding;

import java.util.ArrayList;

public class BusButtonAdapter extends RecyclerView.Adapter<BusButtonAdapter.ViewHolder> {

    FragmentActivity context;
    ArrayList<Bus>busList;
    private MapClick mapClick;;

    public BusButtonAdapter(FragmentActivity context, ArrayList<Bus> busList, MapClick mapClick) {
        this.context = context;
        this.busList = busList;
        this.mapClick=mapClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BusButtonLayoutBinding busButtonItemLayoutBinding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.bus_button_layout, parent, false);
        return new ViewHolder(busButtonItemLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.busButtonItemLayoutBinding.routeBtn.setText("Route No. "+String.valueOf(busList.get(position).getRoute_no()));
        if(busList.get(position).getGroupId()==11)
        {
            holder.busButtonItemLayoutBinding.routeBtn.setBackgroundColor( ContextCompat.getColor(holder.busButtonItemLayoutBinding.getRoot().getContext(), R.color.rounded_rectangle_2_copy_2_color)) ;
            holder.busButtonItemLayoutBinding.routeBtn.setText(busList.get(position).getGroupName());
//        holder.busButtonItemLayoutBinding.busGroupNameTV.setText(busList.get(position).getGroupName());
//        holder.busButtonItemLayoutBinding.routeBtn.setVisibility(View.GONE);
//        holder.busButtonItemLayoutBinding.busGroupNameTV.setOnClickListener(new View.OnClickListener() {
            holder.busButtonItemLayoutBinding.routeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapClick.onClick(String.valueOf(busList.get(position).getRoute_no()));
                    mapClick.onClick(String.valueOf(busList.get(position).getRoute_no()),String.valueOf(busList.get(position).getGroupId()));
                    Log.d("getBusList: ","Route No.:"+busList.get(position).getRoute_no());
                }
            });
        }
        if(busList.get(position).getGroupId()==12)
        {
            holder.busButtonItemLayoutBinding.routeBtn.setBackgroundColor( ContextCompat.getColor(holder.busButtonItemLayoutBinding.getRoot().getContext(), R.color.rounded_rectangle_2_color)) ;
            holder.busButtonItemLayoutBinding.routeBtn.setText(busList.get(position).getGroupName());
//        holder.busButtonItemLayoutBinding.busGroupNameTV.setText(busList.get(position).getGroupName());
//        holder.busButtonItemLayoutBinding.routeBtn.setVisibility(View.GONE);
//        holder.busButtonItemLayoutBinding.busGroupNameTV.setOnClickListener(new View.OnClickListener() {
            holder.busButtonItemLayoutBinding.routeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapClick.onClick(String.valueOf(busList.get(position).getRoute_no()));
                    mapClick.onClick(String.valueOf(busList.get(position).getRoute_no()),String.valueOf(busList.get(position).getGroupId()));
                    Log.d("getBusList: ","Route No.:"+busList.get(position).getRoute_no());
                }
            });
        }
        if(busList.get(position).getGroupId()==16)
        {
            holder.busButtonItemLayoutBinding.routeBtn.setBackgroundColor( ContextCompat.getColor(holder.busButtonItemLayoutBinding.getRoot().getContext(), R.color.Color_BlueViolet)) ;
            holder.busButtonItemLayoutBinding.routeBtn.setText(busList.get(position).getGroupName());
//        holder.busButtonItemLayoutBinding.busGroupNameTV.setText(busList.get(position).getGroupName());
//        holder.busButtonItemLayoutBinding.routeBtn.setVisibility(View.GONE);
//        holder.busButtonItemLayoutBinding.busGroupNameTV.setOnClickListener(new View.OnClickListener() {
            holder.busButtonItemLayoutBinding.routeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapClick.onClick(String.valueOf(busList.get(position).getRoute_no()));
                    mapClick.onClick(String.valueOf(busList.get(position).getRoute_no()),String.valueOf(busList.get(position).getGroupId()));
                    Log.d("getBusList: ","Route No.:"+busList.get(position).getRoute_no());
                }
            });
        }
        if(busList.get(position).getGroupId()==17)
        {
            holder.busButtonItemLayoutBinding.routeBtn.setBackgroundColor( ContextCompat.getColor(holder.busButtonItemLayoutBinding.getRoot().getContext(), R.color.Color_Tomato)) ;
            holder.busButtonItemLayoutBinding.routeBtn.setText(busList.get(position).getGroupName());

//        holder.busButtonItemLayoutBinding.busGroupNameTV.setText(busList.get(position).getGroupName());
//        holder.busButtonItemLayoutBinding.routeBtn.setVisibility(View.GONE);
//        holder.busButtonItemLayoutBinding.busGroupNameTV.setOnClickListener(new View.OnClickListener() {
            holder.busButtonItemLayoutBinding.routeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapClick.onClick(String.valueOf(busList.get(position).getRoute_no()));
                    mapClick.onClick(String.valueOf(busList.get(position).getRoute_no()),String.valueOf(busList.get(position).getGroupId()));

                }
            });
        }
        if(busList.get(position).getGroupId()==0)
        {
            holder.busButtonItemLayoutBinding.routeBtn.setBackgroundColor( ContextCompat.getColor(holder.busButtonItemLayoutBinding.getRoot().getContext(), R.color.Color_SlateBlue)) ;
            holder.busButtonItemLayoutBinding.routeBtn.setText(busList.get(position).getGroupName());
//        holder.busButtonItemLayoutBinding.busGroupNameTV.setText(busList.get(position).getGroupName());
//        holder.busButtonItemLayoutBinding.routeBtn.setVisibility(View.GONE);
//        holder.busButtonItemLayoutBinding.busGroupNameTV.setOnClickListener(new View.OnClickListener() {
            holder.busButtonItemLayoutBinding.routeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapClick.onClick(String.valueOf(busList.get(position).getRoute_no()));
                    mapClick.onClick(String.valueOf(busList.get(position).getRoute_no()),String.valueOf(busList.get(position).getGroupId()));
                    Log.d("getBusList: ","Route No.:"+busList.get(position).getRoute_no());
                }
            });
        }
        if(busList.get(position).getGroupId()==9)
        {
            holder.busButtonItemLayoutBinding.routeBtn.setBackgroundColor( ContextCompat.getColor(holder.busButtonItemLayoutBinding.getRoot().getContext(), R.color.rounded_rectangle_2_copy_color)) ;
            holder.busButtonItemLayoutBinding.routeBtn.setText(busList.get(position).getGroupName());
//        holder.busButtonItemLayoutBinding.busGroupNameTV.setText(busList.get(position).getGroupName());
//        holder.busButtonItemLayoutBinding.routeBtn.setVisibility(View.GONE);
//        holder.busButtonItemLayoutBinding.busGroupNameTV.setOnClickListener(new View.OnClickListener() {
            holder.busButtonItemLayoutBinding.routeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapClick.onClick(String.valueOf(busList.get(position).getRoute_no()));
                    Log.d("getBusList: ","Route No.:"+busList.get(position).getRoute_no());
                    mapClick.onClick(String.valueOf(busList.get(position).getRoute_no()),String.valueOf(busList.get(position).getGroupId()));
                }
            });
        }
        if(busList.get(position).getGroupId()==10)
        {
            holder.busButtonItemLayoutBinding.routeBtn.setBackgroundColor( ContextCompat.getColor(holder.busButtonItemLayoutBinding.getRoot().getContext(), R.color.Color_Coral)) ;
            holder.busButtonItemLayoutBinding.routeBtn.setText(busList.get(position).getGroupName());
//        holder.busButtonItemLayoutBinding.busGroupNameTV.setText(busList.get(position).getGroupName());
//        holder.busButtonItemLayoutBinding.routeBtn.setVisibility(View.GONE);
//        holder.busButtonItemLayoutBinding.busGroupNameTV.setOnClickListener(new View.OnClickListener() {
            holder.busButtonItemLayoutBinding.routeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapClick.onClick(String.valueOf(busList.get(position).getRoute_no()));
                    mapClick.onClick(String.valueOf(busList.get(position).getRoute_no()),String.valueOf(busList.get(position).getGroupId()));
                    Log.d("getBusList: ","Route No.:"+busList.get(position).getRoute_no());
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        BusButtonLayoutBinding busButtonItemLayoutBinding;
        public ViewHolder(@NonNull BusButtonLayoutBinding itemView) {
            super(itemView.getRoot());
            busButtonItemLayoutBinding=itemView;
        }
    }
}
