package ru.veloportation.veloport.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import ru.veloportation.VeloportApplication;
import ru.veloportation.veloport.ConstantsVeloportApp;
import ru.veloportation.veloport.R;
import ru.veloportation.veloport.model.db.Order;


public class BookingFragment extends BaseFragment {



    EditText etName;
    EditText etEmail;
    EditText etPhone;
    EditText etAddressSender;
    EditText etAddressDelivery;
    EditText etMessage;
    Button buttonSend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_booking;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_booking, container, false);
        etName = (EditText) view.findViewById(R.id.etName);
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etPhone = (EditText) view.findViewById(R.id.etPhone);
        etAddressSender = (EditText) view.findViewById(R.id.etAddressSender);
        etAddressDelivery = (EditText) view.findViewById(R.id.etAddressDelivery);
        etMessage = (EditText) view.findViewById(R.id.etMessage);
        buttonSend = (Button) view.findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkout( new Order( getHostActivity() )
                            .setCustomerName( etName.getText().toString() )
                            .setEmail( etEmail.getText().toString() )
                            .setPhone( etPhone.getText().toString() )
                            .setAddressDelivery( etAddressDelivery.getText().toString() )
                            .setAddressSender( etAddressSender.getText().toString() )
                            .setMessage( etMessage.getText().toString()) );
            }
        });


        return view;
    }

    private void checkout(Order order) {
        final RequestQueue queue = Volley.newRequestQueue(getHostActivity());

        Gson gson = new Gson();

        String url = ConstantsVeloportApp.URL_SERVER+"/addOrder/"+gson.toJson(order);

        final StringRequest checkoutRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("SEND_ORDER","Result = "+response);
                        VeloportApplication.orderFlagDEBUG = true;
                        getHostActivity().onBackPressed();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("SEND_ORDER", "Error = " + error.getMessage());
            }
        });

        queue.add(checkoutRequest);
    }

}
