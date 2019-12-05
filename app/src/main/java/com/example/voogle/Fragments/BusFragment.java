package com.example.voogle.Fragments;


import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.voogle.R;
import com.example.voogle.databinding.FragmentBusBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class BusFragment extends Fragment {

    FragmentBusBinding fragmentBusBinding;


    public BusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentBusBinding=DataBindingUtil.inflate(inflater,R.layout.fragment_bus, container, false);
        fragmentBusBinding.busFragmentTV.setText("Bus Fragment");
        return fragmentBusBinding.getRoot();
    }

}
