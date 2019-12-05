package com.example.voogle.Fragments;


import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.voogle.R;
import com.example.voogle.databinding.FragmentTrainBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrainFragement extends Fragment {

    FragmentTrainBinding fragmentTrainBinding;

    public TrainFragement() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentTrainBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_train, container, false);

        fragmentTrainBinding.trainTV.setText("Train Fragment");


        return fragmentTrainBinding.getRoot();
    }

}
