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
import com.example.voogle.R;
import com.example.voogle.databinding.RouteButtonItemLayoutBinding;

import java.util.ArrayList;

public class RouteButtonAdapter extends RecyclerView.Adapter<RouteButtonAdapter.ViewHolder> {

    FragmentActivity context;
    ArrayList<Integer>btnText;
    private MapClick mapClick;;

    public RouteButtonAdapter(FragmentActivity context, ArrayList<Integer> btnText, MapClick mapClick) {
        this.context = context;
        this.btnText = btnText;
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
        holder.routeButtonItemLayoutBinding.routeBtn.setText("Route No. "+btnText.get(position).toString());
        holder.routeButtonItemLayoutBinding.routeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapClick.onClick(btnText.get(position).toString());
            }
        });
        Bundle bundle=new Bundle();
        bundle.putString("routeNo",btnText.get(position).toString());
        MapFragment mapFragment=new MapFragment();
        mapFragment.setArguments(bundle);

    }

    @Override
    public int getItemCount() {
        return btnText.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RouteButtonItemLayoutBinding routeButtonItemLayoutBinding;
        public ViewHolder(@NonNull RouteButtonItemLayoutBinding itemView) {
            super(itemView.getRoot());
            routeButtonItemLayoutBinding=itemView;
        }
    }
}
