package com.example.voogle.Adapters;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voogle.Fragments.MapFragment;
import com.example.voogle.Functions.MapClick;
import com.example.voogle.PojoClasses.Bus;
import com.example.voogle.R;
import com.example.voogle.databinding.RouteButtonItemLayoutBinding;

import java.util.ArrayList;

public class RouteButtonAdapter extends RecyclerView.Adapter<RouteButtonAdapter.ViewHolder> {

    FragmentActivity context;
    ArrayList<Bus>busList;
    private MapClick mapClick;;

    public RouteButtonAdapter(FragmentActivity context, ArrayList<Bus> busList, MapClick mapClick) {
        this.context = context;
        this.busList = busList;
        this.mapClick=mapClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RouteButtonItemLayoutBinding routeButtonItemLayoutBinding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.route_button_item_layout, parent, false);
        return new ViewHolder(routeButtonItemLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.routeButtonItemLayoutBinding.routeBtn.setText("Route No. "+String.valueOf(busList.get(position).getRoute_no()));
        holder.routeButtonItemLayoutBinding.busGroupNameTV.setText(busList.get(position).getGroupName());
        holder.routeButtonItemLayoutBinding.routeBtn.setVisibility(View.GONE);
        holder.routeButtonItemLayoutBinding.busGroupNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapClick.onClick(String.valueOf(busList.get(position).getRoute_no()));
            }
        });
//        Bundle bundle=new Bundle();
//        bundle.putString("routeNo",btnText.get(position).toString());
//        MapFragment mapFragment=new MapFragment();
//        mapFragment.setArguments(bundle);

    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RouteButtonItemLayoutBinding routeButtonItemLayoutBinding;
        public ViewHolder(@NonNull RouteButtonItemLayoutBinding itemView) {
            super(itemView.getRoot());
            routeButtonItemLayoutBinding=itemView;
        }
    }
}
