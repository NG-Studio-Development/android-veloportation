package ru.veloportation.veloport.ui.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import ru.veloportation.veloport.ConstantsVeloportApp;
import ru.veloportation.veloport.R;
import ru.veloportation.veloport.components.FetchAddressIntentService;
import ru.veloportation.veloport.model.db.Order;
import ru.veloportation.veloport.model.requests.OrderRequest;


public class BookingFragment extends BaseFragment  {

    EditText etName;
    EditText etEmail;
    EditText etPhone;
    EditText etAddressSender;
    EditText etAddressDelivery;
    EditText etMessage;
    Button buttonSend;

    LocationResultReceiver locationResultReceiver;

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
                             final Bundle savedInstanceState) {

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
                startIntentService(etAddressSender.getText().toString(),
                        etAddressDelivery.getText().toString());
                /* checkout(new Order(getHostActivity())
                        .setCustomerName(etName.getText().toString())
                        .setEmail(etEmail.getText().toString())
                        .setPhone(etPhone.getText().toString())
                        .setAddressDelivery(etAddressDelivery.getText().toString())
                        .setAddressSender(etAddressSender.getText().toString())
                        .setMessage(etMessage.getText().toString())); */
            }
        });

        locationResultReceiver = new LocationResultReceiver(new Handler());

        //calculateCost();
        return view;
    }

    /* private void calculateCost() {

        Geocoder geocoder = new Geocoder(getHostActivity(), Locale.getDefault());
        List<Address> list = null;
        try {
            list = geocoder.getFromLocationName(getString(R.string.izvilistaya),99);

        } catch (IOException ex) {
            ex.printStackTrace();
            Log.d("CALCULATE_COST", "Error");
        }


        if (list != null) {
            Address address = list.get(0);
            Log.d("CALCULATE_COST", "Latitude = "+address.getLatitude()+" Longitude = "+address.getLongitude());
        }
    } */

    private void checkout(Order order) {
        final RequestQueue queue = Volley.newRequestQueue(getHostActivity());

        OrderRequest orderCheckoutRequest = OrderRequest.requestCreateOrder(order, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("SEND_ORDER","Result = "+response);
                getHostActivity().onBackPressed();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("SEND_ORDER", "Error = " + error.getMessage());
            }
        } );

        queue.add(orderCheckoutRequest);
    }




    protected void startIntentService(String addressSender, String addressDelivery) {
        final Intent intent = new Intent(getHostActivity(), FetchAddressIntentService.class);
        intent.putExtra(ConstantsVeloportApp.RECEIVER, locationResultReceiver);
        intent.putExtra(ConstantsVeloportApp.SENDER_DATA_EXTRA, addressSender);
        intent.putExtra(ConstantsVeloportApp.DELIVERY_DATA_EXTRA, addressDelivery);

        getHostActivity().getProgressDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                getHostActivity().getProgressDialog().setMessage(getString(R.string.calculate_cost));
                getHostActivity().startService(intent);
            }
        });

        getHostActivity().getProgressDialog().show();
    }



    class LocationResultReceiver extends ResultReceiver {
        public LocationResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            String cost = resultData.getString(ConstantsVeloportApp.RESULT_DATA_KEY);

            if ( resultCode == ConstantsVeloportApp.SUCCESS_RESULT ) {}

            getHostActivity().getProgressDialog().hide();
        }
    }

}
