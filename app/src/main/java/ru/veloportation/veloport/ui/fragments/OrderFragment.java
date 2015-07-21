package ru.veloportation.veloport.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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


    private TextView tvStatus;

    private Order order;
    private String runAs;

    private SampleAlarmReceiver alarm;

    private BroadcastReceiver broadcastReceiver;

    private LocationRequest requestLocation;



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

        //super.onCreateView(inflater,container,savedInstanceState);//inflater.inflate(R.layout.fragment_order, container, false);

        View view = super.onCreateView(inflater, container, savedInstanceState);

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
                Toast.makeText(getHostActivity(),"LOCATION_ERROR", Toast.LENGTH_LONG);
            }
        });

        Volley.newRequestQueue(getHostActivity()).add(requestLocation);


        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);
        TextView tvCost = (TextView) view.findViewById(R.id.tvCost);
        TextView tvAddressSender = (TextView) view.findViewById(R.id.tvAddressSender);
        TextView tvAddressDelivery = (TextView) view.findViewById(R.id.tvAddressDelivery);
        TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);

        tvStatus = (TextView) view.findViewById(R.id.tvStatus);
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

        Log.d("SET_LOCATION", "latitude = "+latitude+" longitude = "+longitude);

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

        RelativeLayout rlMapContainer = (RelativeLayout) view.findViewById(R.id.rlMapContainer);
        rlMapContainer.setVisibility(View.INVISIBLE);

        final Button buttonGet = (Button) view.findViewById(R.id.buttonGet);

        if (order.getStatus() == Order.STATE_TAKE)
            buttonGet.setVisibility(View.INVISIBLE);
        else
            buttonGet.setVisibility(View.VISIBLE);

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
        TextView tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        tvStatus.setVisibility(View.VISIBLE);
        if (order.getStatus() == Order.STATE_TAKE) {
            tvStatus.setText(getString(R.string.order_take));
            tvStatus.setTextColor(getResources().getColor(R.color.green));
        } else {
            tvStatus.setText(getString(R.string.searching_of_courier));
            tvStatus.setTextColor(getResources().getColor(R.color.red));
        }
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_order;
    }

    private void takeOrderAction(final Button buttonGet) {
        order.setState(Order.STATE_TAKE);

        OrderRequest request = OrderRequest.requestTakeOrder(order, VeloportApplication.getInstance().getUUID(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //buttonGet.setVisibility(View.INVISIBLE);
                buttonGet.setText(R.string.order_delivery);
                alarm.setAlarm(getHostActivity());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("LIST_ORDER", "ERROR");
            }
        });

        Volley.newRequestQueue(getHostActivity()).add(request);
    }

    private void deliveryOrderAction(Button button) {
        alarm.cancelAlarm(getHostActivity());
        button.setVisibility(View.INVISIBLE);
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText(R.string.order_delivery);
        tvStatus.setTextColor(getResources().getColor(R.color.green));
    }

    private void onTakeOrder() {
        Log.d("UPDATE_INTERFACE", "onTakeOrder");

        tvStatus.setText(getString(R.string.order_take));
        tvStatus.setTextColor(getResources().getColor(R.color.green));

        if ( requestLocation != null )
            Volley.newRequestQueue(getHostActivity()).add(requestLocation);



    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getHostActivity().unregisterReceiver(broadcastReceiver);
    }


    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                getHostActivity().onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    } */
}
