package com.example.keso.quizone;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by KESO on 02/02/2017.
 */
public class CategoryFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_category, container, false);
        return v;
    }

    @Override
    public void onResume() {
            getActivity().setTitle("Kategori");
            super.onResume();
    }
}