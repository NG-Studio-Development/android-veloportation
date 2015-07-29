package ru.veloportation.veloport.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import ru.veloportation.veloport.ConstantsVeloportApp;
import ru.veloportation.veloport.R;
import ru.veloportation.veloport.VeloportApplication;
import ru.veloportation.veloport.model.db.Order;
import ru.veloportation.veloport.model.requests.CourierRequest;
import ru.veloportation.veloport.model.requests.OrderRequest;
import ru.veloportation.veloport.ui.activities.CourierActivity;
import ru.veloportation.veloport.ui.activities.OrderActivity;
import ru.veloportation.veloport.ui.adapters.OrderAdapter;

public class ListOrderFragment extends BaseFragment<CourierActivity> {

    private static final String ARG_TYPE_ORDERS = "type_orders";

    private static final String TYPE_MY_ORDERS = "type_my_orders";
    private static final String TYPE_FREE_ORDERS = "type_free_orders";

    List<Order> listOrder;

    ListView lvOrder;

    private String typeOrders;

    public static ListOrderFragment newMyOrdersFragment() {
        ListOrderFragment fragment = new ListOrderFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TYPE_ORDERS, TYPE_MY_ORDERS);
        fragment.setArguments(bundle);

        return fragment;
    }

    public static ListOrderFragment newFreeOrdersFragment() {
        ListOrderFragment fragment = new ListOrderFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TYPE_ORDERS, TYPE_FREE_ORDERS);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            typeOrders = getArguments().getString(ARG_TYPE_ORDERS);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_list_order;
    }

    /*public static ListOrderFragment createFreeOrdersFragment() {
        return new ListOrderFragment();
    }

    public static ListOrderFragment createMyOrdersFragment() {
        return new ListOrderFragment();
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_order, container, false);

        getHostActivity().getSupportActionBar().setTitle(getString(R.string.free_booking));
        getHostActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        lvOrder = (ListView) view.findViewById(R.id.lvOrders);
        lvOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderActivity.startOrderActivity(getHostActivity(),listOrder.get(position), OrderActivity.RUN_AS_COURIER);
            }
        });

        OrderRequest request = OrderRequest.requestGetFreeOrder(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listOrder = new Gson().fromJson(response, new TypeToken<List<Order>>() {}.getType());
                lvOrder.setAdapter(OrderAdapter.createOrderAdapter(VeloportApplication.getInstance(), listOrder));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });


        /*OrderRequest request = OrderRequest.requestGetFreeOrder(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listOrder = new Gson().fromJson(response, new TypeToken<List<Order>>() {
                }.getType());
                lvOrder.setAdapter(OrderAdapter.createOrderAdapter(getHostActivity(), listOrder));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });*/


        if ( typeOrders!=null && typeOrders.equals(TYPE_FREE_ORDERS) )
            Volley.newRequestQueue(getHostActivity()).add(request);
        else if ( typeOrders!=null && typeOrders.equals(TYPE_MY_ORDERS) )
            Volley.newRequestQueue(getHostActivity()).add(createRequestIdCourier());

        return view;
    }

    protected CourierRequest createRequestIdCourier() {
        return CourierRequest.requestIdCourier(VeloportApplication.getInstance().getApplicationPreferences().getString(ConstantsVeloportApp.PREF_KEY_LOGIN, "NUN"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if ( response.contains("error") ) {
                            Toast.makeText(VeloportApplication.getInstance(), getString(R.string.server_error),Toast.LENGTH_LONG).show();
                        } else {

                            try {
                                String courierId = new JSONArray(response).getJSONObject(0).getString("id");
                                Volley.newRequestQueue(VeloportApplication.getInstance()).add(createRequestOfCourierOrder(courierId) );
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }


                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(VeloportApplication.getInstance(), getString(R.string.server_error),Toast.LENGTH_LONG).show();
                    }
                });
    }

    protected OrderRequest createRequestOfCourierOrder(String courierId) {
        return OrderRequest.requestOfCourierOrder(courierId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if ( response.contains("error") ) {
                    Toast.makeText(VeloportApplication.getInstance(), getString(R.string.server_error),Toast.LENGTH_LONG).show();
                } else {

                    listOrder = new Gson().fromJson(response, new TypeToken<List<Order>>() {}.getType());
                    if (lvOrder != null)
                        lvOrder.setAdapter(OrderAdapter.createOrderAdapter(VeloportApplication.getInstance(), listOrder));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VeloportApplication.getInstance(), getString(R.string.server_error),Toast.LENGTH_LONG).show();
            }
        });
    }

}
