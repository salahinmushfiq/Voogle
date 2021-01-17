package com.example.voogle.Adapters;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voogle.Fragments.TestMapsFragment;
import com.example.voogle.Functions.MapClick;
import com.example.voogle.PojoClasses.Bus;
import com.example.voogle.R;
import com.example.voogle.databinding.BusButtonDetailLayoutBinding;
import com.example.voogle.databinding.BusButtonLayoutBinding;

import java.util.ArrayList;

public class BusButtonDetailsAdapter extends RecyclerView.Adapter<BusButtonDetailsAdapter.ViewHolder> {

    FragmentActivity context;
    ArrayList<Bus>busList;
    private MapClick mapClick;;

    public BusButtonDetailsAdapter(FragmentActivity context, ArrayList<Bus> busList, MapClick mapClick) {
        this.context = context;
        this.busList = busList;
        this.mapClick=mapClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        BusButtonDetailLayoutBinding busButtonDetailLayoutBinding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.bus_button_detail_layout, parent, false);
        return new ViewHolder(busButtonDetailLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.busButtonItemLayoutBinding.routeBtn.setText("Route No. "+String.valueOf(busList.get(position).getRoute_no()));
        holder.busButtonDetailLayoutBinding.busGroupNameTV.setText(busList.get(position).getGroupName());
        holder.busButtonDetailLayoutBinding.busRatingTV.setText(String.valueOf(busList.get(position).getRatings())+"  *");
//        holder.busButtonItemLayoutBinding.busGroupNameTV.setText(busList.get(position).getGroupName());
//        holder.busButtonItemLayoutBinding.routeBtn.setVisibility(View.GONE);
//        holder.busButtonItemLayoutBinding.busGroupNameTV.setOnClickListener(new View.OnClickListener() {


//        holder.busButtonDetailLayoutBinding.routeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mapClick.onClick(String.valueOf(busList.get(position).getRoute_no()));
//                Log.d("getBusList: ","Route No.:"+busList.get(position).getRoute_no());
//            }
//        });


//        Bundle bundle=new Bundle();
//        bundle.putString("routeNo",String.valueOf(busList.get(position).getRoute_no()));
//        TestMapsFragment mapFragment=new TestMapsFragment();
//        mapFragment.setArguments(bundle);

    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        BusButtonDetailLayoutBinding busButtonDetailLayoutBinding;
        public ViewHolder(@NonNull BusButtonDetailLayoutBinding itemView) {
            super(itemView.getRoot());
            busButtonDetailLayoutBinding=itemView;
        }
    }
}
