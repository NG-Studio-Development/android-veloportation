package ru.veloportation.veloport.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.model.db.Order;

public class MapFragment extends BaseMapFragment{

    public static String ACTION_NEW_LOCATION = "action_new_location";
    public static String KEY_DATA = "key_data";

    public MapFragment() {

    }

    public static MapFragment newMapFragment(Order order) {return new MapFragment();}
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