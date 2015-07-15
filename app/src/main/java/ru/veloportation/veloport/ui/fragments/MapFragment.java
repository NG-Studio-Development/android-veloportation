package ru.veloportation.veloport.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.veloportation.veloport.R;

public class MapFragment extends BaseMapFragment{

    public MapFragment() {

    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_map;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater,container,savedInstanceState);
    }

}
