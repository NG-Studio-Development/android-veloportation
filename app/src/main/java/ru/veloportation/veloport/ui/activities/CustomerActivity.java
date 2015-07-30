package ru.veloportation.veloport.ui.activities;

import android.os.Bundle;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.ui.fragments.OrdersMenuFragment;

public class CustomerActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        addFragment(new OrdersMenuFragment(), false);
    }
}
