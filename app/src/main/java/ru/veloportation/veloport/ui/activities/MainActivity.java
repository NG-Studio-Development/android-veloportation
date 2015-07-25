package ru.veloportation.veloport.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.ui.fragments.ListOrderFragment;
import ru.veloportation.veloport.ui.fragments.OrdersMenuFragment;


public class MainActivity extends BaseActivity {

    public static final String START_TYPE = "start_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Intent intent = getIntent();
        int startType = intent.getIntExtra(START_TYPE, START_COURIER);

        addFragment( selectFragmentByStartType(startType),false );
    }

    @Override
    protected int getContainer() {
        return R.id.container;
    }


    private Fragment selectFragmentByStartType(int startType) {
        Fragment fragment = null;
        switch (startType) {
            default:
            case START_CUSTOMER:
                fragment = new OrdersMenuFragment();
                break;

            case START_COURIER:
                fragment = new ListOrderFragment();
                break;
        }

        return fragment;
    }

    public static void startCourierActivity(Context context) {
        startActivityByType(context, START_COURIER);
    }

    public final static int START_COURIER = 0;

    public static void startCustomerActivity(Context context) {
        startActivityByType(context, START_CUSTOMER);
    }

    public final static int START_CUSTOMER = 1;

    private static void startActivityByType(Context context, int type) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(START_TYPE, type);
        context.startActivity(intent);
    }
}
