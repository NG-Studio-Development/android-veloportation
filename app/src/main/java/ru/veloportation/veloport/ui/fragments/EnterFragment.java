package ru.veloportation.veloport.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.veloportation.veloport.ui.activities.MainActivity;
import ru.veloportation.veloport.R;
import ru.veloportation.veloport.ui.activities.StartActivity;


public class EnterFragment extends BaseFragment<StartActivity> implements View.OnClickListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_enter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_enter, container, false);

        view.findViewById(R.id.tvCourier).setOnClickListener(this);
        view.findViewById(R.id.tvCustomer).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.tvCourier:
                getHostActivity().replaceFragment(new LoginFragment(),false);
                break;
            case R.id.tvCustomer:
                MainActivity.startCustomerActivity(getHostActivity());
                getHostActivity().registerInBackground();
                break;
        }

    }
}
