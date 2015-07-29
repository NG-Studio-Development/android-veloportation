package ru.veloportation.veloport.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import ru.veloportation.veloport.R;
import ru.veloportation.veloport.model.db.Order;
import ru.veloportation.veloport.model.requests.CustomerRequest;
import ru.veloportation.veloport.model.requests.OrderRequest;
import ru.veloportation.veloport.ui.activities.AboutAsActivity;
import ru.veloportation.veloport.ui.activities.OrderActivity;
import ru.veloportation.veloport.ui.adapters.OrderAdapter;
import ru.veloportation.veloport.utils.CommonUtils;


public class OrdersMenuFragment extends BaseFragment {

    private List<Order> listOrder;
    private ListView lvOrder;
    private TextView tvEmptyList;

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
        tvEmptyList = (TextView) view.findViewById(R.id.tvEmptyList);
        setHasOptionsMenu(true);

        getHostActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getHostActivity().getSupportActionBar().setTitle(getString(R.string.your_order));

        lvOrder = (ListView) view.findViewById(R.id.lvOrders);

        lvOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderActivity.startOrderActivity(getHostActivity(), listOrder.get(position), OrderActivity.RUN_AS_CUSTOMER);
            }
        });

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

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        lvOrder.setAdapter(null);
        if (CommonUtils.isConnected(getHostActivity()))
            Volley.newRequestQueue(getHostActivity()).add(createRequestCustomerMyCustomerId());
        else
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();

    }

    CustomerRequest createRequestCustomerMyCustomerId() {
        return CustomerRequest.requestCustomerMyCustomerId(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if ( response.contains("error") ) {
                    Toast.makeText( getHostActivity(),getString(R.string.server_error),Toast.LENGTH_LONG ).show();
                } else {
                    Volley.newRequestQueue(getHostActivity()).add(createRequestGetListCustomerOrder(response));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText( getHostActivity(),getString(R.string.server_error),Toast.LENGTH_LONG ).show();
            }
        });
    }

    protected OrderRequest createRequestGetListCustomerOrder(String id) {
        return OrderRequest.requestGetListCustomerOrder(id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listOrder = new Gson().fromJson(response, new TypeToken<List<Order>>() {}.getType());

                if (listOrder.isEmpty())
                    tvEmptyList.setVisibility(View.VISIBLE);

                lvOrder.setAdapter(OrderAdapter.createOrderAdapter(getHostActivity(), listOrder));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                getHostActivity().onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
