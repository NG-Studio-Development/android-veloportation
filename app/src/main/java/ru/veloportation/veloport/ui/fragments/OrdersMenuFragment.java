package ru.veloportation.veloport.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import ru.veloportation.VeloportApplication;
import ru.veloportation.veloport.R;
import ru.veloportation.veloport.model.db.Order;
import ru.veloportation.veloport.model.requests.OrderRequest;
import ru.veloportation.veloport.ui.activities.AboutAsActivity;
import ru.veloportation.veloport.ui.adapters.OrderAdapter;


public class OrdersMenuFragment extends BaseFragment {

    private List<Order> listOrder;

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

        /*Button button = (Button) view.findViewById(R.id.buttonMap);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().replaceFragment(new MapFragment(), true);
            }
        });*/

        final ListView lvOrder = (ListView) view.findViewById(R.id.lvOrders);
        // = new ArrayList<>();// =

        lvOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getHostActivity().replaceFragment( OrderFragment.customerFragment(listOrder.get(position)), true );
            }
        });


        OrderRequest request = OrderRequest.requestGetListCustomerOrder(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listOrder = new Gson().fromJson(response, new TypeToken< List<Order> >() {}.getType());
                lvOrder.setAdapter(OrderAdapter.createOrderAdapter(getHostActivity(), listOrder));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(getHostActivity()).add(request);

        //TextView tvTimer = (TextView) view.findViewById(R.id.tvTimer);
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
            //ibCreateOrder.setVisibility(View.GONE);
            //button.setVisibility(View.VISIBLE);
            //tvTimer.setVisibility(View.VISIBLE);
        } else {
            //ibCreateOrder.setVisibility(View.VISIBLE);
            //button.setVisibility(View.GONE);
            //tvTimer.setVisibility(View.GONE);
        }


        return view;
    }







}
