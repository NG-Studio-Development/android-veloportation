package ru.veloportation.veloport.ui.activities;

import android.os.Bundle;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.ui.fragments.OrdersMenuFragment;

public class CustomerActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        //Intent intent = getIntent();
        //int startType = intent.getIntExtra(START_TYPE, START_COURIER);

        addFragment(new OrdersMenuFragment(), false);
    }

    /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    } */
}
