package ru.veloportation.veloport.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import ru.veloportation.VeloportApplication;
import ru.veloportation.veloport.R;
import ru.veloportation.veloport.ui.activities.AboutAsActivity;


public class OrdersMenuFragment extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_orders_menu;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_orders_menu, container, false);

        Button button = (Button) view.findViewById(R.id.buttonMap);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().replaceFragment(new MapFragment(),true);
            }
        });

        TextView tvTimer = (TextView) view.findViewById(R.id.tvTimer);
        TextView tvAboutUs = (TextView) view.findViewById(R.id.tvAboutUs);
        tvAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().startActivity(new Intent(getHostActivity(), AboutAsActivity.class));
            }
        });

        ImageButton ibCreateOrder = (ImageButton) view.findViewById(R.id.ibCreateOrder);
        ibCreateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().replaceFragment(new BookingFragment(), true);
            }
        });



        if (VeloportApplication.orderFlagDEBUG) {
            ibCreateOrder.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
            tvTimer.setVisibility(View.VISIBLE);
        } else {
            ibCreateOrder.setVisibility(View.VISIBLE);
            button.setVisibility(View.GONE);
            tvTimer.setVisibility(View.GONE);
        }


        return view;
    }







}
