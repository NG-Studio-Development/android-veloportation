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
import ru.veloportation.veloport.model.db.Order;
import ru.veloportation.veloport.model.requests.OrderRequest;
import ru.veloportation.veloport.ui.activities.MainActivity;


public class OrderFragment extends BaseFragment<MainActivity> {

    private static final String RUN_CUSTOMER = "state_customer";

    private static final String RUN_COURIER = "state_courier";

    private Order order;
    private String runAs;



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
        if (getArguments() != null) {

        }
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

        tvName.setText(order.getCustomerName());
        tvPhone.setText(order.getPhone());
        tvAddressSender.setText(order.getAddressSender());
        tvAddressDelivery.setText(order.getAddressDelivery());
        tvMessage.setText(order.getAddress());


        Button button = (Button) view.findViewById(R.id.buttonMap);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().replaceFragment(new MapFragment(), true);
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

        if (order.getStatus())
            buttonGet.setVisibility(View.INVISIBLE);
        else
            buttonGet.setVisibility(View.VISIBLE);
        buttonGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.setState(true);

                OrderRequest request = OrderRequest.requestTakeOrder(order, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        buttonGet.setVisibility(View.INVISIBLE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("LIST_ORDER", "ERROR");
                    }
                });

                Volley.newRequestQueue(getHostActivity()).add(request);
            }
        });
    }

    private void createCustomerView(View view) {
        TextView tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        tvStatus.setVisibility(View.VISIBLE);
        if (order.getStatus()) {
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
}
