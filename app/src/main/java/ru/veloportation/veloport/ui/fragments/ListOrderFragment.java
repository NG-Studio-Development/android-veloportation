package ru.veloportation.veloport.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.model.db.Order;
import ru.veloportation.veloport.model.requests.OrderRequest;
import ru.veloportation.veloport.ui.activities.CourierActivity;
import ru.veloportation.veloport.ui.activities.OrderActivity;
import ru.veloportation.veloport.ui.adapters.OrderAdapter;

public class ListOrderFragment extends BaseFragment<CourierActivity> {

    List<Order> listOrder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_list_order;
    }

    public static ListOrderFragment createMyOrdersFragment() {
        return new ListOrderFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_order, container, false);

        getHostActivity().getSupportActionBar().setTitle(getString(R.string.free_booking));
        getHostActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        final ListView lvOrder = (ListView) view.findViewById(R.id.lvOrders);
        lvOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //getHostActivity().replaceFragment( OrderFragment.courierFragment(listOrder.get(position)), true );
                OrderActivity.startOrderActivity(getHostActivity(),listOrder.get(position));
            }
        });

        OrderRequest request = OrderRequest.requestGetListOrder(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listOrder = new Gson().fromJson(response, new TypeToken< List<Order> >() {}.getType());
                lvOrder.setAdapter(OrderAdapter.createOrderAdapter(getHostActivity(), listOrder));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("LIST_ORDER","ERROR");
            }
        });

        Volley.newRequestQueue(getHostActivity()).add(request);

        return view;
    }
}
