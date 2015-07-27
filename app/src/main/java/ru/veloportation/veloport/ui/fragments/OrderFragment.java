package ru.veloportation.veloport.ui.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import ru.veloportation.VeloportApplication;
import ru.veloportation.veloport.ConstantsVeloportApp;
import ru.veloportation.veloport.R;
import ru.veloportation.veloport.components.SampleAlarmReceiver;
import ru.veloportation.veloport.model.db.Order;
import ru.veloportation.veloport.model.requests.LocationRequest;
import ru.veloportation.veloport.model.requests.OrderRequest;
import ru.veloportation.veloport.ui.activities.MainActivity;


public class OrderFragment extends BaseMapFragment<MainActivity> {

    private static final float START_ZOOM = 14.0f;

    private static final String RUN_CUSTOMER = "state_customer";

    private static final String RUN_COURIER = "state_courier";

    public final static String ACTION_TAKE_ORDER = "action_take_order";

    public final static String DATA_LATITUDE = "data_latitude";

    public final static String DATA_LONGITUDE = "data_longitude";

    private static Order order;

    private String runAs;

    private static SampleAlarmReceiver alarm;

    private BroadcastReceiver broadcastReceiver;

    private LocationRequest requestLocation;

    static Button buttonGet;

    TextView tvTimer;

    TextView tvTitleTimer;

    RelativeLayout rlEmptyMap;

    public static OrderFragment customerFragment(Order order) {
        return createOrderFragment(order, RUN_CUSTOMER);
    }

    public static OrderFragment courierFragment(Order order) {
        return createOrderFragment(order, RUN_COURIER);
    }

    private static OrderFragment createOrderFragment(Order order, String runAs) {
        OrderFragment fragment = new OrderFragment();
        fragment.order = order;
        fragment.runAs = runAs;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarm = new SampleAlarmReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater,container,savedInstanceState);

        View view = super.onCreateView(inflater, container, savedInstanceState);
        buttonGet = (Button) view.findViewById(R.id.buttonGet);
        rlEmptyMap = (RelativeLayout) view.findViewById(R.id.rlEmptyMap);

        tvTitleTimer = (TextView) view.findViewById(R.id.tvTitleTimer);

        setHasOptionsMenu(true);
        getHostActivity().getSupportActionBar().setTitle(order.getAddressDelivery());
        getHostActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        requestLocation =  LocationRequest.requestGetCourierLocation(order, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("Error")) {
                    Toast.makeText(getHostActivity(), "Probliem with location", Toast.LENGTH_LONG);
                } else {
                    try {
                        JSONArray arr = new JSONArray(response);
                        JSONObject jsonObject = arr.getJSONObject(0);

                        double latitude = jsonObject.getDouble("latitude");
                        double longitude = jsonObject.getDouble("longitude");

                        setLocation(new LatLng(latitude, longitude));

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getHostActivity(),"LOCATION_ERROR", Toast.LENGTH_LONG).show();
            }
        });

        Volley.newRequestQueue(getHostActivity()).add(requestLocation);

        tvTimer = (TextView) view.findViewById(R.id.tvTimer);
        TextView tvCost = (TextView) view.findViewById(R.id.tvCost);
        TextView tvAddressSender = (TextView) view.findViewById(R.id.tvAddressSender);
        TextView tvAddressDelivery = (TextView) view.findViewById(R.id.tvAddressDelivery);

        tvAddressSender.setText(order.getAddressSender());
        tvAddressDelivery.setText(order.getAddressDelivery());
        tvCost.setText(order.getCost());

        Button button = (Button) view.findViewById(R.id.buttonMap);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().replaceFragment(MapFragment.newMapFragment(order), true);
            }
        });


        if ( order.getStatus() == Order.STATE_TAKE ) {

            tvTitleTimer.setText(getString(R.string.delivery_via));
            tvTimer.setVisibility(View.VISIBLE);
            tvTimer.setText(getTimeResidueString());
            rlEmptyMap.setVisibility(View.GONE);
        } else {

            tvTitleTimer.setText(getString(R.string.searching_of_courier));
            tvTitleTimer.setTextColor(getResources().getColor(R.color.red));
        }

        if ( runAs.equals(RUN_COURIER) )
            createCourierView(view);
        else if ( runAs.equals(RUN_CUSTOMER) )
            createCustomerView(view);

        IntentFilter intFilt = new IntentFilter(ConstantsVeloportApp.BROADCAST_ACTION);
        broadcastReceiver = createBroadCast();
        getHostActivity().registerReceiver(broadcastReceiver, intFilt);

        return view;
    }



    private void setLocation(Bundle extras) {

        double latitude = extras.getDouble(DATA_LATITUDE, -1);
        double longitude = extras.getDouble(DATA_LONGITUDE, -1);

        Log.d("SET_LOCATION", "latitude = " + latitude + " longitude = " + longitude);

        if (latitude != -1 && longitude != -1)
            setLocation( new LatLng(latitude, longitude) );

    }

    private void setLocation(LatLng latLng) {
        changingCameraPosition(latLng);
        updateLocations(latLng, null, null);
        zoomIn(getOptimalCameraPosition(), START_ZOOM);
    }

    BroadcastReceiver createBroadCast() {
        return new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra(PARAM_ACTION).equals(ACTION_TAKE_ORDER)) {
                    onTakeOrder();
                }
            }
        };
    }

    private void createCourierView(View view) {

        if (order.getStatus() == Order.STATE_TAKE) {
            buttonGet.setText(R.string.order_delivery);
            buttonGet.setVisibility(View.VISIBLE);
        } else if(order.getStatus() == Order.STATE_DELIVERY) {
            buttonGet.setVisibility(View.INVISIBLE);
        } else {
            tvTitleTimer.setVisibility(View.INVISIBLE);
            tvTimer.setVisibility(View.INVISIBLE);
        }


        buttonGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( order.getStatus() == Order.STATE_SEARCH_COURIER ) {
                    takeOrderAction(buttonGet);
                } else if ( order.getStatus() == Order.STATE_TAKE ) {
                    deliveryOrderAction(buttonGet);
                }
            }
        });
    }

    private void createCustomerView(View view) {

        tvTitleTimer.setVisibility(View.VISIBLE);

        //TextView tvTimer = (TextView) view.findViewById(R.id.tvTimer);
        //tvTimer.setVisibility(View.INVISIBLE);

        gonButton(buttonGet);
    }

    private void gonButton(Button button) {
        button.getLayoutParams().height=0;
    }

    String getTimeResidueString() {

        long timestamp = getTimeResidue();

        int days = (int) (timestamp / (1000*60*60*24));
        int hours = (int) ((timestamp - (1000*60*60*24*days)) / (1000*60*60));
        int min = (int) (timestamp - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);

        return hours+" : "+min;
    }

    long getTimeResidue() {
        Calendar calendar = Calendar.getInstance();
        long orderTime = order.getTimeInMills();
        long currentTime = calendar.getTimeInMillis();
        long result = orderTime - currentTime;

        return result;
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_order;
    }

    private void takeOrderAction(final Button buttonGet) {
        showDatePickerDialog();
    }

    private void deliveryOrderAction(Button button) {
        order.setState(Order.STATE_DELIVERY);
        alarm.cancelAlarm(getHostActivity());
        button.setVisibility(View.INVISIBLE);

        OrderRequest request = OrderRequest.removeDelivery( order, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        } );
        Volley.newRequestQueue(getHostActivity()).add(request);
    }

    private void onTakeOrder() {

        tvTitleTimer.setVisibility(View.VISIBLE);
        tvTimer.setVisibility(View.VISIBLE);
        tvTimer.setText(getTimeResidueString());
        rlEmptyMap.setVisibility(View.GONE);

        if ( requestLocation != null )
            Volley.newRequestQueue(getHostActivity()).add(requestLocation);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getHostActivity().unregisterReceiver(broadcastReceiver);
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = TimePickerFragment.newDatePickerFragment(getHostActivity());
        newFragment.show(getHostActivity().getSupportFragmentManager(), "datePicker");
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        static Context context;

        static TimePickerFragment newDatePickerFragment(Context context) {
            TimePickerFragment.context = context;
            return new TimePickerFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(context, this, hour, minute,
                    DateFormat.is24HourFormat(context));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            order.setState(Order.STATE_TAKE);

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay+1);
            calendar.set(Calendar.MINUTE, minute);

            long time = calendar.getTimeInMillis();

            order.setTimeInMills(time);

            OrderRequest request = OrderRequest.requestTakeOrder(order, VeloportApplication.getInstance().getUUID(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    buttonGet.setText(R.string.order_delivery);
                    alarm.setAlarm(context);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                }
            });

            if (view.isShown())
                Volley.newRequestQueue(context).add(request);
        }
    }
}
