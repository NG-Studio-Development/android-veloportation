package ru.veloportation.veloport.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.ui.adapters.ItemsAdapter;

public class ListOrderFragment extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_list_order;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_order, container, false);
        ListView lvOrder = (ListView) view.findViewById(R.id.lvOrder);
        lvOrder.setAdapter(ItemsAdapter.getOrderAdapter(getHostActivity()));
        return view;
    }




}
