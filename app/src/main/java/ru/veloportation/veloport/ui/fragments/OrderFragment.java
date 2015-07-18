package ru.veloportation.veloport.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.components.SampleAlarmReceiver;
import ru.veloportation.veloport.model.db.Order;
import ru.veloportation.veloport.model.requests.OrderRequest;
import ru.veloportation.veloport.ui.activities.MainActivity;


public class OrderFragment extends BaseFragment<MainActivity> {

    private static final String RUN_CUSTOMER = "state_customer";

    private static final String RUN_COURIER = "state_courier";

    private TextView tvStatus;

    private Order order;
    private String runAs;

    private SampleAlarmReceiver alarm;// = new SampleAlarmReceiver();



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

        //if (getArguments() != null) {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order, container, false);

        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);
        TextView tvAddressSender = (TextView) view.findViewById(R.id.tvAddressSender);
        TextView tvAddressDelivery = (TextView) view.findViewById(R.id.tvAddressDelivery);
        TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);

        tvStatus = (TextView) view.findViewById(R.id.tvStatus);

        tvName.setText(order.getCustomerName());
        tvPhone.setText(order.getPhone());
        tvAddressSender.setText(order.getAddressSender());
        tvAddressDelivery.setText(order.getAddressDelivery());
        tvMessage.setText(order.getAddress());


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

        return view;
    }


    private void createCourierView(View view) {

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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    private void takeOrderAction(final Button buttonGet) {
        order.setState(Order.STATE_TAKE);

        OrderRequest request = OrderRequest.requestTakeOrder(order, new Response.Listener<String>() {
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
}
