package ru.veloportation.veloport.ui.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import ru.veloportation.VeloportApplication;
import ru.veloportation.veloport.ConstantsVeloportApp;
import ru.veloportation.veloport.R;
import ru.veloportation.veloport.model.requests.CourierRequest;
import ru.veloportation.veloport.ui.fragments.ListOrderFragment;
import ru.veloportation.veloport.ui.fragments.OrdersMenuFragment;


public abstract class MainActivity extends BaseActivity {

    public static final String START_TYPE = "start_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getContainer() {
        return R.id.container;
    }


    protected Fragment selectFragmentByStartType(int startType) {
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
        Intent intent = new Intent(context, CourierActivity.class);
        intent.putExtra(START_TYPE, START_COURIER);
        context.startActivity(intent);
    }

    public final static int START_COURIER = 0;

    public static void startCustomerActivity(Context context) {
        Intent intent = new Intent(context, CustomerActivity.class);
        intent.putExtra(START_TYPE, START_CUSTOMER);
        context.startActivity(intent);
    }

    public final static int START_CUSTOMER = 1;

    private static void startActivityByType(Context context, int type) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(START_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            case R.id.changeStatus:
                changeStatus(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    protected void logout() {
        VeloportApplication.getInstance().getApplicationPreferencesEditor().clear();
        VeloportApplication.getInstance().getApplicationPreferencesEditor().commit();
        startActivity(new Intent(this, StartActivity.class));
        finish();
    }

    protected void changeStatus(final MenuItem item) {
        final boolean state = VeloportApplication.getInstance().getApplicationPreferences().getBoolean(ConstantsVeloportApp.PREF_KEY_STATE_EMPLOYMENT, true);

        CourierRequest request = CourierRequest.requestSetEmployment(VeloportApplication.getInstance().getUUID(),
                !state,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (state)
                            item.setIcon(R.mipmap.ic_state_busy);
                        else
                            item.setIcon(R.mipmap.ic_state_free);

                        boolean tempState = !state;
                        VeloportApplication.getInstance().getApplicationPreferencesEditor().putBoolean(ConstantsVeloportApp.PREF_KEY_STATE_EMPLOYMENT, tempState);
                        VeloportApplication.getInstance().getApplicationPreferencesEditor().commit();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, getString(R.string.error_change_internet_connection), Toast.LENGTH_LONG).show();
                    }
                });

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }
}
